package com.lazydash.audio.visualizer.external.hue.manager;

import com.lazydash.audio.visualizer.core.algorithm.GlobalColorCalculator;
import com.lazydash.audio.visualizer.core.model.FrequencyBar;
import com.lazydash.audio.visualizer.core.service.FrequencyBarsFFTService;
import com.lazydash.audio.visualizer.external.hue.HueIntegration;
import com.lazydash.audio.visualizer.system.config.AppConfig;
import com.lazydash.audio.visualizer.system.config.ColorConfig;
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

    private Color previousColor = ColorConfig.baseColor;

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

        double delayMs = (1000 / AppConfig.getHueTargetFPS());
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
        Color color = globalColorCalculator.getGlobalColor(frequencyBarList, Integer.MIN_VALUE, Integer.MAX_VALUE, true);

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
