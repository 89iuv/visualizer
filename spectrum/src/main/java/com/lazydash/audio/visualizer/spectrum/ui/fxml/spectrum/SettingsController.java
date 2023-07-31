package com.lazydash.audio.visualizer.spectrum.ui.fxml.spectrum;

import com.lazydash.audio.visualizer.spectrum.plugin.PluginSystem;
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
    public VBox sideBarPlugins;
    public Label pluginsLabel;
    private Map<String, Parent> titleToFXML = new LinkedHashMap<>();

    public VBox centerPane;

    public void initialize() {
        Map<String, Parent> map = new LinkedHashMap<>();
        map.put("Audio input", loadFxml("/ui/fxml/spectrum/settings/audio_input.fxml"));
        map.put("Spectral view", loadFxml("/ui/fxml/spectrum/settings/spectral_view.fxml"));
        map.put("Spectral color", loadFxml("/ui/fxml/spectrum/settings/spectral_color.fxml"));
        map.put("Bar decay", loadFxml("/ui/fxml/spectrum/settings/bar_decay.fxml"));
        map.put("Debug", loadFxml("/ui/fxml/spectrum/settings/debug.fxml"));

        map.keySet().forEach((title) -> {
            addTitleToSettingsFMXL(title, map.get(title));
        });

        select((Label) sideBarSettings.getChildren().get(0));

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

    private void addTitleToSettingsFMXL(String title, Parent parent) {
        addParentToSideBar(title, parent, sideBarSettings);
    }

    public void addTitleToPluginsFMXL(String title, Parent parent) {
        addParentToSideBar(title, parent, sideBarPlugins);
        pluginsLabel.setVisible(true);
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
        root.getStylesheets().add(getClass().getResource("/ui/fxml/spectrum/style.css").toExternalForm());

        centerPane.getChildren().clear();
        centerPane.getChildren().add(root);

        sideBarSettings.getChildren().forEach(node -> {
            node.getStyleClass().clear();
            node.getStyleClass().add("side-bar-text");
        });

        sideBarPlugins.getChildren().forEach(node -> {
            node.getStyleClass().clear();
            node.getStyleClass().add("side-bar-text");
        });

        label.getStyleClass().add("selected");

    }
}
