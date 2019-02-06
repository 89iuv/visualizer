package com.lazydash.audio.visualizer.ui.fxml.settings.components;

import com.lazydash.audio.visualizer.system.config.AppConfig;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

import javax.sound.sampled.AudioSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class AudioInputController {
    public ComboBox<String> inputDevice;
    public ComboBox<Integer> sampleRate;
    public Spinner<Integer> bufferSize;
    public Spinner<Integer> bufferOverlap;
    public Spinner<Integer> zeroPadding;

    public void initialize() {
        List<String> inputDeviceList = new ArrayList<>(0);
        Stream.of(AudioSystem.getMixerInfo()).forEach(mixerInfo -> {
            if (mixerInfo.getDescription().contains("Capture")) {
                inputDeviceList.add(mixerInfo.getName());
            }
        });
        inputDevice.getItems().addAll(inputDeviceList);
        inputDevice.setValue(AppConfig.getInputDevice());

        List<Integer> sampleRateList = Arrays.asList(44100, 48000);
        sampleRate.getItems().addAll(sampleRateList);
        sampleRate.setValue(AppConfig.getSampleRate());

        bufferSize.getValueFactory().setValue(AppConfig.getBufferSize());
        bufferSize.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue % 2 != 0) {
                if (newValue > 12288) {
                    newValue = newValue - 1;

                } else {
                    newValue = newValue + 1;
                }
            }

            if (bufferOverlap.getValue() > newValue - 256) {
                bufferOverlap.getValueFactory().setValue(newValue - 256);
                AppConfig.setBufferOverlap(newValue - 256);
            }

            bufferSize.getValueFactory().setValue(newValue);
            AppConfig.setBufferSize(newValue);
        });

        bufferOverlap.getValueFactory().setValue(AppConfig.getBufferOverlap());
        bufferOverlap.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue % 2 != 0) {
                if (newValue > 12256) {
                    newValue = newValue - 1;

                } else {
                    newValue = newValue + 1;
                }
            }

            if (newValue > bufferSize.getValue() - 256) {
                newValue = newValue - 256;
            }

            if (bufferSize.getValue() - 256 < 0) {
                newValue = 0;
            }

            bufferOverlap.getValueFactory().setValue(newValue);
            AppConfig.setBufferOverlap(newValue);
        });

        zeroPadding.getValueFactory().setValue(AppConfig.getZeroPadding());
        zeroPadding.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue % 2 != 0) {
                if (newValue > 12256) {
                    newValue = newValue - 1;

                } else {
                    newValue = newValue + 1;
                }
            }

            zeroPadding.getValueFactory().setValue(newValue);
            AppConfig.setZeroPadding(newValue);
        });

    }

    public void updateInputDevice(ActionEvent actionEvent) {
        AppConfig.setInputDevice(inputDevice.getValue());
    }

    public void updateSampleRate(ActionEvent actionEvent) {
        AppConfig.setSampleRate(sampleRate.getValue());
    }

}
