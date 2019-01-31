package com.lazydash.audio.visualiser.ui.code.color;

import com.lazydash.audio.visualiser.core.algorithm.GlobalColorCalculator;
import com.lazydash.audio.visualiser.core.model.FrequencyBar;
import com.lazydash.audio.visualiser.core.service.GenericFFTService;
import com.lazydash.audio.visualiser.system.config.AppConfig;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GlobalColorAnimator {
    private GlobalColorView globalColorView;
    private GenericFFTService globalColorFFTService;

    private GlobalColorCalculator globalColorCalculator = new GlobalColorCalculator();

    private Timeline timeline = new Timeline(new KeyFrame(
            Duration.millis(1000d / AppConfig.getTargetFPS()),
            ae -> updateGlobalColor()));

    public GlobalColorAnimator(GenericFFTService globalColorFFTService, GlobalColorView globalColorView) {
        this.globalColorView = globalColorView;
        this.globalColorFFTService = globalColorFFTService;
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    public void play(){
        timeline.play();
    }

    private void updateGlobalColor() {
        Map<double[], float[]> binsToAmplitudesMap = globalColorFFTService.getBinsToAmplitudesMap();
        Optional<Map.Entry<double[], float[]>> first = binsToAmplitudesMap.entrySet().stream().findFirst();

        first.ifPresent(entry -> {
            double[] binsHz = entry.getKey();
            float[] amplitudes = entry.getValue();

            List<FrequencyBar> frequencyBars = globalColorCalculator.createFrequencyBars(binsHz, amplitudes);
            Color color = globalColorCalculator.getGlobalColor(frequencyBars, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            globalColorView.setColor(color);

        });

    }
}
