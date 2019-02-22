package com.lazydash.audio.spectrum;

import com.lazydash.audio.spectrum.core.algorithm.AmplitudeWeightCalculator;

public class MainTest {

    public static void main(String[] args) {

        double f = 10;

        double af = AmplitudeWeightCalculator.getDbB(f);

        System.out.println(String.format("%.2f", af));

    }


}
