package com.lazydash.audio.visualizer.ui.fxml.settings;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SettingsController {
    public VBox sideBarSettings;
    private final Map<String, Parent> titleToFXML = new LinkedHashMap<>();

    public VBox centerPane;

    public void initialize() {
        Map<String, Parent> map = new LinkedHashMap<>();
        map.put("Audio Input", loadFxml("/ui/fxml/settings/categories/audio_input.fxml"));
        map.put("Spectral Limits", loadFxml("/ui/fxml/settings/categories/spectral_limits.fxml"));
        map.put("Appearance & Colors", loadFxml("/ui/fxml/settings/categories/appearance_and_colors.fxml"));
        map.put("Frequency Decay", loadFxml("/ui/fxml/settings/categories/frequency_decay.fxml"));
        map.put("Debug Information", loadFxml("/ui/fxml/settings/categories/debug_information.fxml"));

        map.keySet().forEach((title) -> {
            addTitleToSettingsFMXL(title, map.get(title));
        });

        select((Label) sideBarSettings.getChildren().get(0));

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

    private void addTitleToSettingsFMXL(String title, Parent parent) {
        addParentToSideBar(title, parent, sideBarSettings);
    }

    private void addParentToSideBar(String title, Parent parent, VBox sideBar) {
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
        root.getStylesheets().add(getClass().getResource("/ui/fxml/style.css").toExternalForm());

        centerPane.getChildren().clear();
        centerPane.getChildren().add(root);

        sideBarSettings.getChildren().forEach(node -> {
            node.getStyleClass().clear();
            node.getStyleClass().add("side-bar-text");
        });

        label.getStyleClass().add("selected");
    }
}
