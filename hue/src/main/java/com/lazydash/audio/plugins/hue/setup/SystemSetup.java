package com.lazydash.audio.plugins.hue.setup;

import com.philips.lighting.hue.sdk.wrapper.HueLog;
import com.philips.lighting.hue.sdk.wrapper.Persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SystemSetup {
    private String hueDBFolder = "./data/hue/db";
    private String hueDLLFile = "./dll/huesdk.dll";
    private String hueJarFile = "./lib/huecppsdk-wrapper.jar";

    public void runHueConfiguration() {
       checkForHueDll();
       checkForHueJar();
       checkForHueDatabase();

       configureHue();
    }

    private void checkForHueDll(){
        if (Files.notExists(Paths.get(hueDLLFile))) {
            throw new RuntimeException("Missing "+ hueDLLFile +" file");
        }
    }

    private void checkForHueJar(){
        if (Files.notExists(Paths.get(hueJarFile))) {
            throw new RuntimeException("Missing " + hueJarFile + " file");
        }
    }

    private void checkForHueDatabase() {
        if (Files.notExists(Paths.get(hueDBFolder))) {
            createHuePersistenceFolder();
        }
    }

    private void createHuePersistenceFolder(){
        try {
            Files.createDirectory(Paths.get(hueDBFolder));

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Can not create hue database folder");
        }
    }

    private void configureHue(){
        Persistence.setStorageLocation(hueDBFolder, "device");
        HueLog.setConsoleLogLevel(HueLog.LogLevel.INFO);
    }
}
