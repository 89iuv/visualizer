package com.lazydash.audio.visualizer.core.audio;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.util.fft.FFT;
import be.tarsos.dsp.util.fft.HannWindow;
import be.tarsos.dsp.util.fft.WindowFunction;
import com.lazydash.audio.visualizer.core.algorithm.OctaveGenerator;
import com.lazydash.audio.visualizer.system.config.AppConfig;

import javax.sound.sampled.AudioFormat;
import java.util.List;


public class FFTAudioProcessor implements AudioProcessor {
    private List<FFTListener> listenerList;
    private AudioFormat audioFormat;
    private WindowFunction windowFunction = new HannWindow();
    private double windowCorrectionFactor = 2.00;

    FFTAudioProcessor(AudioFormat audioFormat, List<FFTListener> listenerList) {
        this.audioFormat = audioFormat;
        this.listenerList = listenerList;
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        int interpolation = AppConfig.getZeroPadding();
        List<Double> octaveFrequencies = OctaveGenerator.getOctaveFrequencies(
                AppConfig.getFrequencyCenter(),
                AppConfig.getOctave(),
                AppConfig.getFrequencyStart(),
                AppConfig.getFrequencyEnd());

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

        double[] octaveBins = new double[octaveFrequencies.size()];
        float[] octaveAmplitudes = new float[octaveFrequencies.size()];

        // skip bin 0 and start from bin 1
        int k = 1;

        // skip till the minimum frequency
        while (fft.binToHz(k, audioFormat.getSampleRate()) < octaveFrequencies.get(0)) {
            k++;
        }
        k--;

        // m is the position int the octaveFrequency vectors
        int m = 0;
        for (int i = 0; i < octaveFrequencies.size(); i++){
            double frequencyHigh;
            if ( i < octaveFrequencies.size() - 1) {
                frequencyHigh = (octaveFrequencies.get(i + 1) + octaveFrequencies.get(i)) / 2;
            } else {
                frequencyHigh = octaveFrequencies.get(i);
            }

            // group bins together
            while (fft.binToHz(k, audioFormat.getSampleRate()) <= frequencyHigh) {
                octaveBins[m] = octaveFrequencies.get(i);
                float amplitude = (float) (amplitudes[k] / amplitudes.length); // normalize (n/2)
                amplitude = (float) (amplitude * windowCorrectionFactor); // apply window correction
                octaveAmplitudes[m] = octaveAmplitudes[m] + (float) Math.pow(amplitude, 2); // sum up the "normalized window corrected" energy

                k++;
            }

            octaveAmplitudes[m] = (float) Math.sqrt(octaveAmplitudes[m]); // square root the energy
            octaveAmplitudes[m] = (float) (Math.sqrt(Math.pow(octaveAmplitudes[m], 2) / 2)); // calculate the RMS of the amplitude
            octaveAmplitudes[m] = (float) (20 * Math.log10(octaveAmplitudes[m])); // convert to logarithmic scale

            m++;
        }

        listenerList.forEach(listener -> listener.frame(octaveBins, octaveAmplitudes));

        return true;
    }

    @Override
    public void processingFinished() {

    }
}
