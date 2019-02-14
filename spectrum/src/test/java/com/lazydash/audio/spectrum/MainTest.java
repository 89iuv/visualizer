package com.lazydash.audio.spectrum;

import be.tarsos.dsp.util.CubicSplineFast;

public class MainTest {

    public static void main(String[] args) {
        CubicSplineFast cubicSplineFast = new CubicSplineFast(new double[]{1, 2, 4, 5}, new double[]{1, 2, 2, 1});
        System.out.println(cubicSplineFast.interpolate(2));


    }



}
