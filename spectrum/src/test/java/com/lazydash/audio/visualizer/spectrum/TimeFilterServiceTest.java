package com.lazydash.audio.visualizer.spectrum;

import com.lazydash.audio.visualizer.spectrum.core.algorithm.TimeFilterService;
import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;
import org.junit.Assert;
import org.junit.Test;

public class TimeFilterServiceTest {

    @Test
    public void testWeightedMovingAverageOn5Points() {
        AppConfig.smoothnessType = "WMA";
        AppConfig.timeFilterSize = 5;

        TimeFilterService timeFilterService = new TimeFilterService();

        timeFilterService.filter(new double[]{91});
        timeFilterService.filter(new double[]{90});
        timeFilterService.filter(new double[]{89});
        timeFilterService.filter(new double[]{88});
        double[] filter = timeFilterService.filter(new double[]{90});

        Assert.assertEquals(89.3, filter[0], 0.1);

    }

    @Test
    public void testWeightedMovingAverageOn2Points() {
        AppConfig.smoothnessType = "WMA";
        AppConfig.timeFilterSize = 2;

        TimeFilterService timeFilterService = new TimeFilterService();

        // this is needed because we need to build the buffer first
        timeFilterService.filter(new double[]{0});

        timeFilterService.filter(new double[]{90});
        double[] filter = timeFilterService.filter(new double[]{91});

        Assert.assertEquals(90.6, filter[0],  0.1);

    }
}
