package com.lazydash.audio.plugins.hue.ui.fxml.settings.components;

import com.lazydash.audio.plugins.hue.model.HueAreaMap;
import com.lazydash.audio.plugins.hue.model.Location;
import com.lazydash.audio.plugins.hue.system.config.LocationConfig;
import com.lazydash.audio.plugins.hue.system.config.UserConfig;
import com.lazydash.audio.spectrum.system.config.AppConfig;
import com.lazydash.audio.spectrum.system.notification.EventEnum;
import com.lazydash.audio.spectrum.system.notification.NotificationService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class HueIntegrationController {
    public Label hueStatus;
    public TextField hueEntertainmentName;
    public Spinner<Integer> hueTargetFrameRate;
    public VBox frequencyToRangeAreaMap;

    private List<Location> locations = new ArrayList<>();

    public void initialize() {
        hueStatus.setText(UserConfig.getHueStatus());
        NotificationService.getInstance().register(EventEnum.HUE_INTEGRATION_STATUS, message ->
                Platform.runLater(() -> hueStatus.setText(message)));

        hueEntertainmentName.setText(UserConfig.getHueEntertainmentName());

        hueTargetFrameRate.getValueFactory().setValue((int) UserConfig.getHueTargetFPS());
        hueTargetFrameRate.valueProperty().addListener((observable, oldValue, newValue) -> {
            UserConfig.setHueTargetFPS(newValue);
        });

        LocationConfig.getLocationList().forEach(location -> {
            frequencyToRangeAreaMap.getChildren().add(convertFrequencyRangeToAreaToFXML(location));
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

    public void addFrequencyToRangeArea(ActionEvent actionEvent) {
        Location location = new Location();
        LocationConfig.getLocationList().add(location);
        frequencyToRangeAreaMap.getChildren().add(convertFrequencyRangeToAreaToFXML(location));
    }

    public void removeFrequencyToRangeArea(ActionEvent actionEvent) {
        if (!frequencyToRangeAreaMap.getChildren().isEmpty() && !LocationConfig.getLocationList().isEmpty()) {
            LocationConfig.getLocationList().remove(LocationConfig.getLocationList().size() - 1);
            frequencyToRangeAreaMap.getChildren().remove(frequencyToRangeAreaMap.getChildren().size() - 1);
        }
    }

    private HBox convertFrequencyRangeToAreaToFXML(Location location) {
        HBox hBox = new HBox();
        hBox.setSpacing(5);

        Label frequencyStartLabel = new Label("Start: ");
        Spinner<Integer> spinnerStart = new Spinner<Integer>();
        spinnerStart.setEditable(true);
        spinnerStart.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, AppConfig.getSampleRate() / 2, location.getFrequencyStart()));
        spinnerStart.getValueFactory().setValue(location.getFrequencyStart());
        spinnerStart.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            location.setFrequencyStart(newValue);
        });

        Label frequencyEndLabel = new Label("End: ");
        Spinner<Integer> spinnerEnd = new Spinner<Integer>();
        spinnerEnd.setEditable(true);
        spinnerEnd.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, AppConfig.getSampleRate() / 2, location.getFrequencyEnd()));
        spinnerEnd.getValueFactory().setValue(location.getFrequencyEnd());
        spinnerEnd.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            location.setFrequencyEnd(newValue);
        });

        Label effectAreaStartLabel = new Label("Area: ");
        ComboBox<String> effectAreaComboBox = new ComboBox<>();
        effectAreaComboBox.setPrefWidth(160);
        effectAreaComboBox.getItems().addAll(HueAreaMap.getNameToArea().keySet());
        effectAreaComboBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            location.setName(newValue);
        });
        effectAreaComboBox.setValue(location.getName());

        hBox.getChildren().addAll(
                frequencyStartLabel,
                spinnerStart,
                frequencyEndLabel,
                spinnerEnd,
                effectAreaStartLabel,
                effectAreaComboBox
        );

        return hBox;
    }
}
