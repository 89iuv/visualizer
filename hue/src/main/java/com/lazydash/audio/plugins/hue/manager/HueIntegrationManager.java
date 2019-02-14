package com.lazydash.audio.plugins.hue.manager;

import com.lazydash.audio.plugins.hue.HueIntegration;
import com.lazydash.audio.plugins.hue.system.config.AppConfig;
import com.lazydash.audio.spectrum.core.algorithm.GlobalColorCalculator;
import com.lazydash.audio.spectrum.core.model.FrequencyBar;
import com.lazydash.audio.spectrum.core.service.FrequencyBarsFFTService;
import com.lazydash.audio.spectrum.system.config.SpectralColorConfig;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HueIntegrationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(HueIntegrationManager.class);
    private long oldTime = System.currentTimeMillis();

    private HueIntegration hueIntegration;
    private FrequencyBarsFFTService hueFFTService;

    private Color previousColor = SpectralColorConfig.baseColor;

    private GlobalColorCalculator globalColorCalculator = new GlobalColorCalculator();

    public HueIntegrationManager(HueIntegration hueIntegration, FrequencyBarsFFTService hueFFTService) {
        this.hueIntegration = hueIntegration;
        this.hueFFTService = hueFFTService;
    }

    public void start() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });

        // process correction of 2 ms
        double delayMs = (1000 / AppConfig.getHueTargetFPS()) - 2;
        executorService.scheduleWithFixedDelay(
                this::run,
                0,
                (int) delayMs, TimeUnit.MILLISECONDS);

    }

    private void run() {
        if (AppConfig.isHueIntegrationEnabled()) {
            if (hueIntegration.isReady()) {
                updateHueColor();

            } else {
                hueIntegration.start();
            }

        } else {
            hueIntegration.stop();

        }
    }

    private void updateHueColor() {
        List<FrequencyBar> frequencyBarList = hueFFTService.getFrequencyBarList(AppConfig.getHueTargetFPS());
        Color color = globalColorCalculator.getGlobalColor(
                frequencyBarList,
                Integer.MIN_VALUE,
                80,
                true);

        if (!previousColor.equals(color)) {
            hueIntegration.setColor(color);
            previousColor = color;
        }

        long newTime = System.currentTimeMillis();
        long deltaTime = newTime - oldTime;
//        System.out.println(deltaTime);
        oldTime = newTime;

    }
}
