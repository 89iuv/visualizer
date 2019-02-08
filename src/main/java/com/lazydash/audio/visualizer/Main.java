package com.lazydash.audio.visualizer;

import com.lazydash.audio.visualizer.core.audio.TarsosAudioEngine;
import com.lazydash.audio.visualizer.core.service.FrequencyBarsFFTService;
import com.lazydash.audio.visualizer.external.hue.HueIntegration;
import com.lazydash.audio.visualizer.external.hue.manager.HueIntegrationManager;
import com.lazydash.audio.visualizer.system.config.AppConfig;
import com.lazydash.audio.visualizer.system.config.WindowConfig;
import com.lazydash.audio.visualizer.system.persistance.AppConfigPersistence;
import com.lazydash.audio.visualizer.system.setup.SystemSetup;
import com.lazydash.audio.visualizer.ui.code.spectral.SpectralAnimator;
import com.lazydash.audio.visualizer.ui.code.spectral.SpectralView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
        TarsosAudioEngine tarsosAudioEngine = new TarsosAudioEngine();
        HueIntegration hueIntegration = new HueIntegration();

        SpectralView spectralView = new SpectralView();
        Scene scene = createScene(spectralView);
        Stage settingsStage = createSettingsStage();

        FrequencyBarsFFTService spectralFFTService = new FrequencyBarsFFTService();
        FrequencyBarsFFTService hueFFTService = new FrequencyBarsFFTService();
        FrequencyBarsFFTService globalColorFFTService = new FrequencyBarsFFTService();

        HueIntegrationManager hueIntegrationManager = new HueIntegrationManager(hueIntegration, hueFFTService);

        SpectralAnimator spectralAnimator = new SpectralAnimator(spectralFFTService, spectralView);

        // setup
        spectralView.configure();
        configureStage(stage, scene);

        // wire
        tarsosAudioEngine.getFttListenerList().add(spectralFFTService);
        tarsosAudioEngine.getFttListenerList().add(hueFFTService);
        tarsosAudioEngine.getFttListenerList().add(globalColorFFTService);

        wireSettingsStage(settingsStage, scene);
        wirePrimaryStage(stage, appConfigPersistence, tarsosAudioEngine);

        // run
        tarsosAudioEngine.start();
        hueIntegrationManager.start();

        stage.show();

        spectralAnimator.play();

    }

    private void wireSettingsStage(Stage settingsStage, Scene rootScene) {
        rootScene.setOnMouseClicked(event -> {
            if (!settingsStage.isShowing()) {
                settingsStage.show();
            }
        });
    }

    private void wirePrimaryStage(Stage primaryStage, AppConfigPersistence appConfigPersistence, TarsosAudioEngine tarsosAudioEngine) {
        primaryStage.setOnCloseRequest(event -> {
            AppConfig.setWindowHeight(primaryStage.getHeight());
            AppConfig.setWindowWidth(primaryStage.getWidth());
            appConfigPersistence.persistConfig();
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

    private Scene createScene(SpectralView spectralView){
        //create
        Scene scene = new Scene(spectralView);

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
