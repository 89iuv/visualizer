package com.lazydash.audio.spectrum.ui.fxml.settings.components;

import com.lazydash.audio.spectrum.system.config.AppConfig;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;

public class BarDecayController {
    public Slider dbPerSecondDecay;
    public Slider decayAcceleration;
    public Label decayTimeValue;
    public Label decayAccelerationValue;
    public Spinner<Integer> timeFilter;

    public void initialize() {
        decayTimeValue.setText(String.valueOf(AppConfig.getDbPerSecondDecay()));
        dbPerSecondDecay.setValue(AppConfig.getDbPerSecondDecay());
        dbPerSecondDecay.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.setDbPerSecondDecay(newValue.intValue());
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
