package com.lazydash.audio.visualizer.core.service;

import com.lazydash.audio.visualizer.core.algorithm.BarsHeightCalculator;
import com.lazydash.audio.visualizer.core.algorithm.FFTTimeFilter;
import com.lazydash.audio.visualizer.core.algorithm.FrequencyBars;
import com.lazydash.audio.visualizer.core.audio.FFTListener;
import com.lazydash.audio.visualizer.core.model.FrequencyBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class FrequencyBarsFFTService implements FFTListener {
    // the hzBins and the amplitudes come in pairs and access to them needs to be synchronized
    private static ReentrantLock lock = new ReentrantLock(true);

    // all of the instances have the same input from the audio dispatcher
    private static double[] hzBins = null;
    private static float[] amplitudes = null;

    private FFTTimeFilter fftTimeFilter = new FFTTimeFilter();
    private BarsHeightCalculator barsHeightCalculator = new BarsHeightCalculator();


    @Override
    public void frame(double[] hzBins, float[] normalizedAmplitudes, double spl) {
        try {
            lock.lock();
            FrequencyBarsFFTService.hzBins = hzBins;
            FrequencyBarsFFTService.amplitudes = normalizedAmplitudes;

        } finally {
            lock.unlock();
        }
    }

    public List<FrequencyBar> getFrequencyBarList(double targetFps) {
        double[] returnBinz;
        float[] returnAmplitudes;
        try {
            lock.lock();
            returnBinz = FrequencyBarsFFTService.hzBins;
            returnAmplitudes = FrequencyBarsFFTService.amplitudes;

        } finally {
            lock.unlock();
        }

        List<FrequencyBar> frequencyBars;
        if (returnAmplitudes != null) {
            returnAmplitudes = fftTimeFilter.filter(returnAmplitudes);
            returnAmplitudes = barsHeightCalculator.processAmplitudes(returnAmplitudes, targetFps);
            frequencyBars = FrequencyBars.createFrequencyBars(returnBinz, returnAmplitudes);

        } else {
            frequencyBars = new ArrayList<>();
        }

        return frequencyBars;

    }

}
