package com.lazydash.audio.plugins.hue.system.config;

import com.lazydash.audio.plugins.hue.model.Location;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LocationConfig {
    private static List<Location> locationList = new CopyOnWriteArrayList<>();

    public static List<Location> getLocationList() {
        return locationList;
    }
}
