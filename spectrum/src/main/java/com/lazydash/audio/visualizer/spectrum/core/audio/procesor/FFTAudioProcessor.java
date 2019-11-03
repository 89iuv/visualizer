package com.lazydash.audio.visualizer.spectrum.core.audio.procesor;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.util.fft.FFT;
import be.tarsos.dsp.util.fft.HannWindow;
import be.tarsos.dsp.util.fft.WindowFunction;
import com.lazydash.audio.visualizer.spectrum.core.algorithm.AmplitudeWeightCalculator;
import com.lazydash.audio.visualizer.spectrum.core.algorithm.OctaveGenerator;
import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;
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

    private List<FFTListener> listenerList;
    private AudioFormat audioFormat;
    private FFT fft;
    private double[] bins;

    private UnivariateInterpolator interpolator = new LinearInterpolator();
    private WindowFunction windowFunction = new HannWindow();
    private float windowCorrectionFactor = 2.00f;

    public FFTAudioProcessor(AudioFormat audioFormat, List<FFTListener> listenerList) {
        this.audioFormat = audioFormat;
        this.listenerList = listenerList;

        int bitsPerOneMs = (int) ((audioFormat.getSampleRate() * audioFormat.getChannels() * audioFormat.getSampleSizeInBits()) / 8) / 1000;
        int bufferSize = (bitsPerOneMs * AppConfig.getFftWindowMs() * AppConfig.getFftWindowFrames()) / AppConfig.getChannels();
        int floatSize = bufferSize / AppConfig.getChannels();

        fft = new FFT(floatSize, windowFunction);
        bins = IntStream.range(0, floatSize / 2).mapToDouble(i -> fft.binToHz(i, audioFormat.getSampleRate())).toArray();
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        float[] transformBuffer = audioEvent.getFloatBuffer();
        float[] amplitudes = new float[transformBuffer.length / 2];

        fft.forwardTransform(transformBuffer);
        fft.modulus(transformBuffer, amplitudes);

        double[] doublesAmplitudes = IntStream.range(0, amplitudes.length).mapToDouble(value -> {
            float amplitude = amplitudes[value];
            amplitude = (amplitude / amplitudes.length); // normalize (n/2)
            amplitude = (amplitude * windowCorrectionFactor); // apply window correction
            return amplitude;
        }).toArray();


        int[] frequencyBins;
        double[] frequencyAmplitudes;

        if (AppConfig.getOctave() > 0) {
            // method is cached internally for performance reasons
            int[] octaveFrequencies = OctaveGenerator.getOctaveFrequencies(
                    AppConfig.getFrequencyCenter(),
                    AppConfig.getOctave(),
                    AppConfig.getFrequencyStart(),
                    AppConfig.getFrequencyEnd());

            frequencyBins = new int[octaveFrequencies.length];
            frequencyAmplitudes = new double[octaveFrequencies.length];

            double k = OctaveGenerator.getLowLimit(octaveFrequencies[0], AppConfig.getOctave());
            int step = 1;
            int m = 0;

            // interpolate amplitudes
            UnivariateFunction interpolateFunction = interpolator.interpolate(bins, doublesAmplitudes);

            for (int i = 0; i < octaveFrequencies.length; i++) {
                frequencyBins[m] = octaveFrequencies[i];
                double highLimit = OctaveGenerator.getHighLimit(octaveFrequencies[i], AppConfig.getOctave());

                // group bins together
                while (k < highLimit) {
                    frequencyAmplitudes[m] = frequencyAmplitudes[m] + Math.pow(interpolateFunction.value(k), 2); // sum up the energy

                    k = k + step;

                    if (k > AppConfig.getFrequencyEnd() || k > bins[bins.length - 1]) {
                        break;
                    }
                }

                frequencyAmplitudes[m] = Math.sqrt(frequencyAmplitudes[m]); // square root the energy
                frequencyAmplitudes[m] = (20 * Math.log10(frequencyAmplitudes[m])); // convert to logarithmic scale

                AmplitudeWeightCalculator.WeightWindow weightWindow = AmplitudeWeightCalculator.WeightWindow.valueOf(AppConfig.getWeight());
                frequencyAmplitudes[m] = (frequencyAmplitudes[m] + AmplitudeWeightCalculator.getDbWeight(frequencyBins[m], weightWindow)); // use weight to adjust the spectrum

                m++;
            }

        } else {
            int n = 0;
            for (int i = 0; i < bins.length; i++) {
                double frequency = fft.binToHz(i, audioFormat.getSampleRate());
                if (AppConfig.getFrequencyStart() <= frequency && frequency <= AppConfig.getFrequencyEnd()) {
                    n++;
                } else if (frequency > AppConfig.getFrequencyEnd()) {
                    break;
                }
            }

            frequencyBins = new int[n];
            frequencyAmplitudes = new double[n];

            int m = 0;
            for (int i = 0; i < bins.length; i++) {
                double frequency = bins[i];
                if (AppConfig.getFrequencyStart() <= frequency && frequency <= AppConfig.getFrequencyEnd()) {
                    frequencyBins[m] = (int) Math.round(frequency);
                    frequencyAmplitudes[m] = (20 * Math.log10(doublesAmplitudes[i])); // convert to logarithmic scale

                    AmplitudeWeightCalculator.WeightWindow weightWindow = AmplitudeWeightCalculator.WeightWindow.valueOf(AppConfig.getWeight());
                    frequencyAmplitudes[m] = (frequencyAmplitudes[m] + AmplitudeWeightCalculator.getDbWeight(frequencyBins[m], weightWindow)); // use weight to adjust the spectrum
                    m++;
                }
            }
        }

        listenerList.forEach(listener -> listener.frame(frequencyBins, frequencyAmplitudes));

        return true;
    }

    @Override
    public void processingFinished() {

    }
}
