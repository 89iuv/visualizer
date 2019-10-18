package com.lazydash.audio.visualizer.spectrum;

import be.tarsos.dsp.io.TarsosDSPAudioFloatConverter;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import org.junit.Test;

import javax.sound.sampled.AudioInputStream;
import java.util.stream.IntStream;

public class TarsosFloatConverter {

    @Test
    public void testAudioFloatConversion16SB() {
        TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(
                TarsosDSPAudioFormat.Encoding.PCM_SIGNED,
                48000,
                16,
                1,
                2,
                48000,
                true
        );

        TarsosDSPAudioFloatConverter converter = TarsosDSPAudioFloatConverter.getConverter(format);

        byte[] bytes = new byte[]{0, 0, 127, 127, 64, 64, 0, -127, 0, 64};
        float[] floats = new float[bytes.length / format.getFrameSize()];
        byte[] convertedBytes = new byte[bytes.length];

        converter.toFloatArray(bytes, floats);
        converter.toByteArray(floats, convertedBytes);

        IntStream.range(0, bytes.length).forEach(i -> System.out.println(bytes[i] + " "));
        IntStream.range(0, floats.length).forEach(i -> System.out.println(floats[i] + " "));
        IntStream.range(0, convertedBytes.length).forEach(i -> System.out.println(convertedBytes[i] + " "));


    }

}
