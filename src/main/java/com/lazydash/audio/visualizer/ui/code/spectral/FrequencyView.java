package com.lazydash.audio.visualizer.ui.code.spectral;


import javafx.scene.control.Label;
import javafx.scene.paint.Color;
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

    public void setHzHeight(double hzHeight) {
        this.getHzLabel().setMinHeight(hzHeight);

    }

    public void setHzValue(double hz) {
        if (hz >= 1000) {
            this.hzLabel.setText(String.format("%.1f",hz / 1000d) + "K");
        } else {
            this.hzLabel.setText(String.format("%.0f", hz));
        }

    }

    public void setBarHeight(double height) {
        this.rectangle.setHeight(Math.round(height));
    }


    public void setBarColor(Color color){
        this.rectangle.setFill(color);
    }
}
