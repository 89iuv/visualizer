package com.lazydash.audio.visualizer.spectrum;

/*
double min = 15;
       double max = 24000;

        int bands = 32;
        for (int i = 0; i < bands; i++) {

           System.out.println(min * Math.pow((max / min), i/(double) bands));

       }
 */
public class MainTest {

    public static void main(String[] args) {
        double min = 35;
        double max = 17000;

        int bands = 45;
        for (int i = 0; i < bands; i++) {

            System.out.println(min * Math.pow((max / min), i/(double) bands));

        }
    }


}
