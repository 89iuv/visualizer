package com.lazydash.audio.visualiser.core.algorithm;

import com.lazydash.audio.visualiser.core.model.ColorBand;
import com.lazydash.audio.visualiser.core.model.FrequencyBar;
import com.lazydash.audio.visualiser.system.config.AppConfig;
import com.lazydash.audio.visualiser.system.config.ColorConfig;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class GlobalColorCalculator {

    public Color getGlobalColor(List<FrequencyBar> frequencyBars, int startHz, int endHz, boolean darkMode) {
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

    public List<FrequencyBar> createFrequencyBars(double[] binsHz, float[] amplitudes) {
        List<FrequencyBar> frequencyBars = new ArrayList<>(binsHz.length);
        for (int i=0; i < binsHz.length; i++) {
            frequencyBars.add(new FrequencyBar(binsHz[i], amplitudes[i], Color.BLACK));
        }

        for (ColorBand colorBand: ColorConfig.colorBands) {
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

    private void addColorsToFrequencyBars(List<FrequencyBar> frequencyBars, Color startColor, Color endColor, int startHz, int endHz){
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
