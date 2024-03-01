package com.lazydash.audio.visualizer;

import com.lazydash.audio.visualizer.core.algorithm.TimeFilter;
import com.lazydash.audio.visualizer.system.config.AppConfig;
import org.junit.Assert;
import org.junit.Test;

public class TimeFilterTest {

    @Test
    public void testWeightedMovingAverageOn5Points() {
        AppConfig.smoothnessType = "WMA";
        AppConfig.timeFilterSize = 5;

        TimeFilter timeFilter = new TimeFilter();

        timeFilter.filter(new double[]{91});
        timeFilter.filter(new double[]{90});
        timeFilter.filter(new double[]{89});
        timeFilter.filter(new double[]{88});
        double[] filter = timeFilter.filter(new double[]{90});

        Assert.assertEquals(89.3, filter[0], 0.1);

    }

    @Test
    public void testWeightedMovingAverageOn2Points() {
        AppConfig.smoothnessType = "WMA";
        AppConfig.timeFilterSize = 2;

        TimeFilter timeFilter = new TimeFilter();

        // this is needed because we need to build the buffer first
        timeFilter.filter(new double[]{0});

        timeFilter.filter(new double[]{90});
        double[] filter = timeFilter.filter(new double[]{91});

        Assert.assertEquals(90.6, filter[0],  0.1);

    }
}
