package com.lazydash.audio.spectrum.ui.fxml.settings.components;

import com.lazydash.audio.spectrum.system.config.AppConfig;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class SpectralColorController {
    public Slider position;
    public Label positionValue;

    public Slider range;
    public Label rangeValue;


    public void initialize() {
        positionValue.setText(String.valueOf(AppConfig.getSpectralColorPosition()));
        position.setValue(AppConfig.getSpectralColorPosition());
        position.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.setSpectralColorPosition(newValue.intValue());
                positionValue.setText(String.valueOf(Math.round(newValue.intValue())));
            }
        });

        rangeValue.setText(String.valueOf(AppConfig.getSpectralColorRange()));
        range.setValue(AppConfig.getSpectralColorRange());
        range.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.setSpectralColorRange(newValue.intValue());
                rangeValue.setText(String.valueOf(Math.round(newValue.intValue())));
            }
        });

    }

}
