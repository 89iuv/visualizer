package com.lazydash.audio.spectrum.ui.code.spectral;


import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

public class FrequencyView {
    private Label hzLabel = new Label();
    private Rectangle rectangle = new Rectangle();


    Label getHzLabel() {
        return hzLabel;
    }

    Rectangle getRectangle() {
        return rectangle;
    }

    public void setHzValue(double hz) {
        if (hz >= 10000) {
            this.hzLabel.setText(String.format("%.0f", hz / 1000d) + "K");

        } else if (hz >= 1000) {
            this.hzLabel.setText(String.format("%.1f", hz / 1000d) + "K");

        } else {
            this.hzLabel.setText(String.format("%.0f", hz));
        }

    }
}
