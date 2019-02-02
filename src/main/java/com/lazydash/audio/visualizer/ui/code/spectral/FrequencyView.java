package com.lazydash.audio.visualizer.ui.code.spectral;


import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class FrequencyView {
    private Label hzLabel = new Label();
    private Rectangle rectangle = new Rectangle();

    public FrequencyView(int hz, int hzHeight, int height, int width, Color color) {
        this.hzLabel.setText(String.valueOf(hz));
        this.hzLabel.setPrefHeight(hzHeight);
        this.hzLabel.setMinHeight(hzHeight);
        this.hzLabel.setMaxHeight(hzHeight);

        this.rectangle.setHeight(height);
        this.rectangle.setWidth(width);
        this.rectangle.setFill(color);
    }

    public void setHeight(int height) {
        this.rectangle.setHeight(height);
    }

    public void setHz(int hz) {
        this.hzLabel.setText(String.valueOf(hz));
    }

    public void setColor(Color color){
        this.rectangle.setFill(color);
    }

    public Label getHzLabel() {
        return hzLabel;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }
}
