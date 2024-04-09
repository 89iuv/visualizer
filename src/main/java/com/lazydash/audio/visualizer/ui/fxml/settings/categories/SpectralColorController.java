package com.lazydash.audio.visualizer.ui.fxml.settings.categories;

import com.lazydash.audio.visualizer.system.config.AppConfig;
import com.lazydash.audio.visualizer.ui.model.WindowPropertiesService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;

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
    public ColorPicker textColorPicker;
    public Label opacityValue;
    public Slider opacity;


    public void initialize() {
        positionValue.setText(String.valueOf(AppConfig.spectralColorPosition));
        position.setValue(AppConfig.spectralColorPosition);
        position.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.spectralColorPosition = newValue.intValue();
                positionValue.setText(String.valueOf(Math.round(newValue.intValue())));
            }
        });

        rangeValue.setText(String.valueOf(AppConfig.spectralColorRange));
        range.setValue(AppConfig.spectralColorRange);
        range.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.spectralColorRange = newValue.intValue();
                rangeValue.setText(String.valueOf(Math.round(newValue.intValue())));
            }
        });

        saturationValue.setText(String.valueOf(AppConfig.saturation));
        saturation.setValue(AppConfig.saturation);
        saturation.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.saturation = newValue.intValue();
                saturationValue.setText(String.valueOf(Math.round(newValue.intValue())));
            }
        });

        brightnessValue.setText(String.valueOf(AppConfig.brightness));
        brightness.setValue(AppConfig.brightness);
        brightness.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.brightness = newValue.intValue();
                brightnessValue.setText(String.valueOf(Math.round(newValue.intValue())));
            }
        });

        invert.setSelected(AppConfig.spectralColorInverted);
        invert.setText(AppConfig.spectralColorInverted? "yes" : "no");
        invert.setOnAction(actionEvent -> {
            AppConfig.spectralColorInverted = invert.isSelected();
            invert.setText(AppConfig.spectralColorInverted ? "yes" : "no");
        });

        textColorPicker.setValue(Color.valueOf(AppConfig.textColor));
        textColorPicker.setOnAction(event -> {
            AppConfig.textColor = textColorPicker.getValue().toString();
        });

        opacityValue.setText(String.valueOf(Math.round(AppConfig.opacity)));
        opacity.setValue(AppConfig.opacity);
        opacity.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.opacity = newValue.doubleValue();
                opacityValue.setText(String.valueOf(Math.round(newValue.doubleValue())));
                WindowPropertiesService.opacityProperty.setValue(newValue.doubleValue() / 100d);
            }
        });

    }
}
