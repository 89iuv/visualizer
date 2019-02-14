package com.lazydash.audio.spectrum.system.persistance;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

public class AppConfigPersistence {

    public static AppConfigPersistence createAndReadConfiguration(Class appConfigClass, String path) {
        AppConfigPersistence appConfigPersistence = new AppConfigPersistence();
        appConfigPersistence.readConfig(appConfigClass ,path);
        return appConfigPersistence;
    }

    private void readConfig(Class appConfigClass, String path) {
        Path applicationPropertiesPath = Paths.get(path);
        if (Files.exists(applicationPropertiesPath)) {

            Properties appProps = new Properties();
            try {
                appProps.load(new FileReader(path));

            } catch (IOException e) {
                e.printStackTrace();
            }

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

    public void persistConfig(Class appConfigClass, String path) {
        Properties appProps = new Properties();

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
            appProps.store(new FileWriter(path), appConfigClass.getName() + " config");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
