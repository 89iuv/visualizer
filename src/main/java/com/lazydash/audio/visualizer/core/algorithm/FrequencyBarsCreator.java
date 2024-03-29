package com.lazydash.audio.visualizer.core.algorithm;

import com.lazydash.audio.visualizer.core.model.FrequencyBar;
import com.lazydash.audio.visualizer.system.config.AppConfig;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class FrequencyBarsCreator {

    public static List<FrequencyBar> createFrequencyBars(double[] binsHz, double[] amplitudes) {
        List<FrequencyBar> frequencyBars = new ArrayList<>(binsHz.length);
        for (int i = 0; i < binsHz.length; i++) {
            frequencyBars.add(new FrequencyBar(binsHz[i], amplitudes[i], Color.BLACK));
        }

        double pos = AppConfig.spectralColorPosition;
        double range = AppConfig.spectralColorRange;
        double saturation = AppConfig.saturation / 100d;
        double brightness = AppConfig.brightness / 100d;
        boolean inverted = AppConfig.spectralColorInverted;

        if (!inverted) {
            for (int i = 0; i < binsHz.length; i++) {
                setColor(frequencyBars, pos, saturation, brightness, i);
                pos = pos + (range / binsHz.length);
            }

        } else {
            for (int i = binsHz.length - 1; i >= 0; i--) {
                setColor(frequencyBars, pos, saturation, brightness, i);
                pos = pos + (range / binsHz.length);
            }
        }

        return frequencyBars;
    }

    private static void setColor(List<FrequencyBar> frequencyBars, double pos, double saturation, double brightness, int i) {
        Color color = Color.hsb(pos, saturation, brightness);
//        color = SpectralColorConfig.baseColor.interpolate(color, frequencyBars.get(i).getHeight() / AppConfig.getMaxBarHeight());
        frequencyBars.get(i).setColor(color);
    }
}
