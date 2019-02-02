package com.lazydash.audio.visualizer.ui.code.spectral;

import com.lazydash.audio.visualizer.core.service.FrequencyBarsFFTService;
import com.lazydash.audio.visualizer.system.config.AppConfig;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpectralAnimator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpectralAnimator.class);
    private long oldTime = System.currentTimeMillis();

    private FrequencyBarsFFTService spectralFFTService;
    private SpectralView spectralView;

    private Timeline timeline = new Timeline(new KeyFrame(
            Duration.millis(1000d / AppConfig.getTargetFPS()),
            ae -> updateSpectralView()));

    public SpectralAnimator(FrequencyBarsFFTService spectralFFTService, SpectralView spectralView) {
        this.spectralFFTService = spectralFFTService;
        this.spectralView = spectralView;
    }

    public void play(){
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void updateSpectralView(){
        spectralView.updateState(spectralFFTService.getFrequencyBarList(AppConfig.getTargetFPS()));

        long newTime = System.currentTimeMillis();
        long deltaTime = newTime - oldTime;

        if (Math.abs(deltaTime - 1000d / AppConfig.getTargetFPS()) > 3) {
//                LOGGER.trace(String.valueOf(deltaTime));
        }
        oldTime = newTime;
    }

}
