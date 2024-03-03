package com.lazydash.audio.visualizer.ui.fxml.settings.categories;

import com.lazydash.audio.visualizer.core.algorithm.AmplitudeWeightCalculator;
import com.lazydash.audio.visualizer.system.config.AppConfig;
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
    public ComboBox<String> weighting;
    public Spinner<Integer> minBarHeight;
    public Spinner<Integer> barGap;

    public void initialize() {
        signalAmplificationValue.setText(String.valueOf(AppConfig.signalAmplification));
        signalAmplification.setValue(AppConfig.signalAmplification);
        signalAmplification.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.signalAmplification = newValue.intValue();
                signalAmplificationValue.setText(String.valueOf(AppConfig.signalAmplification));
            }
        });

        signalThresholdValue.setText(String.valueOf(AppConfig.signalThreshold));
        signalThreshold.setValue(AppConfig.signalThreshold);
        signalThreshold.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.signalThreshold = newValue.intValue();
                signalThresholdValue.setText(String.valueOf(AppConfig.signalThreshold));
            }
        });

        frequencyStart.getValueFactory().setValue(AppConfig.frequencyStart);
        frequencyStart.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.frequencyStart = newValue;
        });

        frequencyCenter.getValueFactory().setValue(AppConfig.frequencyCenter);
        frequencyCenter.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.frequencyCenter = newValue;
        });

        frequencyEnd.getValueFactory().setValue(AppConfig.frequencyEnd);
        frequencyEnd.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.frequencyEnd = newValue;
        });

        octave.getValueFactory().setValue(AppConfig.octave);
        octave.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.octave = newValue;
        });

        weighting.setValue(AppConfig.weight);

        AmplitudeWeightCalculator.WeightWindow[] weightWindows = AmplitudeWeightCalculator.WeightWindow.values();
        List<String> collect = Arrays.stream(weightWindows).map(Enum::toString).collect(Collectors.toList());
        weighting.getItems().addAll(collect);

        minBarHeight.getValueFactory().setValue(AppConfig.minBarHeight);
        minBarHeight.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.minBarHeight = newValue;
        });

        barGap.getValueFactory().setValue(AppConfig.barGap);
        barGap.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.barGap = newValue;
        });
    }

    public void updateWeighting(ActionEvent actionEvent) {
        AppConfig.weight = weighting.getValue();
    }
}
