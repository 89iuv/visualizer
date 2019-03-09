package com.lazydash.audio.plugins.hue.model;

public class Location {
    private String name = "";
    private int frequencyStart;
    private int frequencyEnd;
    private String peak = "";

    public Location() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFrequencyStart() {
        return frequencyStart;
    }

    public void setFrequencyStart(int frequencyStart) {
        this.frequencyStart = frequencyStart;
    }

    public int getFrequencyEnd() {
        return frequencyEnd;
    }

    public void setFrequencyEnd(int frequencyEnd) {
        this.frequencyEnd = frequencyEnd;
    }

    public String getPeak() {
        return peak;
    }

    public void setPeak(String peak) {
        this.peak = peak;
    }
}
