package com.lazydash.audio.plugins.hue.plugin.extensions;

import com.lazydash.audio.plugins.hue.HueIntegration;
import com.lazydash.audio.plugins.hue.manager.HueIntegrationManager;
import com.lazydash.audio.plugins.hue.setup.SystemSetup;
import com.lazydash.audio.spectrum.core.service.FrequencyBarsFFTService;
import com.lazydash.audio.spectrum.plugin.SpectralExtensionPoint;
import org.pf4j.Extension;

@Extension
public class HueFftExtension implements SpectralExtensionPoint {

    @Override
    public void register(FrequencyBarsFFTService frequencyBarsFFTService) {
        SystemSetup systemSetup = new SystemSetup();
        systemSetup.runHueConfiguration();

        HueIntegration hueIntegration = new HueIntegration();
        HueIntegrationManager hueIntegrationManager = new HueIntegrationManager(hueIntegration, frequencyBarsFFTService);
        hueIntegrationManager.start();
    }

}
