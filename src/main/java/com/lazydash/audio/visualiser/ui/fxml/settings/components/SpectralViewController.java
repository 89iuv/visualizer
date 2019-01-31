package com.lazydash.audio.visualiser.ui.fxml.settings.components;

import com.lazydash.audio.visualiser.system.config.AppConfig;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;

public class SpectralViewController {

    public Spinner<Integer> barNumber;
    public Spinner<Integer> barOffset;
    public Spinner<Integer> minBarHeight;
    public Slider signalAmplification;
    public Slider signalThreshold;
    public Label signalAmplificationValue;
    public Label signalThresholdValue;

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

        barNumber.getValueFactory().setValue(AppConfig.getBarNumber());
        barNumber.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.setBarNumber(newValue);
        });

        barOffset.getValueFactory().setValue(AppConfig.getBarOffset());
        barOffset.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.setBarOffset(newValue);
        });

        minBarHeight.getValueFactory().setValue(AppConfig.getMinBarHeight());
        minBarHeight.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.setMinBarHeight(newValue);
        });
    }
}
