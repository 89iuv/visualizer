package com.lazydash.audio.visualizer.plugins.hue.extensions;

import com.lazydash.audio.visualizer.spectrum.plugin.UiSettingsExtensionPoint;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.pf4j.Extension;

import java.io.IOException;

@Extension
public class HueUiSettingsExtension implements UiSettingsExtensionPoint {

    @Override
    public String getPluginTitle() {
        return "Hue integration";
    }

    @Override
    public Parent getPluginParent() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/hue/hue_integration.fxml"));

            // here something interesting happens
            // the code gets initialized using the classpath of the parent
            // but what we want is the classpath of the plugin
            // in order to detect and initialize the plugin controller
            fxmlLoader.setClassLoader(getClass().getClassLoader());

            Parent parent = null;

            parent = fxmlLoader.load();
            return parent;

        } catch (IOException e) {
            e.printStackTrace();

            //fixme always return a pane with the encountered errors
            throw new RuntimeException(e);

        }

    }
}
