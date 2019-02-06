package com.lazydash.audio.visualizer.core.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class OctaveGenerator {

    public static List<Double> getOctaveFrequencies(double centerFrequency, double band, double lowerLimit, double upperLimit) {
        Set<Double> octave = new TreeSet<>();

        addLow(octave, centerFrequency, band, lowerLimit);
        addHigh(octave, centerFrequency, band, upperLimit);

        return new ArrayList<>(octave);
    }

    private static void addLow(Set<Double> octave, double center, double band, double lowerLimit){
        if (center < lowerLimit) {
            return;
        }

        octave.add(center);

        double fl = center / (Math.pow(2, ( 1d / (2*band) )));
        addLow(octave, fl, band, lowerLimit);
    }

    private static void addHigh(Set<Double> octave, double center, double band, double upperLimit){
        if (center > upperLimit) {
            return;
        }

        octave.add(center);

        double fh = center * (Math.pow(2, ( 1d / (2*band) )));
        addHigh(octave, fh, band, upperLimit);
    }

}
