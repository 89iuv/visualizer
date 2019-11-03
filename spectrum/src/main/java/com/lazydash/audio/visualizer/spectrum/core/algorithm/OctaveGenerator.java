package com.lazydash.audio.visualizer.spectrum.core.algorithm;

import java.util.*;

public class OctaveGenerator {
    private static Map<OctaveSettings, int[]> cache = new HashMap<>();

    public static int[] getOctaveFrequencies(int centerFrequency, int band, int lowerLimit, int upperLimit) {
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
        int[] octaveCache = cache.get(octaveSettings);
        if (octaveCache == null) {
            Set<Integer> octave = new TreeSet<>();

            addLow(octave, centerFrequency, band, lowerLimit);
            addHigh(octave, centerFrequency, band, upperLimit);

            // if center is 1000 but upper limit is 80 then we need to filter out 80 to 1000 frequencies
            int finalLowerLimit = lowerLimit;
            int finalUpperLimit = upperLimit;
            int[] octaves = octave.stream()
                    .filter(aDouble -> (finalLowerLimit <= aDouble && aDouble <= finalUpperLimit))
                    .mapToInt(value -> value)
                    .toArray();

            cache.put(octaveSettings, octaves);

            return octaves;

        } else {
            return octaveCache;

        }
    }

    public static int getLowLimit(int center, int band){
        return (int)  Math.round(center / (Math.pow(2, ( 1d / (2* band) ))));
    }

    public static int getHighLimit(int center, int band) {
        return (int) Math.round(center * (Math.pow(2, ( 1d / (2* band) ))));
    }

    private static void addLow(Set<Integer> octave, int center, int band, int lowerLimit){
        if (center < lowerLimit) {
            return;
        }

        octave.add(center);

        int fl = (int) Math.round(center / (Math.pow(2, ( 1d / (band) ))));
        addLow(octave, fl, band, lowerLimit);
    }

    private static void addHigh(Set<Integer> octave, int center, int band, int upperLimit){
        if (center > upperLimit) {
            return;
        }

        octave.add(center);

        int fh = (int) Math.round(center * (Math.pow(2, ( 1d / (band) ))));
        addHigh(octave, fh, band, upperLimit);
    }

    private static class OctaveSettings {
        private int centerFrequency;
        private int band;
        private int lowerLimit;
        private int upperLimit;

        OctaveSettings(int centerFrequency, int band, int lowerLimit, int upperLimit) {
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
            return centerFrequency == that.centerFrequency &&
                    band == that.band &&
                    lowerLimit == that.lowerLimit &&
                    upperLimit == that.upperLimit;
        }

        @Override
        public int hashCode() {
            return Objects.hash(centerFrequency, band, lowerLimit, upperLimit);
        }
    }

}
