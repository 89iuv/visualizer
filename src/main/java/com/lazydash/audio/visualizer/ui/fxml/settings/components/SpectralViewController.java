package com.lazydash.audio.visualizer.ui.fxml.settings.components;

import com.lazydash.audio.visualizer.system.config.AppConfig;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;

public class SpectralViewController {
    public Slider signalAmplification;
    public Slider signalThreshold;
    public Label signalAmplificationValue;
    public Label signalThresholdValue;
    public Spinner<Integer> frequencyStart;
    public Spinner<Integer> frequencyEnd;
    public Spinner<Integer> octave;
    public Spinner<Integer> frequencyCenter;

    public void initialize() {
        signalAmplificationValue.setText(String.valueOf(AppConfig.getSignalAmplification()));
        signalAmplification.setValue(AppConfig.getSignalAmplification());
        signalAmplification.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.setSignalAmplification(newValue.intValue());
                signalAmplificationValue.setText(String.valueOf(AppConfig.getSignalAmplification()));
            }
        });

        signalThresholdValue.setText(String.valueOf(AppConfig.getSignalThreshold()));
        signalThreshold.setValue(AppConfig.getSignalThreshold());
        signalThreshold.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.setSignalThreshold(newValue.intValue());
                signalThresholdValue.setText(String.valueOf(AppConfig.getSignalThreshold()));
            }
        });

        frequencyStart.getValueFactory().setValue(AppConfig.getFrequencyStart());
        frequencyStart.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.setFrequencyStart(newValue);
        });

        frequencyCenter.getValueFactory().setValue(AppConfig.getFrequencyCenter());
        frequencyCenter.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.setFrequencyCenter(newValue);
        });

        frequencyEnd.getValueFactory().setValue(AppConfig.getFrequencyEnd());
        frequencyEnd.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.setFrequencyEnd(newValue);
        });

        frequencyEnd.getValueFactory().setValue(AppConfig.getFrequencyEnd());
        frequencyEnd.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.setFrequencyEnd(newValue);
        });

        octave.getValueFactory().setValue(AppConfig.getOctave());
        octave.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.setOctave(newValue);
        });
    }
}
