package com.lazydash.audio.visualizer.core.algorithm;

import com.lazydash.audio.visualizer.core.model.ColorBand;
import com.lazydash.audio.visualizer.core.model.FrequencyBar;
import com.lazydash.audio.visualizer.system.config.SpectralColorConfig;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class FrequencyBarsCreator {

    public static List<FrequencyBar> createFrequencyBars(double[] binsHz, float[] amplitudes) {
        List<FrequencyBar> frequencyBars = new ArrayList<>(binsHz.length);
        for (int i=0; i < binsHz.length; i++) {
            frequencyBars.add(new FrequencyBar(binsHz[i], amplitudes[i], Color.BLACK));
        }

        for (ColorBand colorBand: SpectralColorConfig.colorBands) {
            addColorsToFrequencyBars(
                    frequencyBars,
                    colorBand.getStartColor(),
                    colorBand.getEndColor(),
                    colorBand.getStartHz(),
                    colorBand.getEndHz()
            );
        }
        return frequencyBars;
    }

    private static void addColorsToFrequencyBars(List<FrequencyBar> frequencyBars, Color startColor, Color endColor, int startHz, int endHz){
        int countFreq = 0;
        for (FrequencyBar frequencyBar : frequencyBars) {
            if (startHz <= frequencyBar.getHz() && frequencyBar.getHz() <= endHz) {
                countFreq++;
            }
        }

        double step = 0.0;
        double stepIncrement = 1d / countFreq;
        for (FrequencyBar frequencyBar : frequencyBars) {
            if (startHz <= frequencyBar.getHz() && frequencyBar.getHz() <= endHz) {
                frequencyBar.setColor(startColor.interpolate(endColor, step));
                step = step + stepIncrement;
            }
        }

    }

}