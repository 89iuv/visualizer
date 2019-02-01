package com.lazydash.audio.visualizer;

import com.lazydash.audio.visualizer.core.audio.TarsosAudioEngine;
import com.lazydash.audio.visualizer.core.service.GenericFFTService;
import com.lazydash.audio.visualizer.external.hue.HueIntegration;
import com.lazydash.audio.visualizer.external.hue.manager.HueIntegrationManager;
import com.lazydash.audio.visualizer.system.config.AppConfig;
import com.lazydash.audio.visualizer.system.config.WindowConfig;
import com.lazydash.audio.visualizer.system.persistance.ConfigurationService;
import com.lazydash.audio.visualizer.system.setup.SystemSetup;
import com.lazydash.audio.visualizer.ui.code.color.GlobalColorAnimator;
import com.lazydash.audio.visualizer.ui.code.color.GlobalColorView;
import com.lazydash.audio.visualizer.ui.code.spectral.SpectralAnimator;
import com.lazydash.audio.visualizer.ui.code.spectral.SpectralView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    static {
        try {
            SystemSetup systemSetup = new SystemSetup();
            systemSetup.runHueConfiguration();

        } catch (RuntimeException e) {
            displayExceptionAndClose(e);
        }

    }

    public static void main(String[] args) {
        try {
            launch(args);

        } catch (RuntimeException e) {
            displayExceptionAndClose(e);
        }

    }

    @Override
    public void start(Stage stage) throws Exception {
        // create
        ConfigurationService configurationService = ConfigurationService.createAndReadConfiguration();
        TarsosAudioEngine tarsosAudioEngine = new TarsosAudioEngine();
        HueIntegration hueIntegration = new HueIntegration();

        SpectralView spectralView = new SpectralView();
        GlobalColorView globalColorView = new GlobalColorView();
        Scene scene = createScene(spectralView, globalColorView);
        Stage settingsStage = createSettingsStage();

        GenericFFTService spectralFFTService = new GenericFFTService();
        GenericFFTService hueFFTService = new GenericFFTService();
        GenericFFTService globalColorFFTService = new GenericFFTService();

        HueIntegrationManager hueIntegrationManager = new HueIntegrationManager(hueIntegration, hueFFTService);

        GlobalColorAnimator globalColorAnimator = new GlobalColorAnimator(globalColorFFTService, globalColorView);
        SpectralAnimator spectralAnimator = new SpectralAnimator(spectralFFTService, spectralView);

        // setup
        globalColorView.configure();
        spectralView.configure();
        configureStage(stage, scene);

        // wire
        tarsosAudioEngine.getFttListenerList().add(spectralFFTService);
        tarsosAudioEngine.getFttListenerList().add(hueFFTService);
        tarsosAudioEngine.getFttListenerList().add(globalColorFFTService);

        wireSettingsStage(settingsStage, scene);
        wirePrimaryStage(stage, configurationService);

        // run
        tarsosAudioEngine.start();
        hueIntegrationManager.start();

        stage.show();

        spectralAnimator.play();
        globalColorAnimator.play();

    }

    private void wireSettingsStage(Stage settingsStage, Scene rootScene) {
        rootScene.setOnMouseClicked(event -> {
            if (!settingsStage.isShowing()) {
                settingsStage.show();
            }
        });
    }

    private void wirePrimaryStage(Stage primaryStage, ConfigurationService configurationService) {
        primaryStage.setOnCloseRequest(event -> {
            AppConfig.setWindowHeight(primaryStage.getHeight());
            AppConfig.setWindowWidth(primaryStage.getWidth());
            configurationService.persistConfig();
            Platform.exit();
        });
    }

    private Scene createScene(SpectralView spectralView, GlobalColorView globalColorView){
        //create
        VBox rootVbox = new VBox();
        Scene scene = new Scene(rootVbox);

        // wire
        rootVbox.getChildren().add(spectralView);
        rootVbox.getChildren().add(globalColorView);
        rootVbox.setAlignment(Pos.BOTTOM_CENTER);
        VBox.setVgrow(spectralView, Priority.ALWAYS);

        WindowConfig.widthProperty = scene.widthProperty();
        WindowConfig.heightProperty = scene.heightProperty();

        return scene;
    }

    private void configureStage(Stage stage, Scene scene){
        // setup
        stage.setTitle("Visualiser");

        stage.setWidth(AppConfig.getWindowWidth());
        stage.setHeight(AppConfig.getWindowHeight());

        stage.setScene(scene);
    }

    private Stage createSettingsStage() throws IOException {
        // create
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui.fxml.settings/settings.fxml"));
        Parent root = fxmlLoader.load();
        root.getStylesheets().add(getClass().getResource("/ui.fxml.settings/settings.css").toExternalForm());
        Scene settingsScene = new Scene(root);
        Stage settingsStage = new Stage();

        // setup
        settingsStage.setTitle("Settings");
        settingsStage.setScene(settingsScene);
        settingsStage.setHeight(400);
        settingsStage.setWidth(700);

        return settingsStage;
    }

    private static void displayExceptionAndClose(RuntimeException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
        alert.showAndWait();

        e.printStackTrace();
        System.exit(1);
    }
}
