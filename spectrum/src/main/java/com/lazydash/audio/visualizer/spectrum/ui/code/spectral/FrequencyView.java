package com.lazydash.audio.visualizer.spectrum.ui.code.spectral;


import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class FrequencyView {
    private Label hzLabel = new Label();
    private Rectangle rectangle = new Rectangle();

    public FrequencyView() {
    }

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
