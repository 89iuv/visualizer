package com.lazydash.audio.visualizer.spectrum.core.service;

import com.lazydash.audio.visualizer.spectrum.core.algorithm.BarsHeightCalculator;
import com.lazydash.audio.visualizer.spectrum.core.algorithm.FFTTimeFilter;
import com.lazydash.audio.visualizer.spectrum.core.algorithm.FrequencyBarsCreator;
import com.lazydash.audio.visualizer.spectrum.core.audio.FFTListener;
import com.lazydash.audio.visualizer.spectrum.core.model.FrequencyBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class holds state information regarding to:
 *  - timeFiltering a.k.a smoothness
 *  - previous bar heights that are used in bad decay calculation
 */
public class FrequencyBarsFFTService implements FFTListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(FrequencyBarsFFTService.class);
    private long oldTime = System.currentTimeMillis();

    // the hzBins and the amplitudes come in pairs and access to them needs to be synchronized
    private ReentrantLock lock = new ReentrantLock(true);

    // all of the instances have the same input from the audio dispatcher
    private double[] hzBins = null;
    private double[] amplitudes = null;

    private double[] hzBinsOld = null;
    private double[] amplitudesOld = null;

    private FFTTimeFilter fftTimeFilter = new FFTTimeFilter();
    private BarsHeightCalculator barsHeightCalculator = new BarsHeightCalculator();

    @Override
    public void frame(double[] hzBins, double[] normalizedAmplitudes) {
        try {
            lock.lock();

            if (this.hzBins != null) {
                LOGGER.info("Audio frame dropped");
            }

            this.hzBins = hzBins;
            this.amplitudes = normalizedAmplitudes;

        } finally {
            lock.unlock();
        }
    }

    public List<FrequencyBar> getFrequencyBarList() {
        long oldTime = System.currentTimeMillis();
        double[] returnBinz;
        double[] returnAmplitudes;

        try {
            lock.lock();

            if (this.hzBins == null) {
                returnBinz = this.hzBinsOld;
                returnAmplitudes = this.amplitudesOld;

            } else {
                returnBinz = this.hzBins;
                returnAmplitudes = this.amplitudes;

                this.hzBinsOld = this.hzBins;
                this.amplitudesOld = this.amplitudes;

                this.hzBins = null;
                this.amplitudes = null;
            }

        } finally {
            lock.unlock();
        }

        List<FrequencyBar> frequencyBars = new ArrayList<>();
        if (returnAmplitudes != null) {
            returnAmplitudes = fftTimeFilter.filter(returnAmplitudes);
            returnAmplitudes = barsHeightCalculator.processAmplitudes(returnAmplitudes);
            frequencyBars = FrequencyBarsCreator.createFrequencyBars(returnBinz, returnAmplitudes);
        }

        long newTime = System.currentTimeMillis();
        long deltaTime = newTime - oldTime;

//        LOGGER.info(String.valueOf(deltaTime));

        oldTime = newTime;

        return frequencyBars;

    }

}
