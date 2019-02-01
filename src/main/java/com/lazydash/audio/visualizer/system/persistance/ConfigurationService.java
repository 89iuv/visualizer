package com.lazydash.audio.visualizer.system.persistance;

import com.lazydash.audio.visualizer.system.config.AppConfig;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

public class ConfigurationService {
    private String applicationProperties = "application.properties";

    public static ConfigurationService createAndReadConfiguration() {
        ConfigurationService configurationService = new ConfigurationService();
        configurationService.readConfig();
        return configurationService;
    }

    private void readConfig() {
        Path applicationPropertiesPath = Paths.get(applicationProperties);
        if (Files.exists(applicationPropertiesPath)) {

            Properties appProps = new Properties();
            try {
                appProps.load(new FileReader(applicationProperties));

            } catch (IOException e) {
                e.printStackTrace();
            }

            Class appConfigClass = AppConfig.class;

            Field[] fields = appConfigClass.getDeclaredFields();
            Stream.of(fields).forEach(field -> {
                try {
                    field.setAccessible(true);

                    if (field.getType().toString().equals("class java.lang.String")) {
                        field.set(null, appProps.getProperty(field.getName()));

                    } else if (field.getType().toString().equals("int")){
                        field.setInt(null, Integer.valueOf(appProps.getProperty(field.getName())));

                    }  else if (field.getType().toString().equals("boolean")){
                        field.setBoolean(null, Boolean.valueOf(appProps.getProperty(field.getName())));

                    } else if (field.getType().toString().equals("double")){
                        field.setDouble(null, Double.valueOf(appProps.getProperty(field.getName())));

                    }

                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    field.setAccessible(false);
                }
            });
        }
    }

    public void persistConfig() {
        Properties appProps = new Properties();

        Class appConfigClass = AppConfig.class;
        Field[] fields = appConfigClass.getDeclaredFields();
        Stream.of(fields).forEach(field -> {
            try {
                field.setAccessible(true);
                appProps.setProperty(field.getName(), String.valueOf(field.get(null)));
                field.setAccessible(false);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });


        try {
            appProps.store(new FileWriter(applicationProperties), "Bass Frequency Analyzer");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
