package com.lazydash.audio.visualizer.core.algorithm;

import com.lazydash.audio.visualizer.system.config.AppConfig;

public class BarsHeightCalculator {
    private long oldTime = System.nanoTime();
    private double[] oldAmplitudes;
    private double[] oldDecayDecelSize;

    public double[] processAmplitudes(double[] newAmplitudes) {
        // init on first run or if number of newAmplitudes has changed
        if (oldAmplitudes == null || oldAmplitudes.length != newAmplitudes.length) {
            oldAmplitudes = newAmplitudes;
            oldDecayDecelSize = new double[newAmplitudes.length];

            return convertDbToPixels(newAmplitudes);
        }

        int millisToZero = AppConfig.millisToZero;
        double millisPassed = getMillisPassed();

        double[] pixelAmplitudes = convertDbToPixels(newAmplitudes);
        oldAmplitudes = decayPixelsAmplitudes(oldAmplitudes, pixelAmplitudes, millisToZero, millisPassed);

        return oldAmplitudes;
    }

    private double[] convertDbToPixels(double[] dbAmplitude) {
        int signalThreshold = AppConfig.signalThreshold;
        double maxBarHeight = AppConfig.maxBarHeight;
        int signalAmplification = AppConfig.signalAmplification;

        double[] pixelsAmplitude = new double[dbAmplitude.length];

        for (int i = 0; i < pixelsAmplitude.length; i++) {
            double maxHeight = Math.abs(signalThreshold);

            double newHeight = dbAmplitude[i];
            newHeight = newHeight + Math.abs(signalThreshold);
            // normalizing the bar to the height of the window
            newHeight = (newHeight * maxBarHeight) / maxHeight;
            newHeight = newHeight * (signalAmplification / 100d);

            pixelsAmplitude[i] = newHeight;
        }

        return pixelsAmplitude;
    }

    private double[] decayPixelsAmplitudes(double[] oldAmplitudes, double[] newAmplitudes, double millisToZero, double millisPassed) {
        double[] processedAmplitudes = new double[newAmplitudes.length];
        double maxBarHeight = AppConfig.maxBarHeight;
        int minBarHeight = AppConfig.minBarHeight;

        for (int i = 0; i < processedAmplitudes.length; i++) {
            double oldHeight = oldAmplitudes[i];
            double newHeight = newAmplitudes[i];

            double decayRatePixelsPerMilli = maxBarHeight / millisToZero;
            double decaySize = decayRatePixelsPerMilli * millisPassed;

            if (newHeight < oldHeight) {
                double accelerationStep = (1d / AppConfig.accelerationFactor) * decaySize;
                double nextAccelerationSize = oldDecayDecelSize[i] + accelerationStep;
                if (nextAccelerationSize < decaySize) {
                    decaySize = nextAccelerationSize;
                    oldDecayDecelSize[i] = nextAccelerationSize;
                }

                processedAmplitudes[i] = Math.max(oldHeight - decaySize, newHeight);

            } else {
                processedAmplitudes[i] = newHeight;
                oldDecayDecelSize[i] = 0;
            }

            // apply limits
            if (processedAmplitudes[i] < minBarHeight) {
                // below floor
                processedAmplitudes[i] = minBarHeight;
            }
        }

        return processedAmplitudes;
    }

    private double getMillisPassed() {
        long newTime = System.nanoTime();
        long deltaTime = newTime - oldTime;
        oldTime = newTime;
        // convert nano to ms
        return (deltaTime / 1000000d);
    }
}
