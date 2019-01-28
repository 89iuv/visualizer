package com.lazydash.audio.visualiser;

import com.lazydash.audio.visualiser.core.audio.TarsosAudioEngine;
import com.lazydash.audio.visualiser.core.manager.HueIntegrationManager;
import com.lazydash.audio.visualiser.core.service.HueFFTService;
import com.lazydash.audio.visualiser.core.service.SpectralFFTService;
import com.lazydash.audio.visualiser.external.hue.HueIntegration;
import com.lazydash.audio.visualiser.system.config.AppConfig;
import com.lazydash.audio.visualiser.system.config.WindowConfig;
import com.lazydash.audio.visualiser.system.configure.SystemConfigure;
import com.lazydash.audio.visualiser.system.persistance.ConfigurationService;
import com.lazydash.audio.visualiser.ui.code.color.GlobalColorAnimator;
import com.lazydash.audio.visualiser.ui.code.color.GlobalColorView;
import com.lazydash.audio.visualiser.ui.code.spectral.SpectralAnimator;
import com.lazydash.audio.visualiser.ui.code.spectral.SpectralView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    static {
        SystemConfigure systemConfigure = new SystemConfigure();
        systemConfigure.runHueConfiguration();
    }

    public static void main(String[] args) {
        launch(args);
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

        SpectralFFTService spectralFFTService = new SpectralFFTService();
        HueFFTService hueFFTService = new HueFFTService();

        HueIntegrationManager hueIntegrationManager = new HueIntegrationManager(hueIntegration, hueFFTService);

        GlobalColorAnimator globalColorAnimator = new GlobalColorAnimator(globalColorView, spectralView);
        SpectralAnimator spectralAnimator = new SpectralAnimator(spectralFFTService, spectralView);

        // configure
        globalColorView.configure();
        spectralView.configure();
        configureStage(stage, scene);

        // wire
        tarsosAudioEngine.getFttListenerList().add(spectralFFTService);
        tarsosAudioEngine.getFttListenerList().add(hueFFTService);
        tarsosAudioEngine.getFttListenerList().add(hueIntegrationManager);

        wireSettingsStage(settingsStage, scene);
        wirePrimaryStage(stage, configurationService);

        // run
        tarsosAudioEngine.start();

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
        // configure
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

        // configure
        settingsStage.setTitle("Settings");
        settingsStage.setScene(settingsScene);
        settingsStage.setHeight(400);
        settingsStage.setWidth(700);

        return settingsStage;
    }
}
