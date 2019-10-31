package com.lazydash.audio.visualizer.spectrum.core.algorithm;

import java.util.*;
import java.util.stream.Collectors;

public class OctaveGenerator {
    private static Map<OctaveSettings, List<Double>> cache = new HashMap<>();

    public static List<Double> getOctaveFrequencies(double centerFrequency, double band, double lowerLimit, double upperLimit) {
        // set limits
        if (lowerLimit < 1) {
            lowerLimit = 1;
        }

        if (upperLimit < 1) {
            upperLimit = 1;
        }

        if (centerFrequency < 1) {
            centerFrequency = 1;
        }

        OctaveSettings octaveSettings = new OctaveSettings(centerFrequency, band, lowerLimit, upperLimit);
        List<Double> doubles = cache.get(octaveSettings);
        if (doubles == null) {
            Set<Double> octave = new TreeSet<>();

            addLow(octave, centerFrequency, band, lowerLimit);
            addHigh(octave, centerFrequency, band, upperLimit);

            // if center is 1000 but upper limit is 80 then we need to filter out 80 to 1000 frequencies
            double finalLowerLimit = lowerLimit;
            double finalUpperLimit = upperLimit;
            List<Double> octaves = octave.stream().filter(aDouble -> (finalLowerLimit <= aDouble && aDouble <= finalUpperLimit)).collect(Collectors.toList());
            cache.put(octaveSettings, octaves);

            return octaves;

        } else {
            return doubles;

        }
    }

    public static double getLowLimit(double center, double band){
        return  center / (Math.pow(2, ( 1d / (2* band) )));
    }

    public static double getHighLimit(double center, double band) {
        return center * (Math.pow(2, ( 1d / (2* band) )));
    }

    private static void addLow(Set<Double> octave, double center, double band, double lowerLimit){
        if (center < lowerLimit) {
            return;
        }

        octave.add(center);

        double fl = center / (Math.pow(2, ( 1d / (band) )));
        addLow(octave, fl, band, lowerLimit);
    }

    private static void addHigh(Set<Double> octave, double center, double band, double upperLimit){
        if (center > upperLimit) {
            return;
        }

        octave.add(center);

        double fh = center * (Math.pow(2, ( 1d / (band) )));
        addHigh(octave, fh, band, upperLimit);
    }

    private static class OctaveSettings {
        private double centerFrequency;
        private double band;
        private double lowerLimit;
        private double upperLimit;

        OctaveSettings(double centerFrequency, double band, double lowerLimit, double upperLimit) {
            this.centerFrequency = centerFrequency;
            this.band = band;
            this.lowerLimit = lowerLimit;
            this.upperLimit = upperLimit;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OctaveSettings that = (OctaveSettings) o;
            return Double.compare(that.centerFrequency, centerFrequency) == 0 &&
                    Double.compare(that.band, band) == 0 &&
                    Double.compare(that.lowerLimit, lowerLimit) == 0 &&
                    Double.compare(that.upperLimit, upperLimit) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(centerFrequency, band, lowerLimit, upperLimit);
        }
    }

}
