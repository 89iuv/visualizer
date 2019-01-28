package com.lazydash.audio.visualiser.core.audio;

public interface FFTListener {
    void frame(double[] hzBins, float[] normalizedAmplitudes, double spl);
}
