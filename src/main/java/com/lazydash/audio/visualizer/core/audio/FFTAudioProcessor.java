package com.lazydash.audio.visualizer.core.audio;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.util.fft.FFT;
import be.tarsos.dsp.util.fft.HannWindow;
import be.tarsos.dsp.util.fft.WindowFunction;
import com.lazydash.audio.visualizer.core.algorithm.OctaveGenerator;
import com.lazydash.audio.visualizer.system.config.AppConfig;
import org.apache.commons.math3.analysis.interpolation.NevilleInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;
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
    private NevilleInterpolator nevilleInterpolator = new NevilleInterpolator();

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

        for (int i = 0; i < bins.length; i++) {
            bins[i] = fft.binToHz(i, audioFormat.getSampleRate());
        }

        double[] octaveBins = new double[octaveFrequencies.size()];
        float[] octaveAmplitudes = new float[octaveFrequencies.size()];

        // m is the position in the octaveFrequency vectors
        int m = 0;
        // k is the position in the amplitudes vector
        // ignore first bin as it has the DC component in it
        int k = 1;
        for (int i = 0; i < octaveFrequencies.size(); i++) {
            // the target octave frequency will be calculated
            // by grouping bins from half right to half left
            // of the target frequency value
            double frequencyHigh;
            if ( i < octaveFrequencies.size() - 1) {
                frequencyHigh = (octaveFrequencies.get(i + 1) + octaveFrequencies.get(i)) / 2;
            } else {
                frequencyHigh = octaveFrequencies.get(i);
            }
            octaveBins[m] = octaveFrequencies.get(i);

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

            // interpolate missing value using a 3 point quadratic interpolator
            // http://commons.apache.org/proper/commons-math/userguide/analysis.html
            if (p < 3) {
                double value;
                PolynomialFunctionLagrangeForm interpolate;
                if (k > 3) {
                    // k has been previously incremented past the frequency of interest
                    // and we need to take the 3 points starting from the - 2 position in the bins vector
                    interpolate = nevilleInterpolator.interpolate(new double[]{
                                    bins[k - 2],
                                    bins[k - 1],
                                    bins[k]},
                            new double[]{
                                    amplitudes[k - 2],
                                    amplitudes[k - 1],
                                    amplitudes[k]});
                } else {
                    // we need values that are smaller than the first bin can return
                    // we interpolate from the first 3 bins in this case
                    interpolate = nevilleInterpolator.interpolate(new double[]{
                                    bins[1],
                                    bins[2],
                                    bins[3]},
                            new double[]{
                                    amplitudes[1],
                                    amplitudes[2],
                                    amplitudes[3]});
                }

                value = interpolate.value(octaveFrequencies.get(i));

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
