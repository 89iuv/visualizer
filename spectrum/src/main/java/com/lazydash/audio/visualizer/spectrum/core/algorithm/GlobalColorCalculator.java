package com.lazydash.audio.visualizer.spectrum.core.algorithm;

import com.lazydash.audio.visualizer.spectrum.core.model.FrequencyBar;
import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;
import com.lazydash.audio.visualizer.spectrum.system.config.SpectralColorConfig;
import javafx.scene.paint.Color;

import java.util.List;

public class GlobalColorCalculator {

    public Color getGlobalColor(List<FrequencyBar> frequencyBars, int startHz, int endHz, Peak peak) {
        if (frequencyBars.size() == 0) {
            return Color.BLACK;
        }

        double sumIntensity = 0;
        double maxIntensity = 0;

        double sumRed = 0;
        double sumGreen = 0;
        double sumBlue = 0;

        int nrBars = 0;

        for (FrequencyBar frequencyBar : frequencyBars) {
            if (startHz <= frequencyBar.getHz()
                    && frequencyBar.getHz() <= endHz) {
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

        double intensity = 0;
        if (peak.equals(Peak.AVG)) {
            intensity = avgIntensity;
        } else if (peak.equals(Peak.MAX)) {
            intensity = maxIntensity;
        }
        intensity = intensity * (AppConfig.getBrightness() / 100d);

        Color baseColor = SpectralColorConfig.baseColor;
        Color color = Color.color(avgRed, avgGreen, avgBlue);
        color = baseColor.interpolate(color, maxIntensity);
        color = Color.hsb(color.getHue(), color.getSaturation(), intensity);
        return color;
    }

    public enum Peak {
        AVG("Avg"),
        MAX("Max");

        private String value;

        Peak(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static Peak fromString(String value){
            for (Peak peak: Peak.values()) {
                if (peak.getValue().equals(value)) {
                    return peak;
                }
            }

            return null;
        }
    }
}
