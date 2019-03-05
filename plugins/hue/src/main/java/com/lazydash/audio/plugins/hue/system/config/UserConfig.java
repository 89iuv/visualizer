package com.lazydash.audio.plugins.hue.system.config;

public class UserConfig {
    private static boolean hueIntegrationEnabled = false;
    private static String hueEntertainmentName = "Bass";
    private static String hueStatus = "DISCONNECTED";
    private static double hueTargetFPS = 25;

    public static boolean isHueIntegrationEnabled() {
        return hueIntegrationEnabled;
    }

    public static void setHueIntegrationEnabled(boolean hueIntegrationEnabled) {
        UserConfig.hueIntegrationEnabled = hueIntegrationEnabled;
    }

    public static String getHueEntertainmentName() {
        return hueEntertainmentName;
    }

    public static void setHueEntertainmentName(String hueEntertainmentName) {
        UserConfig.hueEntertainmentName = hueEntertainmentName;
    }

    public static String getHueStatus() {
        return hueStatus;
    }

    public static void setHueStatus(String hueStatus) {
        UserConfig.hueStatus = hueStatus;
    }

    public static double getHueTargetFPS() {
        return hueTargetFPS;
    }

    public static void setHueTargetFPS(double hueTargetFPS) {
        UserConfig.hueTargetFPS = hueTargetFPS;
    }
}
