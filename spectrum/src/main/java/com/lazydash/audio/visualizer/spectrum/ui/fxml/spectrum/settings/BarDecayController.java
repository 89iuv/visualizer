package com.lazydash.audio.visualizer.spectrum.ui.fxml.spectrum.settings;

import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;

public class BarDecayController {
    public Slider pixelsPerSecondDecay;
    public Slider decayAcceleration;
    public Label decayTimeValue;
    public Label decayAccelerationValue;
    public Spinner<Integer> timeFilter;

    public void initialize() {
        decayTimeValue.setText(String.valueOf(AppConfig.getPixelsPerSecondDecay()));
        pixelsPerSecondDecay.setValue(AppConfig.getPixelsPerSecondDecay());
        pixelsPerSecondDecay.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.setPixelsPerSecondDecay(newValue.intValue());
                decayTimeValue.setText(String.valueOf(newValue.intValue()));
            }
        });

        decayAccelerationValue.setText(String.valueOf(AppConfig.getAccelerationFactor()));
        decayAcceleration.setValue(AppConfig.getAccelerationFactor());
        decayAcceleration.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.setAccelerationFactor(newValue.intValue());
                decayAccelerationValue.setText(String.valueOf(AppConfig.getAccelerationFactor()));
            }
        });

        timeFilter.getValueFactory().setValue(AppConfig.getTimeFilterSize());
        timeFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.setTimeFilterSize(newValue);
        });
    }

}
