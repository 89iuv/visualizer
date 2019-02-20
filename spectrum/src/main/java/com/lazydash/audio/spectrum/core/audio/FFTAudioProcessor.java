package com.lazydash.audio.spectrum.core.audio;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.util.fft.FFT;
import be.tarsos.dsp.util.fft.HannWindow;
import be.tarsos.dsp.util.fft.WindowFunction;
import com.lazydash.audio.spectrum.core.algorithm.OctaveGenerator;
import com.lazydash.audio.spectrum.system.config.AppConfig;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
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
    private WindowFunction windowFunction = new HannWindow();
    private double windowCorrectionFactor = 2.00;
    private UnivariateInterpolator interpolator = new LinearInterpolator();

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
        double[] bins = new double[transformBuffer.length / 2];

        FFT fft = new FFT(transformBuffer.length, windowFunction);
        fft.forwardTransform(transformBuffer);
        fft.modulus(transformBuffer, amplitudes);

        bins = IntStream.range(0, bins.length).mapToDouble(i -> fft.binToHz(i, audioFormat.getSampleRate())).toArray();
        double[] doublesAmplitudes = IntStream.range(0, amplitudes.length).mapToDouble(value -> amplitudes[value]).toArray();
        UnivariateFunction interpolate = interpolator.interpolate(bins, doublesAmplitudes);

        double[] octaveBins = new double[octaveFrequencies.size()];
        float[] octaveAmplitudes = new float[octaveFrequencies.size()];

        // m is the position in the octaveFrequency vectors
        int m = 0;
        // k is the position in the amplitudes vector
        int k = 0;
        for (int i = 0; i < octaveFrequencies.size(); i++) {
            octaveBins[m] = octaveFrequencies.get(i);

            // the target octave frequency will be calculated
            // by grouping bins from half right to half left
            // of the target frequency value
            double frequencyHigh;
            if ( i < octaveFrequencies.size() - 1) {
                frequencyHigh = (octaveFrequencies.get(i + 1) + octaveFrequencies.get(i)) / 2;
            } else {
                frequencyHigh = octaveFrequencies.get(i);
            }


            int p = 0;
            // p is number of bins that get grouped under a single target frequency

            // group bins together
            while (bins[k] <= frequencyHigh) {
                // skip k values until we get to the first target frequency
                // otherwise the first target frequency will have all the energy from the first bins
                if (bins[k] < octaveFrequencies.get(0)) {
                    k++;
                    continue;
                }

                float amplitude = (amplitudes[k] / amplitudes.length); // normalize (n/2)
                amplitude = (float) (amplitude * windowCorrectionFactor); // apply window correction
                octaveAmplitudes[m] = octaveAmplitudes[m] + (float) Math.pow(amplitude, 2); // sum up the "normalized window corrected" energy

                k++;
                p++;
            }

            // interpolate if there is to little data
            if (p < 5) {
                double value = interpolate.value(octaveFrequencies.get(i));

                float amplitude = (float) (value / amplitudes.length); // normalize (n/2)
                amplitude = (float) (amplitude * windowCorrectionFactor); // apply window correction
                octaveAmplitudes[m] = (float) Math.pow(amplitude, 2);
            }

            octaveAmplitudes[m] = (float) Math.sqrt(octaveAmplitudes[m]); // square root the energy
            octaveAmplitudes[m] = (float) (Math.sqrt(Math.pow(octaveAmplitudes[m], 2) / 2)); // calculate the RMS of the amplitude
            octaveAmplitudes[m] = (float) (20 * Math.log10(octaveAmplitudes[m])); // convert to logarithmic scale

            m++;
        }

        listenerList.forEach(listener -> listener.frame(octaveBins, octaveAmplitudes));

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
