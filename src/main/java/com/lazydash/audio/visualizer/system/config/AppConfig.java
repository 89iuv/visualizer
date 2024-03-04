package com.lazydash.audio.visualizer.system.config;

public class AppConfig {
    // Audio Input
    public static String inputDevice = "";
    public static int sampleRate = 48000;
    public static int sampleSizeInBits = 16;
    public static int channels = 1;
    public static boolean signed = true;
    public static boolean bigEndian = false;
    public static int audioWindowSize = 33;
    public static int audioWindowNumber = 3;

    // Spectral View
    public static int signalAmplification = 100;
    public static int signalThreshold = -35;
    public static String weight = "dBZ";
    public static int frequencyStart = 30;
    public static int frequencyCenter = 1000;
    public static int frequencyEnd = 19000;
    public static int octave = 6;
    public static int minBarHeight = 2;
    public static int barGap = 1;

    public static double maxBarHeight = 352;
    public static int hzLabelHeight = 20;

    // spectral color
    public static int spectralColorPosition = 360;
    public static int spectralColorRange = 360;
    public static int saturation = 100;
    public static int brightness = 95;
    public static boolean spectralColorInverted = false;
    public static double opacity = 100;
    public static String textColor = "#000000";

    // bar acceleration
    public static int millisToZero = 380;
    public static int accelerationFactor = 6;
    public static int timeFilterSize = 2;
    public static String smoothnessType = "WMA";
    public static int motionBlur = 40;

    // window
    public static boolean windowDecorations = true;
    public static double windowWidth = 1700;
    public static double windowHeight = 400;
    public static double windowX = -1;
    public static double windowY = -1;
    public static boolean enableHoverOpacity = false;
    public static double hoverOpacity = 100;
    public static boolean enableAlwaysOnTop = false;
}
