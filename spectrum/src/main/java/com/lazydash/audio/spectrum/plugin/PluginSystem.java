package com.lazydash.audio.spectrum.plugin;

import com.lazydash.audio.spectrum.core.audio.TarsosAudioEngine;
import com.lazydash.audio.spectrum.core.service.FrequencyBarsFFTService;
import com.lazydash.audio.spectrum.ui.fxml.settings.SettingsController;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import java.util.List;

public class PluginSystem {
    private PluginManager pluginManager = new DefaultPluginManager();

    private static PluginSystem pluginSystem = new PluginSystem();

    private PluginSystem(){}

    public static PluginSystem getInstance(){
        return pluginSystem;
    }

    public void startAllPlugins() {
        pluginManager.loadPlugins();
        pluginManager.startPlugins();
    }

    public void stopAllPlugins() {
        pluginManager.stopPlugins();
    }

    public void registerAllFffPlugins(TarsosAudioEngine tarsosAudioEngine){
        List<SpectralExtensionPoint> fftRegisters = pluginManager.getExtensions(SpectralExtensionPoint.class);
        for (SpectralExtensionPoint fftRegister : fftRegisters) {
            FrequencyBarsFFTService frequencyBarsFFTService = new FrequencyBarsFFTService();
            tarsosAudioEngine.getFttListenerList().add(frequencyBarsFFTService);
            fftRegister.register(frequencyBarsFFTService);
        }

    }

    public void extendUiPlugin(SettingsController settingsController){
        List<UiExtensionPoint> uiExtensionPoints = pluginManager.getExtensions(UiExtensionPoint.class);
        for (UiExtensionPoint uiExtensionPoint : uiExtensionPoints) {
            uiExtensionPoint.extendSettingsController(settingsController);
        }
    }

}
