package com.lazydash.audio.plugins.hue.system.config;

public class AppConfig {
    private static String pluginId = "hue";

    public static String getPluginId() {
        return pluginId;
    }

    public static void setPluginId(String pluginId) {
        AppConfig.pluginId = pluginId;
    }
}
