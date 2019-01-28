package com.lazydash.audio.visualiser.ui.fxml.settings.components;

import com.lazydash.audio.visualiser.system.config.AppConfig;
import com.lazydash.audio.visualiser.system.notification.EventCallback;
import com.lazydash.audio.visualiser.system.notification.NotificationService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class HueIntegrationController {
    public Label hueStatus;
    public TextField hueEntertainmentName;

    public void initialize() {
        hueStatus.setText(AppConfig.getHueStatus());
        NotificationService.getInstance().register("HueIntegrationController-1", "hue-integration-status", new EventCallback() {
            @Override
            public void run(String message) {
                Platform.runLater(() -> hueStatus.setText(message));

            }
        });

        hueEntertainmentName.setText(AppConfig.getHueEntertainmentName());
    }

    public void stopHueIntegration(ActionEvent actionEvent) {
        AppConfig.setHueIntegration(false);
    }

    public void startHueIntegration(ActionEvent actionEvent) {
        AppConfig.setHueIntegration(true);
    }

    public void updateHueEntertainmentName(KeyEvent keyEvent) {
        AppConfig.setHueEntertainmentName(hueEntertainmentName.getText());
    }
}
