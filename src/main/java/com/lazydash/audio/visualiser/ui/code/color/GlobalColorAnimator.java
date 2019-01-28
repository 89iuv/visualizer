package com.lazydash.audio.visualiser.ui.code.color;

import com.lazydash.audio.visualiser.system.config.AppConfig;
import com.lazydash.audio.visualiser.ui.code.spectral.SpectralView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class GlobalColorAnimator {
    private GlobalColorView globalColorView;
    private SpectralView spectralView;
    private Color previousColor = Color.WHITE;

    private Timeline timeline = new Timeline(new KeyFrame(
            Duration.millis(1000d / AppConfig.getTargetFPS()),
            ae -> updateGlobalColor()));

    public GlobalColorAnimator(GlobalColorView globalColorView, SpectralView spectralView) {
        this.globalColorView = globalColorView;
        this.spectralView = spectralView;
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    public void play(){
        timeline.play();
    }

    private void updateGlobalColor() {
        Color currentColor = spectralView.getGlobalColorLight(Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (!previousColor.equals(currentColor)) {
            previousColor = currentColor;
            globalColorView.setColor(currentColor);
        }
    }
}
