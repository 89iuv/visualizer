package com.lazydash.audio.visualizer.ui.code.spectral;


import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;


public class FrequencyView {
    private Label hzLabel = new Label();
    private Rectangle rectangle = new Rectangle();

    public FrequencyView(int hz, int hzHeight, int height, int width) {
        this.hzLabel.setText(String.valueOf(hz));
        this.hzLabel.setPrefHeight(hzHeight);
        this.hzLabel.setMinHeight(hzHeight);
        this.hzLabel.setMaxHeight(hzHeight);

        this.rectangle.setHeight(height);
        this.rectangle.setWidth(width);
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public Label getHzLabel() {
        return hzLabel;
    }

    public void setHzLabel(Label hzLabel) {
        this.hzLabel = hzLabel;
    }

}
