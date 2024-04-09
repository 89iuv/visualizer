package com.lazydash.audio.visualizer;

import com.lazydash.audio.visualizer.core.algorithm.OctaveGenerator;
import org.junit.Test;

import java.util.List;

public class OctaveGeneratorTest {

    @Test
    public void testHighLow() {
        int center = 1000;
        int band = 1;

        double highLimit = OctaveGenerator.getHighCutoffLimit(center, band);
        double lowLimit = OctaveGenerator.getLowCutoffLimit(center, band);

//        System.out.println(lowLimit + " " + center + " " + highLimit);

    }

    @Test
    public void testOctaveGenerator() {
        int band = 1;

        List<Integer> octaveFrequencies = OctaveGenerator.getOctaveFrequencies(1000, band, 30, 17000);

        octaveFrequencies.stream().forEach(center -> {
            int highLimit = OctaveGenerator.getHighCutoffLimit(center, band);
            int lowLimit = OctaveGenerator.getLowCutoffLimit(center, band);

//            System.out.println(lowLimit + " " + center + " " + highLimit);
        });
    }

}
