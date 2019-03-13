package com.lazydash.audio.visualizer.spectrum.ui.code.spectral;

import com.lazydash.audio.visualizer.spectrum.core.service.FrequencyBarsFFTService;
import javafx.animation.AnimationTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpectralAnimator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpectralAnimator.class);
    private long oldTime = System.currentTimeMillis();

    private FrequencyBarsFFTService spectralFFTService;
    private SpectralView spectralView;

    private AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            updateSpectralView();
        }
    };

    public SpectralAnimator(FrequencyBarsFFTService spectralFFTService, SpectralView spectralView) {
        this.spectralFFTService = spectralFFTService;
        this.spectralView = spectralView;
    }

    public void play(){
        animationTimer.start();
    }

    private void updateSpectralView(){
        spectralView.updateState(spectralFFTService.getFrequencyBarList());

        long newTime = System.currentTimeMillis();
        long deltaTime = newTime - oldTime;

//        LOGGER.info(String.valueOf(deltaTime));
        oldTime = newTime;
    }

}
