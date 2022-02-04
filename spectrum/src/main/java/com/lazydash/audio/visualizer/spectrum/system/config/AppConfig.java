package com.lazydash.audio.visualizer.spectrum.system.config;

public class AppConfig {
    // Audio Input
    public static String inputDevice = "";
    public static int sampleRate = 48000;
    public static int sampleSizeInBits = 16;
    public static int channels = 1;
    public static boolean signed = true;
    public static boolean bigEndian = false;
    public static int audioWindowSize = 30;
    public static int audioWindowNumber = 3;

    // Spectral View
    public static int signalAmplification = 100;
    public static int signalThreshold = -34;
    public static String maxLevel = "RMS";
    public static String weight = "dBZ";
    public static int frequencyStart = 39;
    public static int frequencyCenter = 1000;
    public static int frequencyEnd = 16001;
    public static int octave = 6;
    public static int minBarHeight = 2;
    public static int barGap = 1;

    public static double maxBarHeight = 352;
    public static int hzLabelHeight = 20;

    // spectral color
    public static int spectralColorPosition = 240;
    public static int spectralColorRange = 120;
    public static int saturation = 90;
    public static int brightness = 95;
    public static boolean spectralColorInverted = true;

    // bar acceleration
    public static int millisToZero = 400;
    public static int accelerationFactor = 4;
    public static int timeFilterSize = 2;
    public static String smoothnessType = "WMA";

    // window
    public static double windowWidth = 1700;
    public static double windowHeight = 400;
}
