package com.lazydash.audio.visualizer.plugins.arduino.core;

import com.fazecast.jSerialComm.SerialPort;
import com.lazydash.audio.visualizer.spectrum.core.model.FrequencyBar;
import com.lazydash.audio.visualizer.spectrum.core.service.FrequencyBarsFFTService;
import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;
import com.lazydash.audio.visualizer.spectrum.system.config.SpectralColorConfig;
import javafx.scene.paint.Color;

import java.util.List;

public class ArduinoManager {
    private FrequencyBarsFFTService frequencyBarsFFTService;

    public ArduinoManager(FrequencyBarsFFTService frequencyBarsFFTService) {
        this.frequencyBarsFFTService = frequencyBarsFFTService;
    }

    public void play() {
        Thread thread = new Thread(() -> {
            run();
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void run(){
        SerialPort com3 = SerialPort.getCommPort("COM3");
        com3.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);
        com3.setBaudRate(115200);
        com3.openPort();

        boolean run = true;
        while (run) {
            byte[] readBytes = new byte["refresh\r\n".getBytes().length];
            com3.readBytes(readBytes, readBytes.length);
            String messageReceived = new String(readBytes);

            if (messageReceived.equals("refresh\r\n")) {
                int ledNumber = 45;
                byte[] bytes = new byte[ledNumber * 3];

                int k = 0;

                List<FrequencyBar> frequencyBarList = frequencyBarsFFTService.getFrequencyBarList();
                for (int i = 0; i < frequencyBarList.size(); i++) {
                    FrequencyBar frequencyBar = frequencyBarList.get(i);

                    double height = frequencyBar.getHeight();
                    double intensity = height / AppConfig.getMaxBarHeight();
                    intensity = intensity * (AppConfig.getBrightness() / 100d);

                    Color baseColor = SpectralColorConfig.baseColor;
                    Color color = frequencyBar.getColor();
                    color = baseColor.interpolate(color, intensity);
                    color = Color.hsb(
                            color.getHue(),
                            color.getSaturation(),
                            intensity
                    );

                    bytes[k++] = (byte) ((char) Math.round(color.getRed() * 255));
                    bytes[k++] = (byte) ((char) Math.round(color.getGreen() * 255));
                    bytes[k++] = (byte) ((char) Math.round(color.getBlue() * 255));


                    if (!(k < ledNumber * 3)) {
                        break;
                    }

                }

                com3.writeBytes(bytes, bytes.length);
            }

        }

        com3.closePort();
    }
}
