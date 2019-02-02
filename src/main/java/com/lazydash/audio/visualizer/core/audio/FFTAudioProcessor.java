package com.lazydash.audio.visualizer.core.audio;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.util.fft.FFT;
import be.tarsos.dsp.util.fft.HammingWindow;
import be.tarsos.dsp.util.fft.WindowFunction;
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
    private WindowFunction windowFunction = new HammingWindow();

    public FFTAudioProcessor(AudioFormat audioFormat, List<FFTListener> listenerList) {
        this.audioFormat = audioFormat;
        this.listenerList = listenerList;
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        double spl = audioEvent.getdBSPL();
        float[] audioFloatBuffer = audioEvent.getFloatBuffer();

        float[] amplitudes = new float[audioFloatBuffer.length/2];

        // the buffer must be copied into another array for processing otherwise strange behaviour
        // the audioFloatBuffer buffer is reused because of the offset
        // modifying it will create strange issues
        float[] transformBuffer = new float[audioFloatBuffer.length];
        System.arraycopy(audioFloatBuffer, 0, transformBuffer, 0, audioFloatBuffer.length);

        FFT fft = new FFT(transformBuffer.length, windowFunction);
        fft.forwardTransform(transformBuffer);
        fft.modulus(transformBuffer, amplitudes);

        // store and normalize the fft output in map of hz to amplitude
        double[] hzBins = new double[amplitudes.length];
        for (int i = 0; i < amplitudes.length; i++) {
            hzBins[i] = fft.binToHz(i, audioFormat.getSampleRate());
            amplitudes[i] = (float) (20 * Math.log10((amplitudes[i]  / amplitudes.length) * 1.85)); // with window correction factor
        }

        double[] truncatedBins = new double[AppConfig.getBarNumber()];
        System.arraycopy(hzBins, AppConfig.getBarOffset(), truncatedBins, 0, truncatedBins.length);

        float[] truncatedAmplitudes = new float[AppConfig.getBarNumber()];
        System.arraycopy(amplitudes, AppConfig.getBarOffset(), truncatedAmplitudes, 0, truncatedAmplitudes.length);


        listenerList.forEach(listener -> listener.frame(truncatedBins, truncatedAmplitudes, spl));

        long newTime = System.currentTimeMillis();
        long deltaTime = newTime - oldTime;
//        LOGGER.trace(String.valueOf(deltaTime));
        oldTime = newTime;

        return true;
    }

    @Override
    public void processingFinished() {

    }
}
