package com.lazydash.audio.visualizer.core.algorithm;

import com.lazydash.audio.visualizer.core.model.FrequencyBar;
import com.lazydash.audio.visualizer.system.config.AppConfig;
import com.lazydash.audio.visualizer.system.config.ColorConfig;
import javafx.scene.paint.Color;

import java.util.List;

public class GlobalColorCalculator {

    public Color getGlobalColor(List<FrequencyBar> frequencyBars, int startHz, int endHz, boolean darkMode) {
        if (frequencyBars.size() == 0) {
            if (darkMode) {
                return Color.BLACK;

            } else {
                return Color.WHITE;
            }
        }

        double sumIntensity = 0;
        double maxIntensity = 0;

        double sumRed = 0;
        double sumGreen = 0;
        double sumBlue = 0;

        int nrBars = 0;

        for (FrequencyBar frequencyBar : frequencyBars) {
            if (startHz <= frequencyBar.getHz() && frequencyBar.getHz() <= endHz) {
                double barHeight = frequencyBar.getHeight();
                double barIntensity = barHeight / AppConfig.getMaxBarHeight();

                Color barColor = frequencyBar.getColor();
                sumRed = sumRed + (barColor.getRed() * barIntensity);
                sumGreen = sumGreen + (barColor.getGreen() * barIntensity);
                sumBlue = sumBlue + (barColor.getBlue() * barIntensity);

                sumIntensity = sumIntensity + barIntensity;

                if (barIntensity > maxIntensity) {
                    maxIntensity = barIntensity;
                }

                nrBars++;
            }
        }

        double avgRed = sumRed / nrBars;
        double avgGreen = sumGreen / nrBars;
        double avgBlue = sumBlue / nrBars;

        double avgIntensity = sumIntensity / nrBars;

        Color baseColor = ColorConfig.baseColor;
        Color mixColor = baseColor.interpolate(Color.color(avgRed, avgGreen, avgBlue, 1), maxIntensity);

        Color hsb;
        if (darkMode) {
            hsb = Color.hsb(mixColor.getHue(), mixColor.getSaturation(), avgIntensity);

        } else {
            hsb = Color.hsb(mixColor.getHue(), avgIntensity, 1);
        }

        return hsb;
    }

}
