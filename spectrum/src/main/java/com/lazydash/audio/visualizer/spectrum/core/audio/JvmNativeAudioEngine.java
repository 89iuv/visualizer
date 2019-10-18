package com.lazydash.audio.visualizer.spectrum.core.audio;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.MultichannelToMono;
import be.tarsos.dsp.io.TarsosDSPAudioFloatConverter;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class JvmNativeAudioEngine implements AudioEngine {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private List<FFTListener> fttListenerList = new LinkedList<>();

    @Override
    public List<FFTListener> getFttListenerList() {
        return fttListenerList;
    }

    @Override
    public void start() {
        try {
            Mixer mixer = getMixer();
            AudioFormat audioFormat = getAudioFormat();
            TargetDataLine line = getLine(mixer, audioFormat, AppConfig.getBufferSize(), AppConfig.getBufferOverlap());
            run(line, audioFormat, AppConfig.getBufferSize(), AppConfig.getBufferOverlap());

        } catch (LineUnavailableException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void restart() {

    }

    private void run(TargetDataLine line, AudioFormat audioFormat, int bufferSize, int bufferOverlay) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(
                        AppConfig.isSigned() ? TarsosDSPAudioFormat.Encoding.PCM_SIGNED : TarsosDSPAudioFormat.Encoding.PCM_UNSIGNED,
                        getAudioFormat().getSampleRate(),
                        getAudioFormat().getSampleSizeInBits(),
                        getAudioFormat().getChannels(),
                        getAudioFormat().getFrameSize(),
                        getAudioFormat().getFrameRate(),
                        getAudioFormat().isBigEndian()
                );

                TarsosDSPAudioFloatConverter converter = TarsosDSPAudioFloatConverter.getConverter(format);

                AudioEvent audioEvent = new AudioEvent(format);
                FFTAudioProcessor fftAudioProcessor = new FFTAudioProcessor(getAudioFormat(), fttListenerList);
                MultichannelToMono multichannelToMono = new MultichannelToMono(format.getChannels(), true);

                byte[] bytes = new byte[bufferSize];
                byte[] tmpBytes = new byte[bufferSize];
                byte[] readBytes = new byte[bufferOverlay];
                float[] floats = new float[bufferSize / getAudioFormat().getChannels()];
                while (true) {
                    line.read(readBytes, 0, readBytes.length);

                    System.arraycopy(bytes, bufferOverlay, tmpBytes, 0, bufferSize - bufferOverlay);
                    System.arraycopy(readBytes, 0, tmpBytes, bufferSize - bufferOverlay, bufferOverlay);
                    System.arraycopy(tmpBytes, 0, bytes, 0, bufferSize);


                    converter.toFloatArray(bytes, floats);

                    audioEvent.setFloatBuffer(floats);

                    multichannelToMono.process(audioEvent);
                    fftAudioProcessor.process(audioEvent);

                }
            }
        });

        thread.setDaemon(true);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    private AudioFormat getAudioFormat() {
        return new AudioFormat(
                AppConfig.getSampleRate(),
                AppConfig.getSampleSizeInBits(),
                AppConfig.getChannels(),
                AppConfig.isSigned(),
                AppConfig.isBigEndian()
        );
    }

    private Mixer getMixer() {
        //noinspection OptionalGetWithoutIsPresent
        Mixer.Info mixerInfo = Stream.of(AudioSystem.getMixerInfo())
                .filter(curentMixerInfo -> curentMixerInfo.getName().contains(AppConfig.getInputDevice()))
                .findFirst()
                .get();
        LOGGER.info("mixer info: " + mixerInfo);
        return AudioSystem.getMixer(mixerInfo);
    }

    private TargetDataLine getLine(Mixer mixer, AudioFormat audioFormat, int lineBuffer, int bufferOverlay) throws LineUnavailableException {
        //noinspection OptionalGetWithoutIsPresent
        Line.Info lineInfo = Stream.of(mixer.getTargetLineInfo()).findFirst().get();
        TargetDataLine line = (TargetDataLine) mixer.getLine(lineInfo);
        line.open(audioFormat, bufferOverlay * 2);
        line.start();

        LOGGER.info("line format: " + line.getFormat());
        LOGGER.info("line info: " + line.getLineInfo());
        LOGGER.info("line bufferSize size: " + line.getBufferSize());

        return line;
    }
}
