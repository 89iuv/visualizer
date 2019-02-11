package com.lazydash.audio.visualizer.ui.fxml.settings.common;

public class FxmlCounter {
    private int value = -1;

    public int getValue() {
        return value;
    }

    public int getIncrement(){
        return ++value;
    }
}
