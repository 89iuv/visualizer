package com.lazydash.audio.plugins.hue.model;

import com.philips.lighting.hue.sdk.wrapper.entertainment.Area;

import java.util.LinkedHashMap;
import java.util.Map;

public class HueAreaMap {
    private static Map<String, Area> nameToArea = new LinkedHashMap<>();

    static {
        nameToArea.put("All", Area.Predefine.All);
        nameToArea.put("Front", Area.Predefine.Front);
        nameToArea.put("FrontHalfLeft", Area.Predefine.FrontHalfLeft);
        nameToArea.put("FrontHalfRight", Area.Predefine.FrontHalfRight);
        nameToArea.put("Right", Area.Predefine.Right);
        nameToArea.put("Back", Area.Predefine.Back);
        nameToArea.put("BackHalfLeft", Area.Predefine.BackHalfLeft);
        nameToArea.put("BackHalfRight", Area.Predefine.BackHalfRight);
        nameToArea.put("Left", Area.Predefine.Left);

    }

    public static Map<String, Area> getNameToArea() {
        return nameToArea;
    }
}
