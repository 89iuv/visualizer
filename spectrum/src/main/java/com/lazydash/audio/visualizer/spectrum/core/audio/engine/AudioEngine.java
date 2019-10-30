package com.lazydash.audio.visualizer.spectrum.core.audio.engine;

import com.lazydash.audio.visualizer.spectrum.core.audio.procesor.FFTListener;

import java.util.List;

public interface AudioEngine {
    List<FFTListener> getFttListenerList();

    void start();

    void stop();

    void restart();
}
