package com.lazydash.audio.visualizer.spectrum.core.audio;

import java.util.List;

public interface AudioEngine {
    List<FFTListener> getFttListenerList();

    void start();

    void stop();

    void restart();
}
