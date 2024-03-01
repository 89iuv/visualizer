package com.lazydash.audio.visualizer.ui.code.spectral;

import com.lazydash.audio.visualizer.core.service.FrequencyBarsFFTService;
import com.lazydash.audio.visualizer.ui.model.DebugPropertiesService;
import javafx.animation.AnimationTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpectralAnimator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpectralAnimator.class);

    private final DebugPropertiesService debugPropertiesService = DebugPropertiesService.getInstance();
    private int nrFrame = 0;

    private long oldTime = System.currentTimeMillis();

    private FrequencyBarsFFTService spectralFFTService;
    private SpectralView spectralView;

    private AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            nrFrame++;

            long newTime = System.currentTimeMillis();

            if (newTime - oldTime > 1000) {
                debugPropertiesService.getUiFps().setValue(nrFrame);
                nrFrame = 0;
                oldTime = newTime;
            }

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
    }

}
