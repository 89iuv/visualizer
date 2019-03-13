package com.lazydash.audio.visualizer.plugins.arduino.extensions;

import com.lazydash.audio.visualizer.spectrum.plugin.UiSettingsExtensionPoint;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.pf4j.Extension;

import java.io.IOException;

@Extension
public class ArduinoUiSettingsExtension implements UiSettingsExtensionPoint {

    @Override
    public String getPluginTitle() {
        return "Neopixel strip";
    }

    @Override
    public Parent getPluginParent() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/ui/fxml/arduino/neopixel_strip.fxml"));

            // here something interesting happens
            // the code gets initialized using the classpath of the parent
            // but what we want is the classpath of the plugin
            // in order to detect and initialize the plugin controller
            fxmlLoader.setClassLoader(getClass().getClassLoader());
            return fxmlLoader.load();

        } catch (IOException e) {
            e.printStackTrace();

            //fixme always return a pane with the encountered errors
            throw new RuntimeException(e);
        }

    }
}
