package com.lazydash.audio.spectrum.core.audio;

public interface FFTListener {
    void frame(double[] hzBins, double[] normalizedAmplitudes);
}
