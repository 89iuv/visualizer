package com.lazydash.audio.visualizer.spectrum.core.audio.engine;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.MultichannelToMono;
import be.tarsos.dsp.io.TarsosDSPAudioFloatConverter;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import com.lazydash.audio.visualizer.spectrum.core.audio.procesor.FFTAudioProcessor;
import com.lazydash.audio.visualizer.spectrum.core.audio.procesor.FFTListener;
import com.lazydash.audio.visualizer.spectrum.core.audio.procesor.RestartProcessor;
import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class JvmAudioEngine implements AudioEngine {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private TargetDataLine targetLine;
    private SourceDataLine sourceLine;

    private Thread thread;
    private boolean run;

    private List<FFTListener> fttListenerList = new LinkedList<>();

    @Override
    public List<FFTListener> getFttListenerList() {
        return fttListenerList;
    }

    @Override
    public void start() {
        try {
            AudioFormat audioFormat = getAudioFormat();

            // 8 bits in 1 byte, 1000 ms in 1 s
            int bitsPerOneMs = (int) ((audioFormat.getSampleRate() * audioFormat.getChannels() * audioFormat.getSampleSizeInBits()) / 8 ) / 1000;

            int bufferSize = bitsPerOneMs * AppConfig.getFftWindowMs();
            int bufferPadding = bufferSize * AppConfig.getFftWindowFrames();

            targetLine = getTargetLine(audioFormat, bufferSize);
            sourceLine = getSourceLine(audioFormat, bufferSize);
            run(targetLine, sourceLine, bufferPadding, bufferSize);

        } catch (LineUnavailableException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        run = false;

        // wait for thread to finish
        try {
            thread.join(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (sourceLine != null) {
            sourceLine.drain();
            sourceLine.stop();
            sourceLine.close();
        }

        if (targetLine != null) {
            targetLine.drain();
            targetLine.stop();
            targetLine.close();
        }
    }

    @Override
    public void restart() {
        stop();
        start();
    }

    private void run(TargetDataLine targetDataLine, SourceDataLine sourceDataLine,  int bufferPadding, int bufferSize) {
        thread = new Thread(new Runnable() {
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
                RestartProcessor restartProcessor = new RestartProcessor(getThis());
                MultichannelToMono multichannelToMono = new MultichannelToMono(format.getChannels(), true);

                byte[] bytes = new byte[bufferPadding];
                byte[] readBytes = new byte[bufferSize];
                float[] floats = new float[bufferPadding / getAudioFormat().getChannels()];
                run = true;
                while (run) {
                    targetDataLine.read(readBytes, 0, readBytes.length);

                    System.arraycopy(bytes, bufferSize, bytes, 0, bufferPadding - bufferSize);
                    System.arraycopy(readBytes, 0, bytes, bufferPadding - bufferSize, bufferSize);

                    converter.toFloatArray(bytes, floats);
                    audioEvent.setFloatBuffer(floats);

                    multichannelToMono.process(audioEvent);
                    fftAudioProcessor.process(audioEvent);
                    restartProcessor.process(audioEvent);

                    if (sourceDataLine != null) {
                        sourceDataLine.write(readBytes, 0, readBytes.length);
                    }

                }
            }
        });

        thread.setDaemon(true);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setName("JVM Audio Thread");
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

    private TargetDataLine getTargetLine(AudioFormat audioFormat, int bufferSize) throws LineUnavailableException {
        //noinspection OptionalGetWithoutIsPresent
        Mixer.Info mixerInfo = Stream.of(AudioSystem.getMixerInfo())
                .filter(curentMixerInfo -> curentMixerInfo.getName().equals(AppConfig.getInputDevice()))
                .findFirst()
                .get();
        LOGGER.info("mixer info: " + mixerInfo);
        Mixer mixer = AudioSystem.getMixer(mixerInfo);

        //noinspection OptionalGetWithoutIsPresent
        Line.Info lineInfo = Stream.of(mixer.getTargetLineInfo()).findFirst().get();
        TargetDataLine line = (TargetDataLine) mixer.getLine(lineInfo);
        line.open(audioFormat, bufferSize * 2);
        line.start();

        LOGGER.info("line format: " + line.getFormat());
        LOGGER.info("line info: " + line.getLineInfo());
        LOGGER.info("line bufferSize size: " + line.getBufferSize());

        return line;
    }

    private SourceDataLine getSourceLine(AudioFormat audioFormat, int bufferSize) throws LineUnavailableException {
        if ("None".equals(AppConfig.getOutputDevice())) {
            return null;
        }

        //noinspection OptionalGetWithoutIsPresent
        Mixer.Info mixerInfo = Stream.of(AudioSystem.getMixerInfo())
                .filter(curentMixerInfo -> curentMixerInfo.getName().equals(AppConfig.getOutputDevice()))
                .findFirst()
                .get();
        LOGGER.info("mixer info: " + mixerInfo);
        Mixer mixer = AudioSystem.getMixer(mixerInfo);

        //noinspection OptionalGetWithoutIsPresent
        Line.Info lineInfo = Stream.of(mixer.getSourceLineInfo()).findFirst().get();
        SourceDataLine line = (SourceDataLine) mixer.getLine(lineInfo);
        line.open(audioFormat, bufferSize * 2);
        line.start();

        LOGGER.info("line format: " + line.getFormat());
        LOGGER.info("line info: " + line.getLineInfo());
        LOGGER.info("line bufferSize size: " + line.getBufferSize());

        return line;
    }

    private AudioEngine getThis(){
        return this;
    }
}
