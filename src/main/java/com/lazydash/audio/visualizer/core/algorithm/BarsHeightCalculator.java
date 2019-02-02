package com.lazydash.audio.visualizer.core.algorithm;

import com.lazydash.audio.visualizer.system.config.AppConfig;

public class BarsHeightCalculator {
    private float[] amplitudes;
    private double[] decay;

    // holds state and modifies it's internal state based on the input
    public float[] processAmplitudes(float[] newAmplitudes, double targetFPS) {
        // init on first run or if number of newAmplitudes has changed
        if (amplitudes == null || amplitudes.length != newAmplitudes.length) {
            amplitudes = new float[newAmplitudes.length];
            System.arraycopy(newAmplitudes, 0, amplitudes, 0, newAmplitudes.length);

            decay = new double[amplitudes.length];
        }

        for (int i = 0; i < newAmplitudes.length; i++) {
            double oldHeight = amplitudes[i];

            double maxHeight = Math.abs(AppConfig.getSignalThreshold());
            double newHeight = (newAmplitudes[i] + Math.abs(AppConfig.getSignalThreshold()));
            double windowHeight = AppConfig.getMaxBarHeight();

            newHeight =(((newHeight * windowHeight) / maxHeight) * (AppConfig.getSignalAmplification() / 10d));

            // apply limits
            if (newHeight > AppConfig.getMaxBarHeight()) {
                // ceiling hit
                newHeight = AppConfig.getMaxBarHeight();

            } else if (newHeight < AppConfig.getMinBarHeight()) {
                // below floor
                newHeight = AppConfig.getMinBarHeight();
            }

            int decayDeltaTreshold = 3;
            if (newHeight - oldHeight > decayDeltaTreshold) {
                // use new height
                amplitudes[i] = (float) newHeight;
                decay[i] = 0;

            } else if (oldHeight - newHeight > decayDeltaTreshold) {
                // only do the decay if the new height is bellow the minimum decay frames in order to not flicker.
                doDecay(i, targetFPS);

            } else if (newHeight <= decayDeltaTreshold) {
                amplitudes[i] = AppConfig.getMinBarHeight();

            }
        }

        return amplitudes;
    }


    private void doDecay(int i, double targetFPS) {
        double decayFrames = 0;
        if (AppConfig.getDecayTime() > 0) {
            decayFrames = (AppConfig.getMaxBarHeight() * (1000d / targetFPS) / AppConfig.getDecayTime());
        }

        if (decay[i] < 1) {
            double accelerationStep = (decayFrames / (decayFrames * AppConfig.getAccelerationFactor()));
            decay[i] = decay[i] + accelerationStep;
            decayFrames = decayFrames * decay[i];
        }

        if (amplitudes[i] - decayFrames < AppConfig.getMinBarHeight()) {
            amplitudes[i] = AppConfig.getMinBarHeight();

        } else {
            amplitudes[i] = (float) (amplitudes[i] - decayFrames);
        }
    }
}
