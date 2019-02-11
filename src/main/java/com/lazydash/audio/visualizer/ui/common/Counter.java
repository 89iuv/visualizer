package com.lazydash.audio.visualizer.ui.common;

public class Counter {
    private int value = -1;

    public int getValue() {
        return value;
    }

    public int getIncrement(){
        return ++value;
    }
}
