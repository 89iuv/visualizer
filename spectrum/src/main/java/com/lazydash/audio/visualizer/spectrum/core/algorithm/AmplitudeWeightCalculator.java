package com.lazydash.audio.visualizer.spectrum.core.algorithm;

public class AmplitudeWeightCalculator {

    public static double getDbWeight(double frequency, WeightWindow weight) {
        switch (weight) {
            case dBA:
                return getDbA(frequency);

            case dBB:
                return getDbB(frequency);

            case dBC:
                return getDbC(frequency);

            default:
                // dbz, no weight
                return 0;
        }
    }

    public static double getDbA(double frequency){
        double f = frequency;

        double raf = (Math.pow(12194, 2) * Math.pow(f, 4))
                / ((Math.pow(f, 2) + Math.pow(20.6, 2))
                * Math.sqrt(
                (Math.pow(f, 2) + Math.pow(107.7, 2))
                        * (Math.pow(f, 2) + Math.pow(737.9, 2))
        )
                * (Math.pow(f, 2) + Math.pow(12194, 2))
        );

        double af = 20 * Math.log10(raf) + 2.00;

        return af;
    }

    public static double getDbB(double frequency){
        double f = frequency;

        double rbf = (Math.pow(12194, 2) * Math.pow(f, 3))
                / (
                    (Math.pow(f, 2) + Math.pow(20.6, 2))
                    * Math.sqrt(
                        Math.pow(f, 2) + Math.pow(158.5, 2)
                    )
                    * (Math.pow(f, 2) + Math.pow(12194, 2))
                );

        double bf = 20 * Math.log10(rbf) + 0.17;

        return bf;
    }

    public static double getDbC(double frequency){
        double f = frequency;

        double rcf = (Math.pow(12194, 2) * Math.pow(f, 2))
                / (
                (Math.pow(f, 2) + Math.pow(20.6, 2))
                    * (Math.pow(f, 2) + Math.pow(12194, 2))
        );

        double cf = 20 * Math.log10(rcf) + 0.06;

        return cf;
    }

    public enum WeightWindow {
        dBA("dBA"),
        dBB("dBB"),
        dBC("dBC"),
        dBZ("dBZ");

        private final String window;

        WeightWindow(String window) {
            this.window = window;
        }

        public String getWindow() {
            return window;
        }
    }
}
