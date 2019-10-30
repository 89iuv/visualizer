package com.lazydash.audio.visualizer.spectrum.system.config;

public class AppConfig {
    // ui
    private static double targetFPS = 60;

    // Audio format
    private static String inputDevice = "Primary Sound Capture Driver";
    private static String outputDevice = "Primary Sound Driver";
    private static int sampleRate = 48000;
    private static int sampleSizeInBits = 16;
    private static int channels = 2;
    private static boolean signed = true;
    private static boolean bigEndian = true;

    // audio buffer settings
    private static int fftWindowFrames = 4;
    private static int fftWindowMs = 32;

    private static int minBarHeight = 3;
    private static double maxBarHeight = 750;
    private static int barGap = 1;

    private static int hzLabelHeight = 20;

    private static int frequencyStart = 39;
    private static int frequencyCenter = 1000;
    private static int frequencyEnd = 17000;
    private static int octave = 6;

    private static String weight = "dBZ";

    // spectral color
    private static int spectralColorPosition = 0;
    private static int spectralColorRange = 330;
    private static int saturation = 100;
    private static int brightness = 100;
    private static boolean spectralColorInverted = false;

    // bar acceleration
    private static int pixelsPerSecondDecay = 1200;
    private static int accelerationFactor = 12;

    // input signal
    private static int signalAmplification = 120;
    private static int signalThreshold = -34;

    // fft
    private static int timeFilterSize = 2;
    private static double interpolationResolution = 6;

    // window
    private static double windowWidth = 1700;
    private static double windowHeight = 400;


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

    public static int getFftWindowFrames() {
        return fftWindowFrames;
    }

    public static void setFftWindowFrames(int fftWindowFrames) {
        AppConfig.fftWindowFrames = fftWindowFrames;
    }

    public static int getFftWindowMs() {
        return fftWindowMs;
    }

    public static void setFftWindowMs(int fftWindowMs) {
        AppConfig.fftWindowMs = fftWindowMs;
    }

    public static int getTimeFilterSize() {
        return timeFilterSize;
    }

    public static void setTimeFilterSize(int timeFilterSize) {
        AppConfig.timeFilterSize = timeFilterSize;
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


    public static int getPixelsPerSecondDecay() {
        return pixelsPerSecondDecay;
    }

    public static void setPixelsPerSecondDecay(int pixelsPerSecondDecay) {
        AppConfig.pixelsPerSecondDecay = pixelsPerSecondDecay;
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

    public static int getAccelerationFactor() {
        return accelerationFactor;
    }

    public static void setAccelerationFactor(int accelerationFactor) {
        AppConfig.accelerationFactor = accelerationFactor;
    }

    public static int getFrequencyStart() {
        return frequencyStart;
    }

    public static void setFrequencyStart(int frequencyStart) {
        AppConfig.frequencyStart = frequencyStart;
    }

    public static int getFrequencyEnd() {
        return frequencyEnd;
    }

    public static void setFrequencyEnd(int frequencyEnd) {
        AppConfig.frequencyEnd = frequencyEnd;
    }

    public static int getOctave() {
        return octave;
    }

    public static void setOctave(int octave) {
        AppConfig.octave = octave;
    }

    public static int getFrequencyCenter() {
        return frequencyCenter;
    }

    public static void setFrequencyCenter(int frequencyCenter) {
        AppConfig.frequencyCenter = frequencyCenter;
    }

    public static String getWeight() {
        return weight;
    }

    public static void setWeight(String weight) {
        AppConfig.weight = weight;
    }

    public static int getSpectralColorPosition() {
        return spectralColorPosition;
    }

    public static void setSpectralColorPosition(int spectralColorPosition) {
        AppConfig.spectralColorPosition = spectralColorPosition;
    }

    public static int getSpectralColorRange() {
        return spectralColorRange;
    }

    public static void setSpectralColorRange(int spectralColorRange) {
        AppConfig.spectralColorRange = spectralColorRange;
    }

    public static boolean isSpectralColorInverted() {
        return spectralColorInverted;
    }

    public static void setSpectralColorInverted(boolean spectralColorInverted) {
        AppConfig.spectralColorInverted = spectralColorInverted;
    }

    public static int getSaturation() {
        return saturation;
    }

    public static void setSaturation(int saturation) {
        AppConfig.saturation = saturation;
    }

    public static int getBrightness() {
        return brightness;
    }

    public static void setBrightness(int brightness) {
        AppConfig.brightness = brightness;
    }


    public static double getInterpolationResolution() {
        return interpolationResolution;
    }

    public static void setInterpolationResolution(double interpolationResolution) {
        AppConfig.interpolationResolution = interpolationResolution;
    }

    public static String getOutputDevice() {
        return outputDevice;
    }

    public static void setOutputDevice(String outputDevice) {
        AppConfig.outputDevice = outputDevice;
    }
}
