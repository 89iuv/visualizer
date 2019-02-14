package com.lazydash.audio.spectrum.core.audio;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import com.lazydash.audio.spectrum.system.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class TarsosAudioEngine {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private AudioDispatcher dispatcher;
    private Thread audioThread;

    private List<FFTListener> fttListenerList = new LinkedList<>();

    public List<FFTListener> getFttListenerList() {
        return fttListenerList;
    }

    public void start() {
        try {
            Mixer mixer = getMixer();
            AudioFormat audioFormat = getAudioFormat();
            TargetDataLine line = getLine(mixer, audioFormat, AppConfig.getBufferSize());
            run(line, audioFormat, AppConfig.getBufferSize(), AppConfig.getBufferOverlap());

        } catch (LineUnavailableException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public void stop() {
        try {
            dispatcher.stop();
            // wait 5 seconds for audio dispatcher to finish
            audioThread.join(1 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void restart() {
        stop();
        start();
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

    private TargetDataLine getLine(Mixer mixer, AudioFormat audioFormat, int lineBuffer) throws LineUnavailableException {
        //noinspection OptionalGetWithoutIsPresent
        Line.Info lineInfo = Stream.of(mixer.getTargetLineInfo()).findFirst().get();
        TargetDataLine line = (TargetDataLine) mixer.getLine(lineInfo);
        line.open(audioFormat, lineBuffer);
        line.start();

        LOGGER.info("line format: " + line.getFormat());
        LOGGER.info("line info: " + line.getLineInfo());
        LOGGER.info("line bufferSize size: " + line.getBufferSize());

        return line;
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

    private void run(TargetDataLine line, AudioFormat audioFormat, int bufferSize, int bufferOverlay) {
        final AudioInputStream stream = new AudioInputStream(line);
        JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);

        dispatcher = new AudioDispatcher(audioStream, bufferSize, bufferOverlay);
        dispatcher.addAudioProcessor(new AudioEngineRestartProcessor(this));
        dispatcher.addAudioProcessor(new FFTAudioProcessor(audioFormat, fttListenerList));

        // run the dispatcher (on a new thread).
        audioThread = new Thread(dispatcher, "Audio dispatching");
        audioThread.setDaemon(true);
        audioThread.start();
    }
}
