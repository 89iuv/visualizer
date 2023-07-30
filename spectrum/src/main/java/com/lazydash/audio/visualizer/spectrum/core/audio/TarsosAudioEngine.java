package com.lazydash.audio.visualizer.spectrum.core.audio;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.MultichannelToMono;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
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
            AudioFormat audioFormat = getAudioFormat();

            float sampleRate = audioFormat.getSampleRate();
            int audioWindowSize = AppConfig.audioWindowSize;
            int audioWindowNumber = AppConfig.audioWindowNumber;

            float buffer = sampleRate * (audioWindowSize / 1000f);
            int bufferMax = (int) buffer * audioWindowNumber;
            int bufferOverlap = bufferMax - (int) buffer;

            TargetDataLine line = getLine(audioFormat, bufferMax);
            run(line, audioFormat, bufferMax, bufferOverlap);

        } catch (LineUnavailableException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public void stop() {
        try {
            if (dispatcher != null) {
                dispatcher.stop();

                // wait 5 seconds for audio dispatcher to finish
                audioThread.join(1 * 1000);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void restart() {
        stop();
        start();
    }

    private TargetDataLine getLine(AudioFormat audioFormat, int lineBuffer) throws LineUnavailableException {
        TargetDataLine targetDataLine;
        try {
                targetDataLine = getLineFromConfig(audioFormat, lineBuffer);

        } catch (Exception e) {
            LOGGER.info("Unable to get the audio line from config file");
            targetDataLine = getFirstLineAvailable(audioFormat, lineBuffer);
        }

        return targetDataLine;
    }

    private TargetDataLine getLineFromConfig(AudioFormat audioFormat, int lineBuffer) throws LineUnavailableException {
        //noinspection OptionalGetWithoutIsPresent
        Mixer.Info mixerInfo = Stream.of(AudioSystem.getMixerInfo())
                .filter(curentMixerInfo -> curentMixerInfo.getName().equals(AppConfig.inputDevice))
                .findFirst()
                .get();
        Mixer mixer = AudioSystem.getMixer(mixerInfo);
        LOGGER.info("mixer info: " + mixerInfo);

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

    private TargetDataLine getFirstLineAvailable(AudioFormat audioFormat, int lineBuffer) throws LineUnavailableException {
        TargetDataLine line;

        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            for (Line.Info mixerLineInfo : mixer.getTargetLineInfo()) {
                try {
                    // try to get access to the mixer line
                    // if successful ad the mixer name to the list
                    line = (TargetDataLine) mixer.getLine(mixerLineInfo);
                    line.open(audioFormat, lineBuffer);
                    line.start();

                    AppConfig.inputDevice = mixerInfo.getName();

                    LOGGER.info("mixer info: " + mixerInfo);
                    LOGGER.info("line format: " + line.getFormat());
                    LOGGER.info("line info: " + line.getLineInfo());
                    LOGGER.info("line bufferSize size: " + line.getBufferSize());

                    return line;

                } catch (Exception e) {
                    // skip mixer if line can not be obtained
                }
            }
        }

        throw new LineUnavailableException("No available input line found");
    }

    private AudioFormat getAudioFormat() {
        return new AudioFormat(
                AppConfig.sampleRate,
                AppConfig.sampleSizeInBits,
                AppConfig.channels,
                AppConfig.signed,
                AppConfig.bigEndian
        );
    }

    private void run(TargetDataLine line, AudioFormat audioFormat, int bufferSize, int bufferOverlay) {
        final AudioInputStream stream = new AudioInputStream(line);
        JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);

        dispatcher = new AudioDispatcher(audioStream, bufferSize, bufferOverlay);
        dispatcher.addAudioProcessor(new AudioEngineRestartProcessor(this));
//        dispatcher.addAudioProcessor(new MultichannelToMono(audioFormat.getChannels(), true));
        dispatcher.addAudioProcessor(new FFTAudioProcessor(audioFormat, fttListenerList));

        // run the dispatcher (on a new thread).
        audioThread = new Thread(dispatcher, "Audio dispatching");
        audioThread.setPriority(Thread.MAX_PRIORITY);
        audioThread.setDaemon(true);
        audioThread.start();
    }
}
