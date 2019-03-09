package com.lazydash.audio.spectrum.core.audio;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.util.fft.FFT;
import be.tarsos.dsp.util.fft.HannWindow;
import be.tarsos.dsp.util.fft.WindowFunction;
import com.lazydash.audio.spectrum.core.algorithm.AmplitudeWeightCalculator;
import com.lazydash.audio.spectrum.core.algorithm.OctaveGenerator;
import com.lazydash.audio.spectrum.system.config.AppConfig;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import java.util.List;
import java.util.stream.IntStream;


public class FFTAudioProcessor implements AudioProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FFTAudioProcessor.class);
    long oldTime = System.currentTimeMillis();

    private List<FFTListener> listenerList;
    private AudioFormat audioFormat;
    private UnivariateInterpolator interpolator = new SplineInterpolator();
    private WindowFunction windowFunction = new HannWindow();
    private double windowCorrectionFactor = 2.00;

    FFTAudioProcessor(AudioFormat audioFormat, List<FFTListener> listenerList) {
        this.audioFormat = audioFormat;
        this.listenerList = listenerList;
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        int interpolation = AppConfig.getZeroPadding();

        float[] audioFloatBuffer = audioEvent.getFloatBuffer();

        // the buffer must be copied into another array for processing otherwise strange behaviour
        // the audioFloatBuffer buffer is reused because of the offset
        // modifying it will create strange issues
        float[] transformBuffer = new float[audioFloatBuffer.length + interpolation];
        System.arraycopy(audioFloatBuffer, 0, transformBuffer, 0, audioFloatBuffer.length);

        float[] amplitudes = new float[transformBuffer.length / 2];

        FFT fft = new FFT(transformBuffer.length, windowFunction);
        fft.forwardTransform(transformBuffer);
        fft.modulus(transformBuffer, amplitudes);

        double[] bins = IntStream.range(0, transformBuffer.length / 2).mapToDouble(i -> fft.binToHz(i, audioFormat.getSampleRate())).toArray();
        double[] doublesAmplitudes = IntStream.range(0, amplitudes.length).mapToDouble(value -> amplitudes[value]).toArray();

        double[] frequencyBins;
        double[] frequencyAmplitudes;

        if (AppConfig.getOctave() > 0) {
            List<Double> octaveFrequencies = OctaveGenerator.getOctaveFrequencies(
                    AppConfig.getFrequencyCenter(),
                    AppConfig.getOctave(),
                    AppConfig.getFrequencyStart(),
                    AppConfig.getFrequencyEnd());

            frequencyBins = new double[octaveFrequencies.size()];
            frequencyAmplitudes = new double[octaveFrequencies.size()];

            // calculate the frequency step
            // this is the resolution for interpolating and summing bins
            double highLimit = OctaveGenerator.getHighLimit(octaveFrequencies.get(0), AppConfig.getOctave());
            double lowLimit = OctaveGenerator.getLowLimit(octaveFrequencies.get(0), AppConfig.getOctave());
            double step = Math.pow(2, ( 1d / (AppConfig.getOctave()) ));

            // improve resolution at the cost of performance
            // step = step / (AppConfig.getOctave() / 2d);

            // k is the frequency index
            double k = lowLimit;

            // m is the position in the frequency vectors
            int m = 0;

            // setup the interpolator
            UnivariateFunction interpolateFunction = interpolator.interpolate(bins, doublesAmplitudes);

            for (int i = 0; i < octaveFrequencies.size(); i++) {
                frequencyBins[m] = octaveFrequencies.get(i);

                highLimit = OctaveGenerator.getHighLimit(octaveFrequencies.get(i), AppConfig.getOctave());

                // group bins together
                while (k < highLimit) {

                    double amplitude = interpolateFunction.value(k);
                    amplitude = (amplitude / doublesAmplitudes.length); // normalize (n/2)
                    amplitude = (amplitude * windowCorrectionFactor); // apply window correction
                    frequencyAmplitudes[m] = frequencyAmplitudes[m] + Math.pow(amplitude, 2); // sum up the "normalized window corrected" energy

                    k = k + step;

                    // reached upper limit
                    if (k > AppConfig.getFrequencyEnd() || k > bins[bins.length-1]) {
                        break;
                    }
                }

                frequencyAmplitudes[m] = Math.sqrt(frequencyAmplitudes[m]); // square root the energy

                if (AppConfig.getMaxLevel().equals("RMS")) {
                    frequencyAmplitudes[m] = (Math.sqrt(Math.pow(frequencyAmplitudes[m], 2) / 2)); // calculate the RMS of the amplitude
                }
                frequencyAmplitudes[m] = (20 * Math.log10(frequencyAmplitudes[m])); // convert to logarithmic scale

                AmplitudeWeightCalculator.WeightWindow weightWindow = AmplitudeWeightCalculator.WeightWindow.valueOf(AppConfig.getWeight());
                frequencyAmplitudes[m] = (frequencyAmplitudes[m] + AmplitudeWeightCalculator.getDbWeight(frequencyBins[m], weightWindow)); // use weight to adjust the spectrum

                m++;
            }

        } else {
            int n = 0;
            for (int i = 0; i < bins.length; i++) {
                double frequency = fft.binToHz(i, audioFormat.getSampleRate());
                if ( AppConfig.getFrequencyStart() <= frequency && frequency <= AppConfig.getFrequencyEnd() ) {
                    n++;
                } else if (frequency > AppConfig.getFrequencyEnd()){
                    break;
                }
            }

            frequencyBins = new double[n];
            frequencyAmplitudes = new double[n];

            int m = 0;
            for (int i = 0; i < bins.length; i++) {
                double frequency = fft.binToHz(i, audioFormat.getSampleRate());
                if (AppConfig.getFrequencyStart() <= frequency && frequency <= AppConfig.getFrequencyEnd()) {
                    frequencyBins[m] = frequency;

                    frequencyAmplitudes[m] = doublesAmplitudes[i];
                    frequencyAmplitudes[m] = (frequencyAmplitudes[m] / doublesAmplitudes.length); // normalize (n/2)
                    frequencyAmplitudes[m] = (frequencyAmplitudes[m] * windowCorrectionFactor); // apply window correction

                    if (AppConfig.getMaxLevel().equals("RMS")) {
                        frequencyAmplitudes[m] = (Math.sqrt(Math.pow(frequencyAmplitudes[m], 2) / 2)); // calculate the RMS of the amplitude
                    }
                    frequencyAmplitudes[m] = (20 * Math.log10(frequencyAmplitudes[m])); // convert to logarithmic scale

                    AmplitudeWeightCalculator.WeightWindow weightWindow = AmplitudeWeightCalculator.WeightWindow.valueOf(AppConfig.getWeight());
                    frequencyAmplitudes[m] = (frequencyAmplitudes[m] + AmplitudeWeightCalculator.getDbWeight(frequencyBins[m], weightWindow)); // use weight to adjust the spectrum

                    m++;
                }
            }
        }

        listenerList.forEach(listener -> listener.frame(frequencyBins, frequencyAmplitudes));

        long newTime = System.currentTimeMillis();
        long deltaTime = newTime - oldTime;
//        LOGGER.info(String.valueOf(deltaTime));
        oldTime = newTime;

        return true;
    }

    @Override
    public void processingFinished() {

    }
}
