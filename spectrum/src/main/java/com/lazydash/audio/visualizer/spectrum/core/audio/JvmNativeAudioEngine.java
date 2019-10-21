package com.lazydash.audio.visualizer.spectrum.core.audio;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.MultichannelToMono;
import be.tarsos.dsp.io.TarsosDSPAudioFloatConverter;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class JvmNativeAudioEngine implements AudioEngine {
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
            targetLine = getTargetLine(audioFormat, AppConfig.getBufferSize());
            sourceLine = getSourceLine(audioFormat, AppConfig.getBufferSize());
            run(targetLine, sourceLine, AppConfig.getBufferPadding(), AppConfig.getBufferSize());

        } catch (LineUnavailableException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        try {
            run = false;
            thread.join(50000);

            sourceLine.drain();
            sourceLine.stop();
            sourceLine.close();

            targetLine.drain();
            targetLine.stop();
            targetLine.close();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void restart() {
        stop();
        start();
    }

    private void run(TargetDataLine targetDataLine, SourceDataLine sourceDataLine,  int bufferSize, int bufferPadding) {
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
                AudioEngineRestartProcessor audioEngineRestartProcessor = new AudioEngineRestartProcessor(getThis());
                MultichannelToMono multichannelToMono = new MultichannelToMono(format.getChannels(), true);

                byte[] bytes = new byte[bufferSize];
//                byte[] tmpBytes = new byte[bufferSize];
                byte[] readBytes = new byte[bufferPadding];
                float[] floats = new float[bufferSize / getAudioFormat().getChannels()];
                run = true;
                while (run) {
                    targetDataLine.read(readBytes, 0, readBytes.length);

//                    System.arraycopy(bytes, bufferPadding, tmpBytes, 0, bufferSize - bufferPadding);
//                    System.arraycopy(readBytes, 0, tmpBytes, bufferSize - bufferPadding, bufferPadding);
//                    System.arraycopy(tmpBytes, 0, bytes, 0, bufferSize);

                    System.arraycopy(bytes, bufferPadding, bytes, 0, bufferSize - bufferPadding);
                    System.arraycopy(readBytes, 0, bytes, bufferSize - bufferPadding, bufferPadding);
//                    System.arraycopy(tmpBytes, 0, bytes, 0, bufferSize);

                    converter.toFloatArray(bytes, floats);

                    audioEvent.setFloatBuffer(floats);

                    multichannelToMono.process(audioEvent);
                    fftAudioProcessor.process(audioEvent);

                    sourceDataLine.write(readBytes, 0, readBytes.length);
                    audioEngineRestartProcessor.process(audioEvent);
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
