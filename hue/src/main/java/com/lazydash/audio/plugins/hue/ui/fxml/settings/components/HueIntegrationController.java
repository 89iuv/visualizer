package com.lazydash.audio.plugins.hue.ui.fxml.settings.components;

import com.lazydash.audio.plugins.hue.system.config.AppConfig;
import com.lazydash.audio.spectrum.system.notification.EventEnum;
import com.lazydash.audio.spectrum.system.notification.NotificationService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class HueIntegrationController {
    public Label hueStatus;
    public TextField hueEntertainmentName;
    public Spinner<Integer> hueTargetFrameRate;

    public void initialize() {
        hueStatus.setText(AppConfig.getHueStatus());
        NotificationService.getInstance().register(EventEnum.HUE_INTEGRATION_STATUS, message ->
                Platform.runLater(() -> hueStatus.setText(message)));

        hueEntertainmentName.setText(AppConfig.getHueEntertainmentName());

        hueTargetFrameRate.getValueFactory().setValue((int) AppConfig.getHueTargetFPS());
        hueTargetFrameRate.valueProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.setHueTargetFPS(newValue);
        });
    }

    public void stopHueIntegration(ActionEvent actionEvent) {
        AppConfig.setHueIntegrationEnabled(false);
    }

    public void startHueIntegration(ActionEvent actionEvent) {
        AppConfig.setHueIntegrationEnabled(true);
    }

    public void updateHueEntertainmentName(KeyEvent keyEvent) {
        AppConfig.setHueEntertainmentName(hueEntertainmentName.getText());
    }
}
