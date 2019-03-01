package com.lazydash.audio.plugins.hue.model;

import java.util.Objects;

public class Location {
    private String name = ""; // default name to show up in interface and to be ignored when processing
    private int frequencyStart;
    private int frequencyEnd;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return frequencyStart == location.frequencyStart &&
                frequencyEnd == location.frequencyEnd &&
                Objects.equals(name, location.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, frequencyStart, frequencyEnd);
    }
}
