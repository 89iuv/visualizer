package com.lazydash.audio.visualizer.spectrum.core.audio;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.util.fft.FFT;
import be.tarsos.dsp.util.fft.HannWindow;
import be.tarsos.dsp.util.fft.WindowFunction;
import com.lazydash.audio.visualizer.spectrum.core.algorithm.AmplitudeWeightCalculator;
import com.lazydash.audio.visualizer.spectrum.core.algorithm.OctaveGenerator;
import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;


public class FFTAudioProcessor implements AudioProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FFTAudioProcessor.class);

    private List<FFTListener> listenerList;
    private AudioFormat audioFormat;
    private UnivariateInterpolator interpolator = new SplineInterpolator();
    private WindowFunction windowFunction = new HannWindow();
    private double windowCorrectionFactor = 2.00;

    private LinkedList<float[]> fftWindowFrameList = new LinkedList<>();

    FFTAudioProcessor(AudioFormat audioFormat, List<FFTListener> listenerList) {
        this.audioFormat = audioFormat;
        this.listenerList = listenerList;
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        long t0 = System.currentTimeMillis();

        float[] floatBuffer = audioEvent.getFloatBuffer();

        // the buffer must be copied into another array for processing otherwise strange behaviour
        // the audioFloatBuffer buffer is reused because of the offset
        // modifying it will create strange issues
        float[] copiedFloatBuffer = new float[floatBuffer.length];
        System.arraycopy(floatBuffer, 0, copiedFloatBuffer, 0, floatBuffer.length);

        if (fftWindowFrameList.size() < AppConfig.audioWindowNumber) {
            fftWindowFrameList.addLast(copiedFloatBuffer);
        }

        float[] transformBuffer = new float[copiedFloatBuffer.length * fftWindowFrameList.size()];

        for (int i = 0; i < fftWindowFrameList.size(); i++) {
            float[] fftWindowFrame = fftWindowFrameList.get(i);
            System.arraycopy(fftWindowFrame, 0, transformBuffer, fftWindowFrame.length * i, fftWindowFrame.length);
        }

        if (fftWindowFrameList.size() >= AppConfig.audioWindowNumber) {
            fftWindowFrameList.removeFirst();
        }

        float[] amplitudes = new float[transformBuffer.length / 2];

        FFT fft = new FFT(transformBuffer.length, windowFunction);
        fft.forwardTransform(transformBuffer);
        fft.modulus(transformBuffer, amplitudes);

        double[] bins = IntStream.range(0, transformBuffer.length / 2).mapToDouble(i -> fft.binToHz(i, audioFormat.getSampleRate())).toArray();
        double[] doublesAmplitudes = IntStream.range(0, amplitudes.length).mapToDouble(value -> {
            float amplitude = amplitudes[value];
            amplitude = (amplitude / amplitudes.length); // normalize (n/2)
            amplitude = (amplitude * (float) windowCorrectionFactor); // apply window correction
            return amplitude;
        }).toArray();

        List<Double> octaveFrequencies = OctaveGenerator.getOctaveFrequencies(
                AppConfig.frequencyCenter,
                AppConfig.octave,
                AppConfig.frequencyStart,
                AppConfig.frequencyEnd);

        double[] frequencyBins = new double[octaveFrequencies.size()];
        double[] frequencyAmplitudes = new double[octaveFrequencies.size()];

        UnivariateFunction interpolateFunction = interpolator.interpolate(bins, doublesAmplitudes);

        int m = 0; // m is the position in the frequency vectors
        for (int i = 0; i < octaveFrequencies.size(); i++) {
            // get frequency bin
            frequencyBins[m] = octaveFrequencies.get(i);

            double highLimit = OctaveGenerator.getHighLimit(octaveFrequencies.get(i), AppConfig.octave);
            double lowLimit = OctaveGenerator.getLowLimit(octaveFrequencies.get(i), AppConfig.octave);

            double step = 1;
            double k = lowLimit;

            // group amplitude together in frequency bin
            while (k < highLimit) {
                double amplitude = interpolateFunction.value(k);
                frequencyAmplitudes[m] = frequencyAmplitudes[m] + Math.pow(amplitude, 2); // sum up the "normalized window corrected" energy
                k = k + step;
            }

            frequencyAmplitudes[m] = Math.sqrt(frequencyAmplitudes[m]); // square root the energy
            frequencyAmplitudes[m] = AppConfig.maxLevel.equals("RMS") ? Math.sqrt(Math.pow(frequencyAmplitudes[m], 2) / 2) : frequencyAmplitudes[m]; // calculate the RMS of the amplitude
            frequencyAmplitudes[m] = (20 * Math.log10(frequencyAmplitudes[m])); // convert to logarithmic scale

            AmplitudeWeightCalculator.WeightWindow weightWindow = AmplitudeWeightCalculator.WeightWindow.valueOf(AppConfig.weight);
            frequencyAmplitudes[m] = (frequencyAmplitudes[m] + AmplitudeWeightCalculator.getDbWeight(frequencyBins[m], weightWindow)); // use weight to adjust the spectrum

            m++;
        }

        listenerList.forEach(listener -> listener.frame(frequencyBins, frequencyAmplitudes));

        long t1 = System.currentTimeMillis();
        long dt = t1 - t0;
        //LOGGER.info("benchmark - fft computation: " + String.valueOf(dt));

        return true;
    }

    @Override
    public void processingFinished() {

    }
}
