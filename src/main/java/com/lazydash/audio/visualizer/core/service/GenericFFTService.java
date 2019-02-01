package com.lazydash.audio.visualizer.core.service;

import com.lazydash.audio.visualizer.core.algorithm.BarsHeightCalculator;
import com.lazydash.audio.visualizer.core.algorithm.FFTTimeFilter;
import com.lazydash.audio.visualizer.core.audio.FFTListener;
import com.lazydash.audio.visualizer.system.config.AppConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class GenericFFTService implements FFTListener {
    private ReentrantLock lock = new ReentrantLock(true);

    private double[] hzBins = null;
    private float[] amplitudes = null;


    private FFTTimeFilter fftTimeFilter = new FFTTimeFilter();
    private BarsHeightCalculator barsHeightCalculator = new BarsHeightCalculator();


    @Override
    public void frame(double[] hzBins, float[] normalizedAmplitudes, double spl) {
        try {
            lock.lock();

            double[] truncatedBins = new double[AppConfig.getBarNumber()];
            System.arraycopy(hzBins, AppConfig.getBarOffset(), truncatedBins, 0, truncatedBins.length);
            this.hzBins = truncatedBins;

            float[] truncatedAmplitudes = new float[AppConfig.getBarNumber()];
            System.arraycopy(normalizedAmplitudes, AppConfig.getBarOffset(), truncatedAmplitudes, 0, truncatedAmplitudes.length);

            amplitudes = truncatedAmplitudes;

        } finally {
            lock.unlock();
        }
    }

    public Map<double[], float[]> getBinsToAmplitudesMap() {
        try {
            lock.lock();

            double[] returnBinz = this.hzBins;
            float[] returnAmplitudes = this.amplitudes;

            if (returnAmplitudes != null) {
                returnAmplitudes = fftTimeFilter.filter(returnAmplitudes);
                returnAmplitudes = barsHeightCalculator.processAmplitudes(returnAmplitudes);

            }

            Map<double[], float[]> binsToAmplitudesMap = new HashMap<>();
            binsToAmplitudesMap.put(returnBinz, returnAmplitudes);

            return binsToAmplitudesMap;

        } finally {
            lock.unlock();
        }
    }

}
