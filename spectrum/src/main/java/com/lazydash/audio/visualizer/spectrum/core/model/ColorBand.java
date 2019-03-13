package com.lazydash.audio.visualizer.spectrum.core.model;

import javafx.scene.paint.Color;

public class ColorBand {
    private Color startColor;
    private Color endColor;
    private int startHz;
    private int endHz;

    public ColorBand(Color startColor, Color endColor, int startHz, int endHz) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.startHz = startHz;
        this.endHz = endHz;
    }

    public Color getStartColor() {
        return startColor;
    }

    public void setStartColor(Color startColor) {
        this.startColor = startColor;
    }

    public Color getEndColor() {
        return endColor;
    }

    public void setEndColor(Color endColor) {
        this.endColor = endColor;
    }

    public int getStartHz() {
        return startHz;
    }

    public void setStartHz(int startHz) {
        this.startHz = startHz;
    }

    public int getEndHz() {
        return endHz;
    }

    public void setEndHz(int endHz) {
        this.endHz = endHz;
    }
}
