package com.lazydash.audio.visualizer;

import com.lazydash.audio.visualizer.core.audio.TarsosAudioEngine;
import com.lazydash.audio.visualizer.core.service.FrequencyBarsFFTService;
import com.lazydash.audio.visualizer.external.hue.HueIntegration;
import com.lazydash.audio.visualizer.external.hue.manager.HueIntegrationManager;
import com.lazydash.audio.visualizer.system.config.AppConfig;
import com.lazydash.audio.visualizer.system.config.WindowConfig;
import com.lazydash.audio.visualizer.system.persistance.AppConfigPersistence;
import com.lazydash.audio.visualizer.system.persistance.ColorConfigPersistence;
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
        AppConfigPersistence appConfigPersistence = AppConfigPersistence.createAndReadConfiguration();
        ColorConfigPersistence colorConfigPersistence = ColorConfigPersistence.createAndReadColorConfiguration();
        TarsosAudioEngine tarsosAudioEngine = new TarsosAudioEngine();
        HueIntegration hueIntegration = new HueIntegration();

        SpectralView spectralView = new SpectralView();
        GlobalColorView globalColorView = new GlobalColorView();
        Scene scene = createScene(spectralView, globalColorView);
        Stage settingsStage = createSettingsStage();

        FrequencyBarsFFTService spectralFFTService = new FrequencyBarsFFTService();
        FrequencyBarsFFTService hueFFTService = new FrequencyBarsFFTService();
        FrequencyBarsFFTService globalColorFFTService = new FrequencyBarsFFTService();

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
        wirePrimaryStage(stage, appConfigPersistence, colorConfigPersistence, tarsosAudioEngine);

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

    private void wirePrimaryStage(Stage primaryStage, AppConfigPersistence appConfigPersistence, ColorConfigPersistence colorConfigPersistence, TarsosAudioEngine tarsosAudioEngine) {
        primaryStage.setOnCloseRequest(event -> {
            AppConfig.setWindowHeight(primaryStage.getHeight());
            AppConfig.setWindowWidth(primaryStage.getWidth());
            appConfigPersistence.persistConfig();
            colorConfigPersistence.persistConfig();
            tarsosAudioEngine.stop();
            Platform.exit();

            // force exit in 5 seconds if application does not finish
            Thread exit = new Thread(() -> {
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.err.println("Forced exit");
                System.exit(1);
            });
            exit.setDaemon(true);
            exit.start();
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
