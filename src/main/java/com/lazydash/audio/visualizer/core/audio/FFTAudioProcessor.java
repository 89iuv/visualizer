package com.lazydash.audio.visualizer.core.audio;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.util.fft.FFT;
import be.tarsos.dsp.util.fft.HannWindow;
import be.tarsos.dsp.util.fft.WindowFunction;
import com.lazydash.audio.visualizer.core.algorithm.OctaveGenerator;
import com.lazydash.audio.visualizer.system.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import java.util.List;


public class FFTAudioProcessor implements AudioProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FFTAudioProcessor.class);
    long oldTime = System.currentTimeMillis();

    private List<FFTListener> listenerList;
    private AudioFormat audioFormat;
    private WindowFunction windowFunction = new HannWindow();
    private double windowCorrectionFactor = 2.00;

    public FFTAudioProcessor(AudioFormat audioFormat, List<FFTListener> listenerList) {
        this.audioFormat = audioFormat;
        this.listenerList = listenerList;
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        int interpolation = AppConfig.getZeroPadding();

        double spl = audioEvent.getdBSPL();
        float[] audioFloatBuffer = audioEvent.getFloatBuffer();

        float[] amplitudes = new float[(audioFloatBuffer.length + interpolation) / 2];

        // the buffer must be copied into another array for processing otherwise strange behaviour
        // the audioFloatBuffer buffer is reused because of the offset
        // modifying it will create strange issues
        float[] transformBuffer = new float[audioFloatBuffer.length + interpolation];
        System.arraycopy(audioFloatBuffer, 0, transformBuffer, 0, audioFloatBuffer.length);

        FFT fft = new FFT(transformBuffer.length, windowFunction);
        fft.forwardTransform(transformBuffer);
        fft.modulus(transformBuffer, amplitudes);

        List<Double> octaveFrequencies = OctaveGenerator.getOctaveFrequencies(
                AppConfig.getFrequencyCenter(),
                AppConfig.getOctave(),
                AppConfig.getFrequencyStart(),
                AppConfig.getFrequencyEnd());

        double[] octaveBins = new double[octaveFrequencies.size() - 1];
        float[] octaveAmplitudes = new float[octaveFrequencies.size() - 1];

        // store and normalize the fft output in map of hz to amplitude
        double[] hzBins = new double[amplitudes.length];
        int m = 0;
        for (int i = 1; i < octaveFrequencies.size(); i++){
            double low = octaveFrequencies.get(i - 1);
            double high = octaveFrequencies.get(i);

            for (int k = 1; k < amplitudes.length; k++) {
                hzBins[k] = fft.binToHz(k, audioFormat.getSampleRate());
                if (low < hzBins[k] && hzBins[k] <= high) {
                    octaveBins[m] = octaveFrequencies.get(i);
                    float amplitude = (float) Math.pow(amplitudes[k], 2); // use the energy for calculations
                    amplitude = (float) (amplitude / amplitudes.length); // normalize
                    amplitude = (float) (amplitude * windowCorrectionFactor); // apply window correction
                    octaveAmplitudes[m] = octaveAmplitudes[m] + amplitude; // sum up the energy
                }
            }

            octaveAmplitudes[m] = (float) Math.sqrt(octaveAmplitudes[m]); // square root the energy
            octaveAmplitudes[m] = (float) (Math.sqrt(Math.pow(octaveAmplitudes[m], 2) / 2)); // calculate the RMS of the amplitude
            octaveAmplitudes[m] = (float) (20 * Math.log10(octaveAmplitudes[m] / 40)); // convert to logarithmic scale

            m++;
        }

        listenerList.forEach(listener -> listener.frame(octaveBins, octaveAmplitudes, spl));

        long newTime = System.currentTimeMillis();
        long deltaTime = newTime - oldTime;
//        System.out.println(String.valueOf(deltaTime));
//        LOGGER.debug(String.valueOf(deltaTime));
        oldTime = newTime;

        return true;
    }

    @Override
    public void processingFinished() {

    }
}
