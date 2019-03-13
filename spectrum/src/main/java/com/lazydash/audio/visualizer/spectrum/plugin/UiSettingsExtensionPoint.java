package com.lazydash.audio.visualizer.spectrum.plugin;

import com.lazydash.audio.visualizer.spectrum.ui.fxml.settings.SettingsController;
import org.pf4j.ExtensionPoint;

public interface UiSettingsExtensionPoint extends ExtensionPoint {

    void extendController(SettingsController settingsController);

}
