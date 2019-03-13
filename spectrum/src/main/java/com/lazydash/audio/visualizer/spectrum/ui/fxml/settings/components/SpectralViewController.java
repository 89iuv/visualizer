package com.lazydash.audio.visualizer.spectrum.ui.fxml.settings.components;

import com.lazydash.audio.visualizer.spectrum.core.algorithm.AmplitudeWeightCalculator;
import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SpectralViewController {
    public Slider signalAmplification;
    public Slider signalThreshold;
    public Label signalAmplificationValue;
    public Label signalThresholdValue;
    public Spinner<Integer> frequencyStart;
    public Spinner<Integer> frequencyEnd;
    public Spinner<Integer> octave;
    public Spinner<Integer> frequencyCenter;
    public ComboBox<String> maxLevel;
    public ComboBox<String> weighting;
    public Spinner<Integer> minBarHeight;
    public Spinner<Integer> barGap;

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

        maxLevel.setValue(AppConfig.getMaxLevel());
        maxLevel.getItems().addAll(Arrays.asList("RMS", "Peak"));

        weighting.setValue(AppConfig.getWeight());

        AmplitudeWeightCalculator.WeightWindow[] weightWindows = AmplitudeWeightCalculator.WeightWindow.values();
        List<String> collect = Arrays.stream(weightWindows).map(Enum::toString).collect(Collectors.toList());
        weighting.getItems().addAll(collect);

        minBarHeight.getValueFactory().setValue(AppConfig.getMinBarHeight());
        minBarHeight.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.setMinBarHeight(newValue);
        });

        barGap.getValueFactory().setValue(AppConfig.getBarGap());
        barGap.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.setBarGap(newValue);
        });
    }


    public void updateMaxLeve(ActionEvent actionEvent) {
        AppConfig.setMaxLevel(maxLevel.getValue());
    }

    public void updateWeighting(ActionEvent actionEvent) {
        AppConfig.setWeight(weighting.getValue());
    }
}
