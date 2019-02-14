package com.lazydash.audio.spectrum.plugin;

import com.lazydash.audio.spectrum.ui.fxml.settings.SettingsController;
import org.pf4j.ExtensionPoint;

public interface UiExtensionPoint extends ExtensionPoint {

    void extendSettingsController(SettingsController settingsController);

}
