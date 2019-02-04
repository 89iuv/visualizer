package com.lazydash.audio.visualizer.system.config;

import com.lazydash.audio.visualizer.core.model.ColorBand;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;

public class ColorConfig {
    public static Color baseColor = Color.color(0.1, 0.1, 0.2);

    public static List<ColorBand> colorBands = Arrays.asList(
            new ColorBand(
                    Color.color(0.0, 0.0, 1.0),
                    Color.color(0.8, 0.0, 0.5),
                    Integer.MIN_VALUE,
                    Integer.MAX_VALUE)
    );
}
