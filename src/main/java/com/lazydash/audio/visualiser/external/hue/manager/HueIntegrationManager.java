package com.lazydash.audio.visualiser.external.hue.manager;

import com.lazydash.audio.visualiser.core.algorithm.GlobalColorCalculator;
import com.lazydash.audio.visualiser.core.model.FrequencyBar;
import com.lazydash.audio.visualiser.core.service.GenericFFTService;
import com.lazydash.audio.visualiser.external.hue.HueIntegration;
import com.lazydash.audio.visualiser.system.config.AppConfig;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HueIntegrationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(HueIntegrationManager.class);
    private long oldTime = System.currentTimeMillis();

    private HueIntegration hueIntegration;
    private GenericFFTService hueFFTService;

    private Color previousColor = Color.BLACK;

    private GlobalColorCalculator globalColorCalculator = new GlobalColorCalculator();

    public HueIntegrationManager(HueIntegration hueIntegration, GenericFFTService hueFFTService) {
        this.hueIntegration = hueIntegration;
        this.hueFFTService = hueFFTService;
    }

    public void start() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });

        double delayMs = (1000 / AppConfig.getTargetFPS());
        executorService.scheduleWithFixedDelay(
                this::run,
                0,
                (int) delayMs, TimeUnit.MILLISECONDS);

    }

    private void run() {
        if (AppConfig.isHueIntegrationEnabled()) {
            hueIntegration.start();

        } else {
            hueIntegration.stop();
        }

        if (hueIntegration.isReady()) {
            updateHueColor();
        }
    }

    private void updateHueColor() {
        Map<double[], float[]> binsToAmplitudesMap = hueFFTService.getBinsToAmplitudesMap();
        Optional<Map.Entry<double[], float[]>> first = binsToAmplitudesMap.entrySet().stream().findFirst();

        first.ifPresent(entry -> {
            double[] binsHz = entry.getKey();
            float[] amplitudes = entry.getValue();

            List<FrequencyBar> frequencyBars = globalColorCalculator.createFrequencyBars(binsHz, amplitudes);
            Color color = globalColorCalculator.getGlobalColor(frequencyBars, Integer.MIN_VALUE, Integer.MAX_VALUE, true);

            if (!previousColor.equals(color)) {
                hueIntegration.setColor(color);
                previousColor = color;
            }

            long newTime = System.currentTimeMillis();
            long deltaTime = newTime - oldTime;
//            LOGGER.trace(String.valueOf(deltaTime));
            oldTime = newTime;
        });
    }
}
