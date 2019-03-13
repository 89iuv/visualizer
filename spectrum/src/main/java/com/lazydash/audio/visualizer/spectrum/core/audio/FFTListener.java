package com.lazydash.audio.visualizer.spectrum.core.audio;

public interface FFTListener {
    void frame(double[] hzBins, double[] normalizedAmplitudes);
}
