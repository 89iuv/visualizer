package com.lazydash.audio.visualizer.plugins.hue.system.setup;

import com.lazydash.audio.visualizer.plugins.hue.system.config.AppConfig;
import com.philips.lighting.hue.sdk.wrapper.HueLog;
import com.philips.lighting.hue.sdk.wrapper.Persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Setup {
    private String hueDBFolder;

    public void checkHueEnvironment() {
        checkForHueDll();
        checkForHueJar();
        checkForHueDatabase();
    }

    public void runHueConfig(){
        configureHue();
    }

    private void checkForHueDll() {
        String hueDLLFile = "./dll/huesdk.dll";
        if (Files.notExists(Paths.get(hueDLLFile))) {
            throw new RuntimeException("Missing " + hueDLLFile + " file");
        }
    }

    private void checkForHueJar() {
        String hueJarFile = "./lib/huecppsdk-wrapper.jar";
        if (Files.notExists(Paths.get(hueJarFile))) {
            throw new RuntimeException("Missing " + hueJarFile + " file");
        }
    }

    private void checkForHueDatabase() {
        hueDBFolder = "./data/" + AppConfig.getPluginId() + "/db";
        if (Files.notExists(Paths.get(hueDBFolder))) {
            createHuePersistenceFolder();
        }
    }

    private void createHuePersistenceFolder() {
        try {
            Files.createDirectories(Paths.get(hueDBFolder));

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Can not create hue database folder");
        }
    }

    private void configureHue() {
        Persistence.setStorageLocation(hueDBFolder, "device");
        HueLog.setConsoleLogLevel(HueLog.LogLevel.INFO);
    }
}
