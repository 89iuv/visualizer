package com.lazydash.audio.visualizer.spectrum;

import org.junit.Test;

import java.util.stream.IntStream;

public class RangeTest {

    @Test
    public void run(){
        int[] ints = IntStream.range(0, 10).toArray();

        int low = 3;
        int high = 6;

        IntStream.range(0, 10).filter(value -> low <= value && value <= high ).forEach(System.out::println);

    }

}
