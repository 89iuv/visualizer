package com.lazydash.audio.visualiser.system.config;

public class AppConfig {
    // ui
    private static double targetFPS = 60;

    // Audio format
    private static String inputDevice = "Primary Sound Capture";
    private static int sampleRate = 48000;
    private static int sampleSizeInBits = 16;
    private static int channels = 1;
    private static boolean signed = true;
    private static boolean bigEndian = false;

    // audio buffer settings
    private static int bufferSize = 6000;
    private static int bufferOverlap = 4976;

    // spectral view
    private static int barNumber = 12;
    private static int barOffset = 4;

    private static int minBarHeight = 3;
    private static double maxBarHeight = 750;
    private static int barWidth = 40;
    private static int barGap = 2;

    private static int hzLabelHeight = 20;
    private static int globalColorHeight = 40;

    // bar acceleration
    private static int decayTime = 400;
    private static int accelerationFactor = 20;

    // input signal
    private static int signalAmplification = 16;
    private static int signalThreshold = -34;

    // fft
    private static int timeFilterSize = 2;

    // hue
    private static boolean hueIntegrationEnabled = false;
    private static String hueEntertainmentName = "Bass";
    private static String hueStatus = "DISCONNECT";

    // window
    private static double windowWidth = 500;
    private static double windowHeight = 850;

    public static int getSampleRate() {
        return sampleRate;
    }

    public static void setSampleRate(int sampleRate) {
        AppConfig.sampleRate = sampleRate;
    }

    public static int getSampleSizeInBits() {
        return sampleSizeInBits;
    }

    public static void setSampleSizeInBits(int sampleSizeInBits) {
        AppConfig.sampleSizeInBits = sampleSizeInBits;
    }

    public static int getChannels() {
        return channels;
    }

    public static void setChannels(int channels) {
        AppConfig.channels = channels;
    }

    public static boolean isSigned() {
        return signed;
    }

    public static void setSigned(boolean signed) {
        AppConfig.signed = signed;
    }

    public static boolean isBigEndian() {
        return bigEndian;
    }

    public static void setBigEndian(boolean bigEndian) {
        AppConfig.bigEndian = bigEndian;
    }

    public static int getBufferSize() {
        return bufferSize;
    }

    public static void setBufferSize(int bufferSize) {
        AppConfig.bufferSize = bufferSize;
    }

    public static int getBufferOverlap() {
        return bufferOverlap;
    }

    public static void setBufferOverlap(int bufferOverlap) {
        AppConfig.bufferOverlap = bufferOverlap;
    }

    public static int getTimeFilterSize() {
        return timeFilterSize;
    }

    public static void setTimeFilterSize(int timeFilterSize) {
        AppConfig.timeFilterSize = timeFilterSize;
    }

    public static int getBarNumber() {
        return barNumber;
    }

    public static void setBarNumber(int barNumber) {
        AppConfig.barNumber = barNumber;
    }

    public static int getBarOffset() {
        return barOffset;
    }

    public static void setBarOffset(int barOffset) {
        AppConfig.barOffset = barOffset;
    }

    public static int getBarGap() {
        return barGap;
    }

    public static void setBarGap(int barGap) {
        AppConfig.barGap = barGap;
    }

    public static int getMinBarHeight() {
        return minBarHeight;
    }

    public static void setMinBarHeight(int minBarHeight) {
        AppConfig.minBarHeight = minBarHeight;
    }

    public static double getMaxBarHeight() {
        return maxBarHeight;
    }

    public static void setMaxBarHeight(double maxBarHeight) {
        AppConfig.maxBarHeight = maxBarHeight;
    }

    public static int getSignalAmplification() {
        return signalAmplification;
    }

    public static void setSignalAmplification(int signalAmplification) {
        AppConfig.signalAmplification = signalAmplification;
    }

    public static int getSignalThreshold() {
        return signalThreshold;
    }

    public static void setSignalThreshold(int signalThreshold) {
        AppConfig.signalThreshold = signalThreshold;
    }

    public static boolean isHueIntegrationEnabled() {
        return hueIntegrationEnabled;
    }

    public static void setHueIntegrationEnabled(boolean hueIntegrationEnabled) {
        AppConfig.hueIntegrationEnabled = hueIntegrationEnabled;
    }

    public static int getDecayTime() {
        return decayTime;
    }

    public static void setDecayTime(int decayTime) {
        AppConfig.decayTime = decayTime;
    }

    public static String getHueEntertainmentName() {
        return hueEntertainmentName;
    }

    public static void setHueEntertainmentName(String hueEntertainmentName) {
        AppConfig.hueEntertainmentName = hueEntertainmentName;
    }

    public static int getBarWidth() {
        return barWidth;
    }

    public static void setBarWidth(int barWidth) {
        AppConfig.barWidth = barWidth;
    }

    public static int getHzLabelHeight() {
        return hzLabelHeight;
    }

    public static void setHzLabelHeight(int hzLabelHeight) {
        AppConfig.hzLabelHeight = hzLabelHeight;
    }

    public static double getWindowWidth() {
        return windowWidth;
    }

    public static void setWindowWidth(double windowWidth) {
        AppConfig.windowWidth = windowWidth;
    }

    public static double getWindowHeight() {
        return windowHeight;
    }

    public static void setWindowHeight(double windowHeight) {
        AppConfig.windowHeight = windowHeight;
    }

    public static String getInputDevice() {
        return inputDevice;
    }

    public static void setInputDevice(String inputDevice) {
        AppConfig.inputDevice = inputDevice;
    }

    public static double getTargetFPS() {
        return targetFPS;
    }

    public static void setTargetFPS(double targetFPS) {
        AppConfig.targetFPS = targetFPS;
    }

    public static int getGlobalColorHeight() {
        return globalColorHeight;
    }

    public static void setGlobalColorHeight(int globalColorHeight) {
        AppConfig.globalColorHeight = globalColorHeight;
    }

    public static int getAccelerationFactor() {
        return accelerationFactor;
    }

    public static void setAccelerationFactor(int accelerationFactor) {
        AppConfig.accelerationFactor = accelerationFactor;
    }

    public static String getHueStatus() {
        return hueStatus;
    }

    public static void setHueStatus(String hueStatus) {
        AppConfig.hueStatus = hueStatus;
    }
}
