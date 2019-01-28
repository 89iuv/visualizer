package com.lazydash.audio.visualiser.core.manager;

import com.lazydash.audio.visualiser.core.algorithm.BarsHeightCalculator;
import com.lazydash.audio.visualiser.core.algorithm.FFTTimeFilter;
import com.lazydash.audio.visualiser.core.algorithm.GlobalColorCalculator;
import com.lazydash.audio.visualiser.core.audio.FFTListener;
import com.lazydash.audio.visualiser.core.model.ColorBand;
import com.lazydash.audio.visualiser.core.model.FrequencyBar;
import com.lazydash.audio.visualiser.core.service.HueFFTService;
import com.lazydash.audio.visualiser.external.hue.HueIntegration;
import com.lazydash.audio.visualiser.system.config.AppConfig;
import com.lazydash.audio.visualiser.system.config.ColorConfig;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HueIntegrationManager implements FFTListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(HueIntegrationManager.class);
    private long oldTime = System.currentTimeMillis();

    private Color oldFrontColor = ColorConfig.baseColor;
    private Color oldBackColor = ColorConfig.baseColor;

    private double[] oldBinsHz = new double[AppConfig.getBarNumber()];

    private HueIntegration hueIntegration;
    private HueFFTService hueFFTService;

    private BarsHeightCalculator barsHeightCalculator = new BarsHeightCalculator();
    private FFTTimeFilter fftTimeFilter = new FFTTimeFilter();
    private GlobalColorCalculator getGlobalColor = new GlobalColorCalculator();

    private ScheduledExecutorService executorService;
    private boolean isExecutorServiceRunning;

    public HueIntegrationManager(HueIntegration hueIntegration, HueFFTService hueFFTService) {
        this.hueIntegration = hueIntegration;
        this.hueFFTService = hueFFTService;
    }

    @Override
    public void frame(double[] hzBins, float[] normalizedAmplitudes, double spl) {
        if (AppConfig.isHueIntegration()) {
            hueIntegration.start();

        } else {
            hueIntegration.stop();
        }

        if (hueIntegration.isReady()) {
            if (!isExecutorServiceRunning) {
                isExecutorServiceRunning = true;

                executorService = Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread thread = new Thread(r);
                    thread.setDaemon(true);
                    return thread;
                });

                double delayMs = (1000 / AppConfig.getTargetFPS());
                executorService.scheduleWithFixedDelay(
                        this::updateHueColor,
                        0,
                        (int) delayMs, TimeUnit.MILLISECONDS);
            }

        } else {
            if (isExecutorServiceRunning) {
                executorService.shutdown();
                isExecutorServiceRunning = false;
            }
        }
    }

    private void updateHueColor() {
        Map<double[], float[]> binsToAmplitudesMap = hueFFTService.getBinsToAmplitudesMap();
        Optional<Map.Entry<double[], float[]>> first = binsToAmplitudesMap.entrySet().stream().findFirst();

        first.ifPresent(entry -> {
            double[] binsHz = entry.getKey();
            if (this.oldBinsHz.length != binsHz.length) {
                barsHeightCalculator = new BarsHeightCalculator();
                fftTimeFilter = new FFTTimeFilter();
            }
            this.oldBinsHz = binsHz;

            float[] amplitudes = entry.getValue();
            float[] timeFilteredAmplitudes = fftTimeFilter.filter(amplitudes);
            float[] processAmplitudes = barsHeightCalculator.processAmplitudes(timeFilteredAmplitudes);

            List<FrequencyBar> frequencyBars = new ArrayList<>(binsHz.length);
            for (int i=0; i < binsHz.length; i++) {
                frequencyBars.add(new FrequencyBar(binsHz[i], processAmplitudes[i], Color.BLACK));
            }

            for (ColorBand colorBand: ColorConfig.colorBands) {
                addColorsToFrequencyBars(
                        frequencyBars,
                        colorBand.getStartColor(),
                        colorBand.getEndColor(),
                        colorBand.getStartHz(),
                        colorBand.getEndHz()
                );
            }

            Color frontColor = getGlobalColor.getGlobalColor(frequencyBars, Integer.MIN_VALUE, 80, true);
            Color backColor = getGlobalColor.getGlobalColor(frequencyBars, 64, Integer.MAX_VALUE, true);
            hueIntegration.setColor(frontColor, backColor);

            long newTime = System.currentTimeMillis();
            long deltaTime = newTime - oldTime;
//            LOGGER.trace(String.valueOf(deltaTime));
            oldTime = newTime;
        });

    }

    private void addColorsToFrequencyBars(List<FrequencyBar> frequencyBars, Color startColor, Color endColor, int startHz, int endHz){
        int countFreq = 0;
        for (FrequencyBar frequencyBar : frequencyBars) {
            if (startHz <= frequencyBar.getHz() && frequencyBar.getHz() <= endHz) {
                countFreq++;
            }
        }

        double step = 0.0;
        double stepIncrement = 1d / countFreq;
        for (FrequencyBar frequencyBar : frequencyBars) {
            if (startHz <= frequencyBar.getHz() && frequencyBar.getHz() <= endHz) {
                frequencyBar.setColor(startColor.interpolate(endColor, step));
                step = step + stepIncrement;
            }
        }

    }
}
