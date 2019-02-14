package com.lazydash.audio.spectrum.plugin;

import com.lazydash.audio.spectrum.core.service.FrequencyBarsFFTService;
import org.pf4j.ExtensionPoint;

public interface SpectralExtensionPoint extends ExtensionPoint {

    void register(FrequencyBarsFFTService frequencyBarsFFTService);

}
