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
    public Spinner<Integer> fftWindowFrames;
    public Spinner<Integer> fftWindowMs;

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

        fftWindowFrames.getValueFactory().setValue(AppConfig.getFftWindowFrames());
        fftWindowFrames.valueProperty().addListener((observable, oldValue, newValue) -> {
            fftWindowFrames.getValueFactory().setValue(newValue);
            AppConfig.setFftWindowFrames(newValue);
        });

        fftWindowMs.getValueFactory().setValue(AppConfig.getFftWindowMs());
        fftWindowMs.valueProperty().addListener((observable, oldValue, newValue) -> {
          fftWindowMs.getValueFactory().setValue(newValue);
            AppConfig.setFftWindowMs(newValue);
        });
    }

    public void updateInputDevice(ActionEvent actionEvent) {
        AppConfig.setInputDevice(inputDevice.getValue());
    }

    public void updateOutputDevice(ActionEvent actionEvent) {
        AppConfig.setOutputDevice(outputDevice.getValue());
    }
}
