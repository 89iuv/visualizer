package com.lazydash.audio.visualizer.spectrum.ui.fxml.spectrum.settings;

import com.lazydash.audio.visualizer.spectrum.ui.model.DebugPropertiesService;
import javafx.scene.control.Label;

import java.text.DecimalFormat;

public class DebugController {

    private final DebugPropertiesService debugPropertiesService = DebugPropertiesService.getInstance();

    public Label uiFps;
    public Label audioFramesMergedPerSecond;
    public Label avgSameAudioFrameFromUiFramePerSecond;

    public void initialize() {
        uiFps.textProperty().bindBidirectional(debugPropertiesService.getUiFps(), new DecimalFormat());
        audioFramesMergedPerSecond.textProperty().bindBidirectional(debugPropertiesService.getAudioFramesMergedPerSecond(), new DecimalFormat());
        avgSameAudioFrameFromUiFramePerSecond.textProperty().bindBidirectional(debugPropertiesService.getAvgSameAudioFrameFromUiFramePerSecond(), new DecimalFormat());

    }

}
