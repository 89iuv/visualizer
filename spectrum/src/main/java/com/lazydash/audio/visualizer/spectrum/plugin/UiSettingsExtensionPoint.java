package com.lazydash.audio.visualizer.spectrum.plugin;

import javafx.scene.Parent;
import org.pf4j.ExtensionPoint;

public interface UiSettingsExtensionPoint extends ExtensionPoint {
    String getPluginTitle();

    Parent getPluginParent();

}
