package com.lazydash.audio.visualizer.ui.code.color;

import com.lazydash.audio.visualizer.core.algorithm.GlobalColorCalculator;
import com.lazydash.audio.visualizer.core.model.FrequencyBar;
import com.lazydash.audio.visualizer.core.service.FrequencyBarsFFTService;
import com.lazydash.audio.visualizer.system.config.AppConfig;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.List;

public class GlobalColorAnimator {
    private GlobalColorView globalColorView;
    private FrequencyBarsFFTService globalColorFFTService;

    private GlobalColorCalculator globalColorCalculator = new GlobalColorCalculator();

    private Timeline timeline = new Timeline(new KeyFrame(
            Duration.millis(1000d / AppConfig.getTargetFPS()),
            ae -> updateGlobalColor()));

    public GlobalColorAnimator(FrequencyBarsFFTService globalColorFFTService, GlobalColorView globalColorView) {
        this.globalColorView = globalColorView;
        this.globalColorFFTService = globalColorFFTService;
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    public void play(){
        timeline.play();
    }

    private void updateGlobalColor() {
        List<FrequencyBar> frequencyBars = globalColorFFTService.getFrequencyBarList(AppConfig.getTargetFPS());
        Color color = globalColorCalculator.getGlobalColor(frequencyBars, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        globalColorView.setColor(color);
    }
}
