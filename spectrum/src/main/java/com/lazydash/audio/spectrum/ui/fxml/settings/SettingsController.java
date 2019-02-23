package com.lazydash.audio.spectrum.ui.fxml.settings;

import com.lazydash.audio.spectrum.plugin.PluginSystem;
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
    private Map<String, Parent> titleToFXML = new LinkedHashMap<>();

    public VBox sideBar;
    public Pane centerPane;

    public void initialize() {
        Map<String, Parent> map = new LinkedHashMap<>();
        map.put("Audio input", loadFxml("/ui.fxml.settings/components/audio_input.fxml"));
        map.put("Spectral view", loadFxml("/ui.fxml.settings/components/spectral_view.fxml"));
        map.put("Spectral color", loadFxml("/ui.fxml.settings/components/spectral_color.fxml"));
        map.put("Bar decay", loadFxml("/ui.fxml.settings/components/bar_decay.fxml"));

        map.keySet().forEach((title) -> {
            addTitleToFMXL(title, map.get(title));
        });

        select((Label) sideBar.getChildren().get(0));

        PluginSystem.getInstance().extendUiPlugin(this);

    }

    private Parent loadFxml(String fxmlPath) {
        Parent parent = null;
        try {
            parent = new FXMLLoader(
                    getClass().getResource(fxmlPath))
                    .load();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return parent;
    }

    public void addTitleToFMXL(String title, Parent parent) {
        titleToFXML.put(title, parent);

        Label label = new Label(title);
        label.getStyleClass().add("side-bar-text");
        label.setOnMouseClicked(this::selectEvent);

        sideBar.getChildren().add(label);
    }

    public void selectEvent(MouseEvent mouseEvent) {
        select((Label) mouseEvent.getSource());
    }

    public void select(Label label) {
        String title = label.getText();
        Parent root = titleToFXML.get(title);
        root.getStylesheets().add(getClass().getResource("/ui.fxml.settings/settings.css").toExternalForm());

        centerPane.getChildren().clear();
        centerPane.getChildren().add(root);

        sideBar.getChildren().forEach(node -> {
            node.getStyleClass().clear();
            node.getStyleClass().add("side-bar-text");
        });
        label.getStyleClass().add("selected");

    }
}
