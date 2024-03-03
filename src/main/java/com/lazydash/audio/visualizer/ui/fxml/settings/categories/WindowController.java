package com.lazydash.audio.visualizer.ui.fxml.settings.categories;

import com.lazydash.audio.visualizer.system.config.AppConfig;
import com.lazydash.audio.visualizer.ui.model.WindowPropertiesService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class WindowController {
    public CheckBox windowDecorations;
    public Slider opacity;
    public Label opacityValue;
    public CheckBox enableHoverOpacity;
    public Slider hoverOpacity;
    public Label hoverOpacityValue;
    public CheckBox enableAlwaysOnTop;

    public void initialize() {
        windowDecorations.setSelected(AppConfig.windowDecorations);
        windowDecorations.setText(AppConfig.windowDecorations ? "yes" : "no");
        windowDecorations.setOnAction(actionEvent -> {
            AppConfig.windowDecorations = windowDecorations.isSelected();
            windowDecorations.setText(AppConfig.windowDecorations ? "yes" : "no");
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

        enableAlwaysOnTop.setSelected(AppConfig.enableAlwaysOnTop);
        enableAlwaysOnTop.setText(AppConfig.enableAlwaysOnTop? "yes" : "no");
        enableAlwaysOnTop.setOnAction(actionEvent -> {
            AppConfig.enableAlwaysOnTop = enableAlwaysOnTop.isSelected();
            enableAlwaysOnTop.setText(AppConfig.enableAlwaysOnTop ? "yes" : "no");
        });

        enableHoverOpacity.setSelected(AppConfig.enableHoverOpacity);
        enableHoverOpacity.setText(AppConfig.enableHoverOpacity ? "yes" : "no");
        enableHoverOpacity.setOnAction(actionEvent -> {
            AppConfig.enableHoverOpacity = enableHoverOpacity.isSelected();
            enableHoverOpacity.setText(AppConfig.enableHoverOpacity ? "yes" : "no");
            hoverOpacity.setDisable(!enableHoverOpacity.isSelected());
        });

        hoverOpacityValue.setText(String.valueOf(Math.round(AppConfig.hoverOpacity)));
        hoverOpacity.setValue(AppConfig.hoverOpacity);
        hoverOpacity.setDisable(!AppConfig.enableHoverOpacity);
        hoverOpacity.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AppConfig.hoverOpacity = newValue.doubleValue();
                hoverOpacityValue.setText(String.valueOf(Math.round(newValue.doubleValue())));
            }
        });
    }

}
