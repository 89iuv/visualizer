package com.lazydash.audio.visualizer.spectrum.ui.fxml.spectrum.settings;

import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class SpectralColorController {
    public Slider position;
    public Label positionValue;

    public Slider range;
    public Label rangeValue;

    public Slider saturation;
    public Label saturationValue;

    public Slider brightness;
    public Label brightnessValue;
    public CheckBox invert;


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

        saturationValue.setText(String.valueOf(AppConfig.getSaturation()));
        saturation.setValue(AppConfig.getSaturation());
        saturation.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.setSaturation(newValue.intValue());
                saturationValue.setText(String.valueOf(Math.round(newValue.intValue())));
            }
        });

        brightnessValue.setText(String.valueOf(AppConfig.getBrightness()));
        brightness.setValue(AppConfig.getBrightness());
        brightness.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.setBrightness(newValue.intValue());
                brightnessValue.setText(String.valueOf(Math.round(newValue.intValue())));
            }
        });

        invert.setSelected(AppConfig.isSpectralColorInverted());
        invert.setText(AppConfig.isSpectralColorInverted()? "yes" : "no");
        invert.setOnAction(actionEvent -> {
            AppConfig.setSpectralColorInverted(invert.isSelected());
            invert.setText(AppConfig.isSpectralColorInverted()? "yes" : "no");
        });

    }

}
