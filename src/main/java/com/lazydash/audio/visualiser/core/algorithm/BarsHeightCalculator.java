package com.lazydash.audio.visualiser.core.algorithm;

import com.lazydash.audio.visualiser.system.config.AppConfig;

public class BarsHeightCalculator {
    private float[] amplitudes;
    private double[] decay;

    // holds state and modifies it's internal state based on the input
    public float[] processAmplitudes(float[] newAmplitudes) {
        // init on first run or if number of newAmplitudes has changed
        if (amplitudes == null || amplitudes.length != newAmplitudes.length){

            amplitudes = new float[newAmplitudes.length];
            System.arraycopy(newAmplitudes, 0, amplitudes, 0, newAmplitudes.length);

            decay = new double[amplitudes.length];
        }

        for (int i = 0; i<newAmplitudes.length; i++) {
            double oldHeight = amplitudes[i];

            double maxHeight = Math.abs(AppConfig.getSignalThreshold());
            double newHeight = (newAmplitudes[i] + Math.abs(AppConfig.getSignalThreshold()));
            double windowHeight = AppConfig.getMaxBarHeight();

            newHeight = ((newHeight * windowHeight) / maxHeight) * (AppConfig.getSignalAmplification() / 100d);

            // ceiling hit
            if (newHeight > AppConfig.getMaxBarHeight()) {
                newHeight = AppConfig.getMaxBarHeight();
            }

            // below floor
            if (newHeight < AppConfig.getMinBarHeight()) {
                newHeight = AppConfig.getMinBarHeight();
            }

            if (newHeight > oldHeight) {
                // use new height
                amplitudes[i] = (float) newHeight;
                decay[i] = 0;

            // reduce fluctuation introduce because of low sample buffer and double comparison
            } else if (oldHeight - newHeight > 0.01) {
                doDecay(i);

            } // if they are equal do nothing

        }

        return amplitudes;
    }


    private void doDecay(int i){
        double decayFrames = 0;
        if (AppConfig.getDecayTime() > 0) {
            decayFrames = (AppConfig.getMaxBarHeight() * (1000d / AppConfig.getTargetFPS()) / AppConfig.getDecayTime());
        }

        if (decay[i] < 1) {
            decayFrames = decayFrames * decay[i];
            double accelerationStep = AppConfig.getMaxBarHeight() / (AppConfig.getMaxBarHeight() + (AppConfig.getMaxBarHeight() * (AppConfig.getAccelerationFactor())));
            decay[i] = decay[i] + accelerationStep;
        }

        if (amplitudes[i] > AppConfig.getMinBarHeight()) {
            if (amplitudes[i] - decayFrames < AppConfig.getMinBarHeight()) {
                amplitudes[i] = AppConfig.getMinBarHeight();

            } else {
                amplitudes[i] = (float) (amplitudes[i] - decayFrames);
            }

        } else if (amplitudes[i] < AppConfig.getMinBarHeight()) {
            amplitudes[i] = (AppConfig.getMinBarHeight());
        }

    }

}
