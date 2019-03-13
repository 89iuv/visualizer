package com.lazydash.audio.plugins.hue.extensions;

import com.lazydash.audio.spectrum.plugin.UiSettingsExtensionPoint;
import com.lazydash.audio.spectrum.ui.fxml.settings.SettingsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.pf4j.Extension;

import java.io.IOException;

@Extension
public class HueUiSettingsExtension implements UiSettingsExtensionPoint {

    @Override
    public void extendController(SettingsController settingsController) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/hue_integration.fxml"));

            // here something interesting happens
            // the code gets initialized using the classpath of the parent
            // but what we want is the classpath of the plugin
            // in order to detect and initialize the plugin controller
            fxmlLoader.setClassLoader(getClass().getClassLoader());

            Parent parent = fxmlLoader.load();
            settingsController.addTitleToPluginsFMXL("Hue integration", parent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
