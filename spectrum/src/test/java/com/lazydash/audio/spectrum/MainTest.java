package com.lazydash.audio.spectrum;

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
//        List<Double> octaveFrequencies = OctaveGenerator.getOctaveFrequencies(1000, 3, 31, 17000);
//        octaveFrequencies.forEach(System.out::println);

        int center = 1000;
        int band = 3;
        double fl = center / (Math.pow(2, ( 1d / (2* band) )));
        System.out.println(fl);


    }


}
