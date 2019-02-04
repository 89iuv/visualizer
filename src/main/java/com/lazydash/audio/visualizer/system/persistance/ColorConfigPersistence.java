package com.lazydash.audio.visualizer.system.persistance;

import com.lazydash.audio.visualizer.system.config.ColorConfig;
import javafx.scene.paint.Color;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ColorConfigPersistence {
    private String colorPropertiesLocation = "color.properties";

    public static ColorConfigPersistence createAndReadColorConfiguration() {
        ColorConfigPersistence colorConfigPersistence = new ColorConfigPersistence();
        colorConfigPersistence.readConfig();
        return colorConfigPersistence;
    }

    private void readConfig() {
        Path colorPropertiesPath = Paths.get(colorPropertiesLocation);
        if (Files.exists(colorPropertiesPath)) {

            Properties appProps = new Properties();
            try {
                appProps.load(new FileReader(colorPropertiesLocation));

                String startColor = appProps.getProperty("startColor");
                String endColor = appProps.getProperty("endColor");

                ColorConfig.colorBands.get(0).setStartColor(
                        Color.web(startColor)
                );

                ColorConfig.colorBands.get(0).setEndColor(
                        Color.web(endColor)
                );

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public void persistConfig() {
        Properties appProps = new Properties();

        Color startColor = ColorConfig.colorBands.get(0).getStartColor();
        Color endColor = ColorConfig.colorBands.get(0).getEndColor();

        String startColorHex = toRGBCode(startColor);
        String endColorHex = toRGBCode(endColor);

        appProps.setProperty("startColor", startColorHex);
        appProps.setProperty("endColor", endColorHex);

        try {
            appProps.store(new FileWriter(colorPropertiesLocation), "visualizer - colors");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toRGBCode( Color color ) {
        return String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) );
    }

}
