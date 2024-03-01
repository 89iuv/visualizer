package com.lazydash.audio.visualizer.core.audio;

public interface FFTListener {
    void frame(double[] hzBins, double[] normalizedAmplitudes);
}
