package com.lazydash.audio.plugins.hue.core.manager;

import com.lazydash.audio.plugins.hue.core.HueIntegration;
import com.lazydash.audio.plugins.hue.model.Location;
import com.lazydash.audio.plugins.hue.system.config.LocationConfig;
import com.lazydash.audio.plugins.hue.system.config.UserConfig;
import com.lazydash.audio.spectrum.core.algorithm.GlobalColorCalculator;
import com.lazydash.audio.spectrum.core.model.FrequencyBar;
import com.lazydash.audio.spectrum.core.service.FrequencyBarsFFTService;
import com.lazydash.audio.spectrum.system.worker.VariableFpsLoopWorker;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HueIntegrationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(HueIntegrationManager.class);
    private long oldTime = System.currentTimeMillis();

    private HueIntegration hueIntegration;
    private FrequencyBarsFFTService hueFFTService;

    private GlobalColorCalculator globalColorCalculator = new GlobalColorCalculator();

    public HueIntegrationManager(HueIntegration hueIntegration, FrequencyBarsFFTService hueFFTService) {
        this.hueIntegration = hueIntegration;
        this.hueFFTService = hueFFTService;
    }

    public void start() {
        VariableFpsLoopWorker variableFpsLoopWorker = new VariableFpsLoopWorker(){

            @Override
            public int getTargetFps() {
                return (int) UserConfig.getHueTargetFPS();
            }

            @Override
            public void run() {
                if (UserConfig.isHueIntegrationEnabled()) {
                    if (hueIntegration.isReady()) {
                        updateHueColor();

                    } else {
                        hueIntegration.start();
                    }

                } else {
                    hueIntegration.stop();
                }
            }
        };

        variableFpsLoopWorker.start();

    }

    private void updateHueColor() {
        List<FrequencyBar> frequencyBarList = hueFFTService.getFrequencyBarList();

        Map<Location, Color> locationColorMap = new LinkedHashMap<>();
        for (Location location: LocationConfig.getLocationList()) {
            if (location.getName().equals("")) {
                continue;
            }

            Color color = globalColorCalculator.getGlobalColor(
                    frequencyBarList,
                    location.getFrequencyStart(),
                    location.getFrequencyEnd(),
                    true);

            locationColorMap.put(location, color);
        }

        hueIntegration.updateHueEntertainment(locationColorMap);

        long newTime = System.currentTimeMillis();
        long deltaTime = newTime - oldTime;
//        System.out.println(deltaTime);
        oldTime = newTime;
    }
}
