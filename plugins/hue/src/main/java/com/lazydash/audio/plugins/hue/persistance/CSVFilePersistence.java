package com.lazydash.audio.plugins.hue.persistance;


import com.lazydash.audio.plugins.hue.model.Location;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CSVFilePersistence {
    private static String cvsSplitBy = ",";

    public static List<Location> load(String path){
        List<Location> locations = new ArrayList<>();

        if (!Files.exists(Paths.get(path))) {
            return locations;
        }


        try (BufferedReader br = Files.newBufferedReader(Paths.get(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineArray = line.split(cvsSplitBy);

                Location location = new Location();
                location.setFrequencyStart(Integer.valueOf(lineArray[0]));
                location.setFrequencyEnd(Integer.valueOf(lineArray[1]));
                location.setName(lineArray[2]);

                locations.add(location);
            }


            return locations;

        } catch (IOException e) {
            e.printStackTrace();

            return locations;
        }

    }

    public static void persist(List<Location> locations, String path){
       try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(path))) {
            locations.forEach(location -> {
                String locationLine = location.getFrequencyStart() + cvsSplitBy
                        + location.getFrequencyEnd() + cvsSplitBy
                        + location.getName()
                        + System.lineSeparator();
                try {
                    bufferedWriter.write(locationLine);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
