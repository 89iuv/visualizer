package com.lazydash.audio.spectrum.core.algorithm;

import com.lazydash.audio.spectrum.core.model.FrequencyBar;
import com.lazydash.audio.spectrum.system.config.AppConfig;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class FrequencyBarsCreator {

    public static List<FrequencyBar> createFrequencyBars(double[] binsHz, double[] amplitudes) {
        List<FrequencyBar> frequencyBars = new ArrayList<>(binsHz.length);
        for (int i=0; i < binsHz.length; i++) {
            frequencyBars.add(new FrequencyBar(binsHz[i], amplitudes[i], Color.BLACK));
        }

        double pos = AppConfig.getSpectralColorPosition();
        double range = AppConfig.getSpectralColorRange();
        boolean inverted = AppConfig.isSpectralColorInverted();

        if (!inverted) {
            for (int i=0; i < binsHz.length; i++) {
                frequencyBars.get(i).setColor(Color.hsb(pos, 1,1));
                pos = pos + (range / binsHz.length);
            }

        } else {
            for (int i = binsHz.length - 1; i >= 0; i--) {
                frequencyBars.get(i).setColor(Color.hsb(pos, 1,1));
                pos = pos + (range / binsHz.length);
            }
        }



       /* for (ColorBand colorBand: SpectralColorConfig.colorBands) {
            addColorsToFrequencyBars(
                    frequencyBars,
                    colorBand.getStartColor(),
                    colorBand.getEndColor(),
                    colorBand.getStartHz(),
                    colorBand.getEndHz()
            );
        }*/

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
