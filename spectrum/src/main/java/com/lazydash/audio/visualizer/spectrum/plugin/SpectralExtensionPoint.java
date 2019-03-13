package com.lazydash.audio.visualizer.spectrum.plugin;

import com.lazydash.audio.visualizer.spectrum.core.service.FrequencyBarsFFTService;
import org.pf4j.ExtensionPoint;

public interface SpectralExtensionPoint extends ExtensionPoint {

    void register(FrequencyBarsFFTService frequencyBarsFFTService);

}
