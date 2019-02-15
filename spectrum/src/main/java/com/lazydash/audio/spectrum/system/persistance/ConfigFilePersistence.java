package com.lazydash.audio.spectrum.system.persistance;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

public class ConfigFilePersistence {

    public void load(Class appConfigClass, String path) {
        try {
            if (Files.exists(Paths.get(path))) {
                Properties appProps = new Properties();
                appProps.load(new FileReader(path));

                Field[] fields = appConfigClass.getDeclaredFields();
                Stream.of(fields).forEach(field -> {
                    setPropertyOnFiled(appProps, field);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(Class appConfigClass, InputStream inputStream) {
        try {
            Properties appProps = new Properties();
            appProps.load(inputStream);

            Field[] fields = appConfigClass.getDeclaredFields();
            Stream.of(fields).forEach(field -> {
                setPropertyOnFiled(appProps, field);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void persist(Class appConfigClass, String path) {
        try {
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

            appProps.store(new FileWriter(path), appConfigClass.getName() + " config");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setPropertyOnFiled(Properties appProps, Field field) {
        try {
            field.setAccessible(true);
            switch (field.getType().toString()) {
                case "class java.lang.String":
                    field.set(null, appProps.getProperty(field.getName()));
                    break;

                case "int":
                    field.setInt(null, Integer.valueOf(appProps.getProperty(field.getName())));
                    break;

                case "boolean":
                    field.setBoolean(null, Boolean.valueOf(appProps.getProperty(field.getName())));
                    break;

                case "double":
                    field.setDouble(null, Double.valueOf(appProps.getProperty(field.getName())));
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            field.setAccessible(false);
        }
    }

}
