package com.lazydash.audio.spectrum.core.service;

import com.lazydash.audio.spectrum.core.algorithm.BarsHeightCalculator;
import com.lazydash.audio.spectrum.core.algorithm.FFTTimeFilter;
import com.lazydash.audio.spectrum.core.algorithm.FrequencyBarsCreator;
import com.lazydash.audio.spectrum.core.audio.FFTListener;
import com.lazydash.audio.spectrum.core.model.FrequencyBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class FrequencyBarsFFTService implements FFTListener {
    // the hzBins and the amplitudes come in pairs and access to them needs to be synchronized
    private ReentrantLock lock = new ReentrantLock(true);

    // all of the instances have the same input from the audio dispatcher
    private double[] hzBins = null;
    private float[] amplitudes = null;

    private FFTTimeFilter fftTimeFilter = new FFTTimeFilter();
    private BarsHeightCalculator barsHeightCalculator = new BarsHeightCalculator();


    @Override
    public void frame(double[] hzBins, float[] normalizedAmplitudes) {
        try {
            lock.lock();
            this.hzBins = hzBins;
            this.amplitudes = normalizedAmplitudes;

        } finally {
            lock.unlock();
        }
    }

    public List<FrequencyBar> getFrequencyBarList(double targetFps) {
        double[] returnBinz;
        float[] returnAmplitudes;

        try {
            lock.lock();
            returnBinz = this.hzBins;
            returnAmplitudes = this.amplitudes;

        } finally {
            lock.unlock();
        }

        List<FrequencyBar> frequencyBars;
        if (returnAmplitudes != null) {
            returnAmplitudes = fftTimeFilter.filter(returnAmplitudes);
            returnAmplitudes = barsHeightCalculator.processAmplitudes(returnAmplitudes, targetFps);
            frequencyBars = FrequencyBarsCreator.createFrequencyBars(returnBinz, returnAmplitudes);

        } else {
            // return empty array
            frequencyBars = new ArrayList<>();
        }

        return frequencyBars;

    }

}
