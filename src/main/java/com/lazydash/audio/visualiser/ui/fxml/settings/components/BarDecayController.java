package com.lazydash.audio.visualiser.ui.fxml.settings.components;

import com.lazydash.audio.visualiser.system.config.AppConfig;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class BarDecayController {
    public Slider decayTime;
    public Slider decayAcceleration;
    public Label decayTimeValue;
    public Label decayAccelerationValue;

    public void initialize() {
        decayTimeValue.setText(String.valueOf(AppConfig.getDecayTime()));
        decayTime.setValue(AppConfig.getDecayTime());
        decayTime.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.setDecayTime(newValue.intValue());
                decayTimeValue.setText(String.valueOf(AppConfig.getDecayTime()));
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


    }

}
