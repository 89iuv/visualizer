package com.lazydash.audio.visualiser.core.service;

import com.lazydash.audio.visualiser.core.audio.FFTListener;
import com.lazydash.audio.visualiser.system.config.AppConfig;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractFFTService implements FFTListener {
    private ReentrantLock lock = new ReentrantLock(true);
    private int barNumber = AppConfig.getBarNumber();

    private double[] hzBins = null;
    private float[] amplitudes = null;

    private Queue<double[]> hzBinsQue = new CircularFifoQueue<>(2);
    private Queue<float[]> amplitudeQue = new CircularFifoQueue<>(2);

    @Override
    public void frame(double[] hzBins, float[] normalizedAmplitudes, double spl) {
        try {
            lock.lock();
            if (barNumber != AppConfig.getBarNumber()) {
                barNumber = AppConfig.getBarNumber();
                amplitudeQue.clear();
                hzBinsQue.clear();
            }

            double[] truncatedBins = new double[AppConfig.getBarNumber()];
            System.arraycopy(hzBins, AppConfig.getBarOffset(), truncatedBins, 0, truncatedBins.length);
            hzBinsQue.offer(truncatedBins);
            this.hzBins = truncatedBins;

            float[] truncatedAmplitudes = new float[AppConfig.getBarNumber()];
            System.arraycopy(normalizedAmplitudes, AppConfig.getBarOffset(), truncatedAmplitudes, 0, truncatedAmplitudes.length);

            amplitudeQue.offer(truncatedAmplitudes);
            amplitudes = truncatedAmplitudes;

        } finally {
            lock.unlock();
        }
    }

    public Map<double[], float[]> getBinsToAmplitudesMap() {
        try {
            lock.lock();
            double[] returnBinz = hzBinsQue.poll();
            if (returnBinz == null) {
                returnBinz = this.hzBins;
            }

            float[] returnAmplitudes = amplitudeQue.poll();
            if (returnAmplitudes == null) {
                returnAmplitudes = this.amplitudes;
            }

            Map<double[], float[]> binsToAmplitudesMap = new HashMap<>();
            binsToAmplitudesMap.put(returnBinz, returnAmplitudes);

            return binsToAmplitudesMap;

        } finally {
            lock.unlock();
        }
    }

}
