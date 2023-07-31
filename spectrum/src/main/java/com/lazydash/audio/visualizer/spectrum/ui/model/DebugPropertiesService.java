package com.lazydash.audio.visualizer.spectrum.ui.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class DebugPropertiesService {

    private static final DebugPropertiesService DEBUG_PROPERTIES_SERVICE = new DebugPropertiesService();

    private final DoubleProperty uiFps = new SimpleDoubleProperty();
    private final DoubleProperty audioFramesMergedPerSecond = new SimpleDoubleProperty();
    private final DoubleProperty avgSameAudioFrameFromUiFramePerSecond = new SimpleDoubleProperty();

    private DebugPropertiesService() {
        // use static method
    }

    public static DebugPropertiesService getInstance() {
        return DEBUG_PROPERTIES_SERVICE;
    }

    public DoubleProperty getUiFps() {
        return uiFps;
    }

    public DoubleProperty getAudioFramesMergedPerSecond() {
        return audioFramesMergedPerSecond;
    }

    public DoubleProperty getAvgSameAudioFrameFromUiFramePerSecond() {
        return avgSameAudioFrameFromUiFramePerSecond;
    }

}
