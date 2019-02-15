package com.lazydash.audio.plugins.hue.ui.fxml.settings.components;

import com.lazydash.audio.plugins.hue.system.config.UserConfig;
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
        hueStatus.setText(UserConfig.getHueStatus());
        NotificationService.getInstance().register(EventEnum.HUE_INTEGRATION_STATUS, message ->
                Platform.runLater(() -> hueStatus.setText(message)));

        hueEntertainmentName.setText(UserConfig.getHueEntertainmentName());

        hueTargetFrameRate.getValueFactory().setValue((int) UserConfig.getHueTargetFPS());
        hueTargetFrameRate.valueProperty().addListener((observable, oldValue, newValue) -> {
            UserConfig.setHueTargetFPS(newValue);
        });
    }

    public void stopHueIntegration(ActionEvent actionEvent) {
        UserConfig.setHueIntegrationEnabled(false);
    }

    public void startHueIntegration(ActionEvent actionEvent) {
        UserConfig.setHueIntegrationEnabled(true);
    }

    public void updateHueEntertainmentName(KeyEvent keyEvent) {
        UserConfig.setHueEntertainmentName(hueEntertainmentName.getText());
    }
}
