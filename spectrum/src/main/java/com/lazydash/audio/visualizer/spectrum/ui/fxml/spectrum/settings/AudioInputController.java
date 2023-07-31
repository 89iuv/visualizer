package com.lazydash.audio.visualizer.spectrum.ui.fxml.spectrum.settings;

import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class AudioInputController {
    public ComboBox<String> inputDevice;
    public Spinner<Integer> audioWindowSize;
    public Spinner<Integer> audioWindowNumber;

    public void initialize() {
        List<String> inputDeviceList = new ArrayList<>(0);
        Stream.of(AudioSystem.getMixerInfo()).forEach(mixerInfo -> {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            Stream.of(mixer.getTargetLineInfo()).forEach(mixerLineInfo -> {
                try {
                    // try to get access to the mixer line
                    // if successful ad the mixer name to the list
                    TargetDataLine line = (TargetDataLine) mixer.getLine(mixerLineInfo);
                    inputDeviceList.add(mixerInfo.getName());

                } catch (Exception e) {
                    // skip mixer if line can not be obtained
                }
            });
        });

        inputDevice.getItems().addAll(inputDeviceList);
        inputDevice.setValue(AppConfig.inputDevice.equals("") ? inputDevice.getItems().get(0) : AppConfig.inputDevice);
        inputDevice.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.inputDevice = newValue;
        });

        audioWindowSize.getValueFactory().setValue(AppConfig.audioWindowSize);
        audioWindowSize.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.audioWindowSize = newValue;
        });

        audioWindowNumber.getValueFactory().setValue(AppConfig.audioWindowNumber);
        audioWindowNumber.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.audioWindowNumber = newValue;
        });

    }

    public void updateInputDevice(ActionEvent actionEvent) {
        AppConfig.inputDevice = inputDevice.getValue();
    }

}
