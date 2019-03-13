package com.lazydash.audio.visualizer.plugins.arduino.extensions;

import com.lazydash.audio.visualizer.plugins.arduino.core.ArduinoManager;
import com.lazydash.audio.visualizer.spectrum.core.service.FrequencyBarsFFTService;
import com.lazydash.audio.visualizer.spectrum.plugin.SpectralExtensionPoint;
import org.pf4j.Extension;

@Extension
public class ArduinoFftExtension implements SpectralExtensionPoint {

    @Override
    public void register(FrequencyBarsFFTService frequencyBarsFFTService) {
        ArduinoManager arduinoManager = new ArduinoManager(frequencyBarsFFTService);
        arduinoManager.play();
    }

}
