package com.lazydash.audio.spectrum.ui.fxml.common;

/*
    Convenience class to auto increment grid rows and columns in FXML context
 */
public class FxmlCounter {
    private int value = -1;

    public int getValue() {
        return value;
    }

    public int getIncrement(){
        return ++value;
    }
}
