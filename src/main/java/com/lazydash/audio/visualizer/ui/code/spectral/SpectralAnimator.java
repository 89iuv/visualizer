package com.lazydash.audio.visualizer.ui.code.spectral;

import com.lazydash.audio.visualizer.core.service.GenericFFTService;
import com.lazydash.audio.visualizer.system.config.AppConfig;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class SpectralAnimator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpectralAnimator.class);
    private long oldTime = System.currentTimeMillis();

    private GenericFFTService spectralFFTService;
    private SpectralView spectralView;

    private Timeline timeline = new Timeline(new KeyFrame(
            Duration.millis(1000d / AppConfig.getTargetFPS()),
            ae -> updateSpectralView()));

    public SpectralAnimator(GenericFFTService spectralFFTService, SpectralView spectralView) {
        this.spectralFFTService = spectralFFTService;
        this.spectralView = spectralView;
    }

    public void play(){
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void updateSpectralView(){
        Map<double[], float[]> binsToAmplitudesMap = spectralFFTService.getBinsToAmplitudesMap();
        Optional<Map.Entry<double[], float[]>> first = binsToAmplitudesMap.entrySet().stream().findFirst();

        first.ifPresent(entry -> {
            double[] binsHz = entry.getKey();
            float[] amplitudes = entry.getValue();

            // the audio engine has not run yet
            if (binsHz == null || amplitudes == null) {
                return;
            }

            spectralView.updateState(binsHz, amplitudes);

            long newTime = System.currentTimeMillis();
            long deltaTime = newTime - oldTime;
            if (Math.abs(deltaTime - 1000d / AppConfig.getTargetFPS()) > 3) {
//                LOGGER.trace(String.valueOf(deltaTime));
            }
            oldTime = newTime;
        });


    }

}
