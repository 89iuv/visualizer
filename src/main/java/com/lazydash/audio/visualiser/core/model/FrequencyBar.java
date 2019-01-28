package com.lazydash.audio.visualiser.core.model;

import javafx.scene.paint.Color;

public class FrequencyBar {
    private double hz;
    private double height;
    private Color color;

    public FrequencyBar(double hz, double height, Color color) {
        this.hz = hz;
        this.height = height;
        this.color = color;
    }

    public double getHz() {
        return hz;
    }

    public void setHz(double hz) {
        this.hz = hz;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
