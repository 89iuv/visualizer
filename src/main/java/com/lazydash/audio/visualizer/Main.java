package com.lazydash.audio.visualizer;

import com.lazydash.audio.visualizer.core.audio.TarsosAudioEngine;
import com.lazydash.audio.visualizer.core.service.FrequencyBarsFFTService;
import com.lazydash.audio.visualizer.system.config.AppConfig;
import com.lazydash.audio.visualizer.system.persistance.ConfigFilePersistence;
import com.lazydash.audio.visualizer.ui.code.spectral.SpectralAnimator;
import com.lazydash.audio.visualizer.ui.code.spectral.SpectralView;
import com.lazydash.audio.visualizer.ui.model.WindowPropertiesService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/*
Using Intellij:
    - vm options: --module-path "/pathToJavaFxHome/lib" --add-modules=javafx.controls,javafx.fxml
Using Maven:
    - mvn clean javafx:run
*/
public class Main extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static final String CONFIG_VISUALIZER_APPLICATION_PROPERTIES = System.getProperty("user.home") + "/.config/visualizer/application.properties";

    private double xOffset = 0;
    private double yOffset = 0;

    public static void main(String[] args) {
        try {
            launch(args);

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        // create
        ConfigFilePersistence configFilePersistence = new ConfigFilePersistence();
        configFilePersistence.load(AppConfig.class, CONFIG_VISUALIZER_APPLICATION_PROPERTIES);
        TarsosAudioEngine tarsosAudioEngine = new TarsosAudioEngine();

        SpectralView spectralView = new SpectralView();
        Scene scene = createScene(spectralView);
        Stage settingsStage = createSettingsStage();

        FrequencyBarsFFTService spectralFFTService = new FrequencyBarsFFTService();
        SpectralAnimator spectralAnimator = new SpectralAnimator(spectralFFTService, spectralView);

        // setup
        spectralView.configure();
        configureStage(stage, scene);

        // wire
        tarsosAudioEngine.getFttListenerList().add(spectralFFTService);
        //PluginSystem.getInstance().registerAllFffPlugins(tarsosAudioEngine);

        wireSettingsStage(settingsStage, scene);
        wirePrimaryStage(stage, configFilePersistence, tarsosAudioEngine);

        // set taskbar icon
//        Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
//        Image image = defaultToolkit.getImage(getClass().getResource("/icon.png"));
//        Taskbar.getTaskbar().setIconImage(image);


        // set click and move for scene
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset - (stage.getHeight() - scene.getHeight()));
            }
        });

        stage.show();
        spectralAnimator.play();
        tarsosAudioEngine.start();

    }

    private void wireSettingsStage(Stage settingsStage, Scene rootScene) {
        rootScene.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY && !settingsStage.isShowing()) {
                settingsStage.show();
            }
        });
    }

    private void wirePrimaryStage(Stage primaryStage, ConfigFilePersistence configFilePersistence, TarsosAudioEngine tarsosAudioEngine) {
        primaryStage.setOnCloseRequest(event -> {
            AppConfig.windowHeight = primaryStage.getHeight();
            AppConfig.windowWidth = primaryStage.getWidth();
            configFilePersistence.persist(AppConfig.class, CONFIG_VISUALIZER_APPLICATION_PROPERTIES);
            tarsosAudioEngine.stop();
            Platform.exit();

            // force exit in 1 seconds if application does not finish
            Thread exit = new Thread(() -> {
                try {
                    Thread.sleep(1 * 1000);
                } catch (InterruptedException e) {
                    // we expect that the sleep will be interrupted
                    // and the application should finish before the sleep is over
                }

                LOGGER.error("Forced exit");
                System.exit(1);
            });

            exit.setDaemon(true);
            exit.start();
        });
    }

    private Scene createScene(SpectralView spectralView) {
        //create
        Scene scene = new Scene(spectralView);

        WindowPropertiesService.widthProperty = scene.widthProperty();
        WindowPropertiesService.heightProperty = scene.heightProperty();

        return scene;
    }

    private void configureStage(Stage stage, Scene scene) {
        // setup
        stage.setTitle("Visualiser");

        stage.setWidth(AppConfig.windowWidth);
        stage.setHeight(AppConfig.windowHeight);

        stage.setScene(scene);
    }

    private Stage createSettingsStage() throws IOException {
        // create
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/fxml/spectrum/spectrum.fxml"));
        Parent root = fxmlLoader.load();

        root.getStylesheets().add(getClass().getResource("/ui/fxml/spectrum/style.css").toExternalForm());
        Scene settingsScene = new Scene(root);
        Stage settingsStage = new Stage();

        // setup
        settingsStage.setTitle("Settings");
        settingsStage.setScene(settingsScene);
        settingsStage.setHeight(560);
        settingsStage.setWidth(840);

        // preload the settings page
        settingsStage.show();
        settingsStage.hide();

        return settingsStage;
    }
}
