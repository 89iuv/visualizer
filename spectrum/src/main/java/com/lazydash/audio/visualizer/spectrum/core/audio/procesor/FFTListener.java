package com.lazydash.audio.visualizer.spectrum.core.audio.procesor;

public interface FFTListener {
    void frame(double[] hzBins, double[] normalizedAmplitudes);
}
