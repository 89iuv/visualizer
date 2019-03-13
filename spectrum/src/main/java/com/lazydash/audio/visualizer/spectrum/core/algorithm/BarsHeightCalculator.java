package com.lazydash.audio.visualizer.spectrum.core.algorithm;

import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;

public class BarsHeightCalculator {
    private long oldTime = System.nanoTime();
    private double[] oldAmplitudes;
    private double[] oldDecayFactor;

    public double[] processAmplitudes(double[] newAmplitudes) {
        // init on first run or if number of newAmplitudes has changed
        if (oldAmplitudes == null || oldAmplitudes.length != newAmplitudes.length) {
            oldAmplitudes = newAmplitudes;
            oldDecayFactor = new double[newAmplitudes.length];

            return convertDbToPixels(newAmplitudes);
        }

        int pixelsPerSecondDecay = AppConfig.getPixelsPerSecondDecay();
        double secondsPassed = getSecondsPassed();

        double[] pixelAmplitudes = convertDbToPixels(newAmplitudes);
        oldAmplitudes = decayPixelsAmplitudes(oldAmplitudes, pixelAmplitudes, pixelsPerSecondDecay, secondsPassed);

        return oldAmplitudes;
    }

    private double[] convertDbToPixels(double[] dbAmplitude) {
        int signalThreshold = AppConfig.getSignalThreshold();
        double maxBarHeight = AppConfig.getMaxBarHeight();
        int signalAmplification = AppConfig.getSignalAmplification();
        int minBarHeight = AppConfig.getMinBarHeight();

        double[] pixelsAmplitude = new double[dbAmplitude.length];

        for (int i = 0; i < pixelsAmplitude.length; i++) {
            double maxHeight = Math.abs(signalThreshold);

            double newHeight = dbAmplitude[i];
            newHeight = newHeight + Math.abs(signalThreshold);
            newHeight = newHeight * (signalAmplification / 100d);
            newHeight = (newHeight * maxBarHeight) / maxHeight;
//            newHeight = Math.round(newHeight);

            // apply limits
            if (newHeight > maxBarHeight) {
                // ceiling hit
                newHeight = maxBarHeight;

            } else if (newHeight < minBarHeight) {
                // below floor
                newHeight = minBarHeight;
            }

            pixelsAmplitude[i] = newHeight;
        }

        return pixelsAmplitude;
    }

    private double[] decayPixelsAmplitudes(double[] oldAmplitudes, double[] newAmplitudes, double pixelsPerSecond, double secondsPassed) {
        double[] processedAmplitudes = new double[newAmplitudes.length];

        for (int i = 0; i < processedAmplitudes.length; i++) {
            double oldHeight = oldAmplitudes[i];
            double newHeight = newAmplitudes[i];

            if (newHeight >= oldHeight) {
                processedAmplitudes[i] = newHeight;
                oldDecayFactor[i] = 0;

            } else {
//                double dbPerSecondDecay = (pixelsPerSecond + (0.01 * Math.pow(1.15, i) - 0.01)) * secondsPassed; // experiment with logarithmic function
                double dbPerSecondDecay = pixelsPerSecond * secondsPassed;

                if (AppConfig.getAccelerationFactor() > 0 && oldDecayFactor[i] < 1) {
                    double accelerationStep = 1d / AppConfig.getAccelerationFactor();
                    oldDecayFactor[i] = oldDecayFactor[i] + accelerationStep;
                    dbPerSecondDecay = dbPerSecondDecay * oldDecayFactor[i];
                }

                if (newHeight > oldHeight - dbPerSecondDecay) {
                    processedAmplitudes[i] = newHeight;
                    oldDecayFactor[i] = 0;

                } else {
                    processedAmplitudes[i] = oldHeight - dbPerSecondDecay;

                }
            }
        }

        return processedAmplitudes;
    }

    private double getSecondsPassed() {
        long newTime = System.nanoTime();
        long deltaTime = newTime - oldTime;
        oldTime = newTime;
        // convert nano to ms to seconds
        return (deltaTime / 1000000d) / 1000d;
    }
}
