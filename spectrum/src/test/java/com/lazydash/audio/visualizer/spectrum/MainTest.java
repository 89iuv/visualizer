package com.lazydash.audio.visualizer.spectrum;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFloatConverter;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;

import javax.sound.sampled.*;
import java.util.stream.Stream;

public class MainTest {
    private static long oldTime = System.currentTimeMillis();

    public static void main(String[] args) throws Exception {
        int latencyMs = 80;
        int bytesPerFrame = 96 * 2; // this is depended on the audio format
//        int bufferSize = bytesPerFrame * latencyMs;
        int bufferSize = 6144;

        String primarySoundCaptureDriver = "Primary Sound Capture Driver";
        String primarySoundDriver = "Speakers (2- USB Audio CODEC )";

        // print all mixers
        Stream.of(AudioSystem.getMixerInfo())
                .forEach(mixerInfo -> {
                    System.out.println(mixerInfo.getName());
                });
        System.out.println();

        // read audio
        Mixer.Info outputMixerInfo = Stream.of(AudioSystem.getMixerInfo())
                .filter(curentMixerInfo -> curentMixerInfo.getName().equals(primarySoundCaptureDriver))
                .findFirst()
                .get();
        System.out.println("output mixer info: " + outputMixerInfo);
        Mixer outputMixer = AudioSystem.getMixer(outputMixerInfo);

        Line.Info targetLineInfo = Stream.of(outputMixer.getTargetLineInfo()).findFirst().get();
        TargetDataLine targetLine = (TargetDataLine) outputMixer.getLine(targetLineInfo);

        targetLine.open(getAudioFormat(), bufferSize*2);
        targetLine.start();

        System.out.println("targetLine format: " + targetLine.getFormat());
        System.out.println("targetLine info: " + targetLine.getLineInfo());
        System.out.println("targetLine bufferSize size: " + targetLine.getBufferSize());


        // write audio
        Mixer.Info inputMixerInfo = Stream.of(AudioSystem.getMixerInfo())
                .filter(curentMixerInfo -> curentMixerInfo.getName().equals(primarySoundDriver))
                .findFirst()
                .get();
        System.out.println("input mixer info: " + inputMixerInfo);
        Mixer inputMixer = AudioSystem.getMixer(inputMixerInfo);

        Line.Info sourceLineInfo = Stream.of(inputMixer.getSourceLineInfo()).findFirst().get();
        SourceDataLine sourceLine = (SourceDataLine) inputMixer.getLine(sourceLineInfo);
        sourceLine.open(getAudioFormat(), bufferSize*2);
        sourceLine.start();

        System.out.println("sourceLine format: " + sourceLine.getFormat());
        System.out.println("sourceLine info: " + sourceLine.getLineInfo());
        System.out.println("sourceLine bufferSize size: " + sourceLine.getBufferSize());


        byte[] bytes = new byte[bufferSize];
        while (true) {
            int read = targetLine.read(bytes, 0, bufferSize);
            int write = sourceLine.write(bytes, 0, bufferSize);

            long newTime = System.currentTimeMillis();
            long delta = newTime - oldTime;
//            System.out.println(delta);
            oldTime = newTime;
        }
    }

    static private AudioFormat getAudioFormat() {
        return new AudioFormat(
                48000,
                16,
                2,
                true,
                true
        );
    }
}
