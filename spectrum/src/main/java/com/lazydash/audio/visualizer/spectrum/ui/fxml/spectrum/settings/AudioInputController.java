package com.lazydash.audio.visualizer.spectrum.ui.fxml.spectrum.settings;

import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

import javax.sound.sampled.AudioSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AudioInputController {
    public ComboBox<String> inputDevice;
    public ComboBox<String> outputDevice;
    public Spinner<Integer> bufferPadding;
    public Spinner<Integer> bufferSize;

    public void initialize() {
        List<String> inputDeviceList = new ArrayList<>(0);
        Stream.of(AudioSystem.getMixerInfo()).forEach(mixerInfo -> {
            if (mixerInfo.getDescription().contains("Capture")) {
                inputDeviceList.add(mixerInfo.getName());
            }
        });
        inputDevice.getItems().addAll(inputDeviceList);
        inputDevice.setValue(AppConfig.getInputDevice());

        List<String> outputDeviceList = new ArrayList<>(0);
        Stream.of(AudioSystem.getMixerInfo()).forEach(mixerInfo -> {
            if (mixerInfo.getDescription().contains("Playback")) {
                outputDeviceList.add(mixerInfo.getName());
            }
        });
        outputDevice.getItems().addAll(outputDeviceList);
        outputDevice.setValue(AppConfig.getOutputDevice());

        bufferPadding.getValueFactory().setValue(AppConfig.getBufferPadding());
        bufferPadding.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (bufferSize.getValue() > newValue - 256) {
                bufferSize.getValueFactory().setValue(newValue - 256);
                AppConfig.setBufferSize(newValue - 256);
            }

            bufferPadding.getValueFactory().setValue(newValue);
            AppConfig.setBufferPadding(newValue);
        });

        bufferSize.getValueFactory().setValue(AppConfig.getBufferSize());
        bufferSize.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue > bufferPadding.getValue() - 256) {
                newValue = newValue - 256;
            }

            if (bufferPadding.getValue() - 256 < 0) {
                newValue = 0;
            }

            bufferSize.getValueFactory().setValue(newValue);
            AppConfig.setBufferSize(newValue);
        });

    }

    public void updateInputDevice(ActionEvent actionEvent) {
        AppConfig.setInputDevice(inputDevice.getValue());
    }

    public void updateOutputDevice(ActionEvent actionEvent) {
        AppConfig.setOutputDevice(outputDevice.getValue());
    }
}
