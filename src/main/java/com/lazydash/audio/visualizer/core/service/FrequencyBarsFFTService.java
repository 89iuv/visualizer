package com.lazydash.audio.visualizer.core.service;

import com.lazydash.audio.visualizer.core.algorithm.BarsHeightCalculator;
import com.lazydash.audio.visualizer.core.algorithm.FrequencyBarsCreator;
import com.lazydash.audio.visualizer.core.algorithm.TimeFilter;
import com.lazydash.audio.visualizer.core.audio.FFTListener;
import com.lazydash.audio.visualizer.core.model.FrequencyBar;
import com.lazydash.audio.visualizer.ui.model.DebugPropertiesService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class holds state information regarding:
 *  - timeFiltering a.k.a smoothness
 *  - previous bar heights that are used in bad decay calculation
 */
public class FrequencyBarsFFTService implements FFTListener {

    private final DebugPropertiesService debugPropertiesService = DebugPropertiesService.getInstance();

    // the hzBins and the amplitudes come in pairs and access to them needs to be synchronized
    private final ReentrantLock lock = new ReentrantLock(true);

    // all the instances have the same input from the audio dispatcher
    private double[] hzBins = null;
    private double[] amplitudes = null;

    private double[] hzBinsOld = null;
    private double[] amplitudesOld = null;

    private final TimeFilter timeFilter = new TimeFilter();
    private final BarsHeightCalculator barsHeightCalculator = new BarsHeightCalculator();

    private long t0 = System.currentTimeMillis();
    private long t1 = 0;
    private long dt = 1000;

    private int frameMerged = 0;
    private int sameFrameTaken = 0;
    private int frameTaken = 0;


    // this is used from audio dispatcher thread
    @Override
    public void frame(double[] hzBins, double[] normalizedAmplitudes) {
        try {
            lock.lock();

            if (this.hzBins != null) {
                frameMerged++;

                // average with last value so that we do not lose information
                for (int i = 0; i<this.hzBins.length; i++) {
                    this.amplitudes[i] = (this.amplitudes[i] + normalizedAmplitudes[i]) / 2d;
                }

            } else {
                this.amplitudes = normalizedAmplitudes;
            }

            this.hzBins = hzBins;

        } finally {
            lock.unlock();
        }
    }

    // this is used from javafx thread
    public List<FrequencyBar> getFrequencyBarList() {
        double[] returnBinz;
        double[] returnAmplitudes;

        try {
            lock.lock();

            if (this.hzBins == null) {
                sameFrameTaken++;

                returnBinz = this.hzBinsOld;
                returnAmplitudes = this.amplitudesOld;

            } else {
                frameTaken++;
                sameFrameTaken++;

                returnBinz = this.hzBins;
                returnAmplitudes = this.amplitudes;

                this.hzBinsOld = this.hzBins;
                this.amplitudesOld = this.amplitudes;

                this.hzBins = null;
                this.amplitudes = null;
            }

            t1 = System.currentTimeMillis();
            if (t1 - t0 > dt) {
                debugPropertiesService.getAvgSameAudioFrameFromUiFramePerSecond().setValue((double) sameFrameTaken / frameTaken);
                debugPropertiesService.getAudioFramesMergedPerSecond().setValue(frameMerged);
                frameMerged = 0;
                sameFrameTaken = 0;
                frameTaken = 0;
                t0 = t1;
            }

        } finally {
            lock.unlock();
        }

        List<FrequencyBar> frequencyBars = new ArrayList<>();
        if (returnAmplitudes != null) {
            // setting this at the signal level otherwise it will impact other functionality
            returnAmplitudes = timeFilter.filter(returnAmplitudes);

            returnAmplitudes = barsHeightCalculator.processAmplitudes(returnAmplitudes);
            frequencyBars = FrequencyBarsCreator.createFrequencyBars(returnBinz, returnAmplitudes);
        }

        return frequencyBars;
    }

}
