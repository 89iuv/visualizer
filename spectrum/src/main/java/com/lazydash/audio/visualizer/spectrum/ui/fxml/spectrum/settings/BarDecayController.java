package com.lazydash.audio.visualizer.spectrum.ui.fxml.spectrum.settings;

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

public class BarDecayController {
    public Slider pixelsPerSecondDecay;
    public Slider decayAcceleration;
    public Label decayTimeValue;
    public Label decayAccelerationValue;
    public Spinner<Integer> timeFilter;
    public ComboBox<String> smoothnessType;
    public Slider motionBlur;
    public Label motionBlurValue;

    public void initialize() {
        decayTimeValue.setText(String.valueOf(AppConfig.millisToZero));
        pixelsPerSecondDecay.setValue(AppConfig.millisToZero);
        pixelsPerSecondDecay.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.millisToZero = newValue.intValue();
                decayTimeValue.setText(String.valueOf(newValue.intValue()));
            }
        });

        decayAccelerationValue.setText(String.valueOf(AppConfig.accelerationFactor));
        decayAcceleration.setValue(AppConfig.accelerationFactor);
        decayAcceleration.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.accelerationFactor = newValue.intValue();
                decayAccelerationValue.setText(String.valueOf(AppConfig.accelerationFactor));
            }
        });

        timeFilter.getValueFactory().setValue(AppConfig.timeFilterSize);
        timeFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.timeFilterSize = newValue;
        });

        List<String> smoothnessTypeList = Arrays.asList("SMA", "WMA", "EMA");
        smoothnessType.getItems().addAll(smoothnessTypeList);
        smoothnessType.setValue(AppConfig.smoothnessType);
        smoothnessType.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.smoothnessType = newValue;
        });

        motionBlurValue.setText(String.valueOf(AppConfig.motionBlur));
        motionBlur.setValue(AppConfig.motionBlur);
        motionBlur.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.motionBlur = newValue.intValue();
                motionBlurValue.setText(String.valueOf(AppConfig.motionBlur));
            }
        });

    }

    public void updateSmoothnessType(ActionEvent actionEvent) {
        AppConfig.smoothnessType = smoothnessType.getValue();
    }
}
