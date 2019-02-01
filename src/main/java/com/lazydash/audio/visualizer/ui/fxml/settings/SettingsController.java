package com.lazydash.audio.visualizer.ui.fxml.settings;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SettingsController {
    private Map<String, String> titleToFXML = new LinkedHashMap<>();

    public VBox sideBar;
    public Pane centerPane;

    public void initialize(){
        titleToFXML.put("Audio input", "/ui.fxml.settings/components/audio_input.fxml");
        titleToFXML.put("Spectral view", "/ui.fxml.settings/components/spectral_view.fxml");
        titleToFXML.put("Bar decay", "/ui.fxml.settings/components/bar_decay.fxml");
        titleToFXML.put("Hue Integration", "/ui.fxml.settings/components/hue_integration.fxml");

        titleToFXML.keySet().forEach(title -> {
            Label label = new Label(title);
            label.getStyleClass().add("side-bar-text");
            label.setOnMouseClicked(this::selectEvent);

            sideBar.getChildren().add(label);
        });

        select((Label) sideBar.getChildren().get(0));

    }

    public void selectEvent(MouseEvent mouseEvent) {
        select((Label) mouseEvent.getSource());
    }

    public void select(Label label) {
        try {
            String title = label.getText();
            String location = titleToFXML.get(title);

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(location));
            Parent root = fxmlLoader.load();
            root.getStylesheets().add(getClass().getResource("/ui.fxml.settings/settings.css").toExternalForm());

            centerPane.getChildren().clear();
            centerPane.getChildren().add(root);

            sideBar.getChildren().forEach(node -> {
                node.getStyleClass().clear();
                node.getStyleClass().add("side-bar-text");
            });
            label.getStyleClass().add("selected");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
