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
    private long oldTime = System.currentTimeMillis();

    private List<FFTListener> listenerList;
    private AudioFormat audioFormat;
    private  FFT fft;

    private UnivariateInterpolator interpolator = new LinearInterpolator();
    private WindowFunction windowFunction = new HannWindow();
    private double windowCorrectionFactor = 2.00;

    public FFTAudioProcessor(AudioFormat audioFormat, List<FFTListener> listenerList) {
        this.audioFormat = audioFormat;
        this.listenerList = listenerList;

        int bitsPerOneMs = (int) ((audioFormat.getSampleRate() * audioFormat.getChannels() * audioFormat.getSampleSizeInBits()) / 8 ) / 1000;
        int bufferSize = (bitsPerOneMs * AppConfig.getFftWindowMs() * AppConfig.getFftWindowFrames()) / AppConfig.getChannels();
        int floatSize = bufferSize / AppConfig.getChannels();

        fft =  new FFT(floatSize, windowFunction);
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        float[] transformBuffer = audioEvent.getFloatBuffer();
        float[] amplitudes = new float[transformBuffer.length / 2];

        fft.forwardTransform(transformBuffer);
        fft.modulus(transformBuffer, amplitudes);

        double[] bins = IntStream.range(0, transformBuffer.length / 2).mapToDouble(i -> fft.binToHz(i, audioFormat.getSampleRate())).toArray();
        double[] doublesAmplitudes = IntStream.range(0, amplitudes.length).mapToDouble(value -> {
            // make the dc component equal to 0 to improve interpolator performance
            if (value < 1) {
                return 0;
            } else {
                return amplitudes[value];
            }
        }).toArray();
        double[] doublesEnergy = IntStream.range(0, amplitudes.length).mapToDouble(value -> {
            // make the dc component equal to 0 to improve interpolator performance
            if (value < 1) {
                return 0;
            } else {
                return Math.pow(amplitudes[value], 2);
            }
        }).toArray();

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

            double k = OctaveGenerator.getLowLimit(octaveFrequencies.get(0), AppConfig.getOctave());
            double step = 6d / AppConfig.getOctave(); // higher octave require higher precision
            int m = 0;

            UnivariateFunction interpolateFunction = interpolator.interpolate(bins, doublesEnergy);

            for (int i = 0; i < octaveFrequencies.size(); i++) {
                frequencyBins[m] = octaveFrequencies.get(i);
                double highLimit = OctaveGenerator.getHighLimit(octaveFrequencies.get(i), AppConfig.getOctave());

                // group bins together
                while (k < highLimit) {
                    frequencyAmplitudes[m] = frequencyAmplitudes[m] + interpolateFunction.value(k); // sum up the energy
                    k = k + step;

                    if (k > AppConfig.getFrequencyEnd() || k > bins[bins.length-1]) {
                        break;
                    }
                }

                frequencyAmplitudes[m] = Math.sqrt(frequencyAmplitudes[m]); // square root the energy
                frequencyAmplitudes[m] = (frequencyAmplitudes[m] / doublesAmplitudes.length); // normalize (n/2)
                frequencyAmplitudes[m] = (frequencyAmplitudes[m] * windowCorrectionFactor); // apply window correction
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
