package com.lazydash.audio.visualizer.ui.fxml.settings.components;

import com.lazydash.audio.visualizer.system.config.AppConfig;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;

public class BarDecayController {
    public Slider decayTime;
    public Slider decayAcceleration;
    public Label decayTimeValue;
    public Label decayAccelerationValue;
    public Spinner<Integer> minBarHeight;
    public Spinner<Integer> timeFilter;

    public void initialize() {
        decayTimeValue.setText(String.valueOf(AppConfig.getDecayTime()));
        decayTime.setValue(AppConfig.getDecayTime());
        decayTime.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.setDecayTime(newValue.intValue());
                decayTimeValue.setText(String.valueOf(Math.round(AppConfig.getDecayTime() / 10d) * 10));
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

        minBarHeight.getValueFactory().setValue(AppConfig.getMinBarHeight());
        minBarHeight.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.setMinBarHeight(newValue);
        });
    }

}
