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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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

    // for mvn javafx:run this will get overwritten to the root of the project, see pom.xml
    public static final String CONFIG_VISUALIZER_APPLICATION_PROPERTIES =
            System.getProperty("user.home") + "/.config/visualizer/application.properties";

    private Stage stage;
    private SpectralView spectralView;
    private ConfigFilePersistence configFilePersistence;
    private TarsosAudioEngine tarsosAudioEngine;

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
    public void start(Stage primaryStage) throws Exception {
        configFilePersistence = new ConfigFilePersistence();
        configFilePersistence.load(AppConfig.class, CONFIG_VISUALIZER_APPLICATION_PROPERTIES);

        tarsosAudioEngine = new TarsosAudioEngine();
        tarsosAudioEngine.start();

        spectralView = new SpectralView();
        spectralView.configure();

        stage = createStage(spectralView);

        FrequencyBarsFFTService spectralFFTService = new FrequencyBarsFFTService();
        tarsosAudioEngine.getFttListenerList().add(spectralFFTService);
        SpectralAnimator spectralAnimator = new SpectralAnimator(spectralFFTService, spectralView);
        spectralAnimator.play();

        stage.show();
    }

    @Override
    public void stop() throws Exception {
        AppConfig.windowHeight = stage.getHeight();
        AppConfig.windowWidth = stage.getWidth();
        AppConfig.windowX = stage.getX();
        AppConfig.windowY = stage.getY();
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
    }

    private void restartUI() throws IOException {
        stage.close();
        stage.getScene().setRoot(new Pane());
        stage = createStage(spectralView);
        stage.show();
    }

    private Stage createStage(Parent root) throws IOException {
        Scene scene = new Scene(root);

        Stage stage = new Stage();
        stage.setTitle("Visualizer");
        if (AppConfig.enableAlwaysOnTop) {
            stage.setAlwaysOnTop(true);
            stage.setOpacity(AppConfig.hoverOpacity);
        } else {
            stage.setAlwaysOnTop(false);
            stage.setOpacity(AppConfig.opacity);
        }
        stage.setScene(scene);

        // set width and height
        stage.setWidth(AppConfig.windowWidth);
        stage.setHeight(AppConfig.windowHeight);

        // set possition
        if (AppConfig.windowX == -1 || AppConfig.windowY == -1) {
            stage.centerOnScreen();
        } else {
            stage.setX(AppConfig.windowX);
            stage.setY(AppConfig.windowY);
        }

        stage.xProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.windowX = newValue.doubleValue();
        });

        stage.yProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.windowY = newValue.doubleValue();
        });

        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.windowHeight = newValue.doubleValue();
        });

        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.windowWidth = newValue.doubleValue();
        });

        // set opacity
        stage.setOpacity(AppConfig.opacity / 100d);
        WindowPropertiesService.opacityProperty = stage.opacityProperty();

        // set style
        if (AppConfig.windowDecorations) {
            stage.initStyle(StageStyle.DECORATED);

        } else {
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setResizable(true);
            scene.setFill(Color.TRANSPARENT);
        }

        // set opacity based on mouse events
        scene.setOnMouseEntered(event -> {
            if (AppConfig.enableAlwaysOnTop && AppConfig.enableHoverOpacity) {
                stage.setOpacity(AppConfig.hoverOpacity / 100d);
            }
        });
        scene.setOnMouseExited(event -> {
            if (AppConfig.enableAlwaysOnTop && AppConfig.enableHoverOpacity) {
                stage.setOpacity(AppConfig.opacity / 100d);
            }
        });

        // click and drag to move window
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)){
                    spectralView.updateBorder();

                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset - (stage.getHeight() - scene.getHeight()));
                }
            }
        });

        scene.setOnMouseReleased(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                spectralView.updateBorder();
            }
        });



        // create context menu
        ContextMenu contextMenu = creaateContextMenu(stage);

        scene.setOnContextMenuRequested(event -> {
            contextMenu.show(stage, event.getScreenX(), event.getScreenY());
        });

        return stage;
    }

    private ContextMenu creaateContextMenu(Stage stage) throws IOException {
        MenuItem switchWindowDecoration = new MenuItem();
        if (AppConfig.windowDecorations) {
            switchWindowDecoration.setText("Enable transparency");
        } else {
            switchWindowDecoration.setText("Disable transparency");
        }
        switchWindowDecoration.setOnAction(event -> {
            try {
                if (AppConfig.windowDecorations) {
                    AppConfig.windowDecorations = false;
                    spectralView.updateBorder();
                    switchWindowDecoration.setText("Enable transparency");
                } else {
                    AppConfig.windowDecorations = true;
                    spectralView.updateBorder();
                    switchWindowDecoration.setText("Disable transparency");
                }

               restartUI();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        MenuItem enableAlwaysOnTop = new MenuItem();
        if (AppConfig.enableAlwaysOnTop) {
            enableAlwaysOnTop.setText("Disable always on top");
        } else {
            enableAlwaysOnTop.setText("Enable always on top");
        }

        enableAlwaysOnTop.setOnAction(event -> {
            if (stage.isAlwaysOnTop()) {
                AppConfig.enableAlwaysOnTop = false;
                stage.setAlwaysOnTop(false);
                stage.setOpacity(AppConfig.opacity / 100d);
                spectralView.updateBorder();
                enableAlwaysOnTop.setText("Enable always on top");
            } else {
                AppConfig.enableAlwaysOnTop = true;
                stage.setAlwaysOnTop(true);
                stage.setOpacity(AppConfig.opacity / 100d);
                spectralView.updateBorder();
                enableAlwaysOnTop.setText("Disable always on top");
            }
        });

        MenuItem minimize = new MenuItem("Minimize");
        minimize.setOnAction(event -> {
            stage.setIconified(true);
        });

        Stage settingsStage = createSettingsStage();
        MenuItem settings = new MenuItem("Settings");
        settings.setOnAction(event -> {
            settingsStage.show();
        });

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(event -> {
            Platform.exit();
        });

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().add(switchWindowDecoration);
        contextMenu.getItems().add(enableAlwaysOnTop);
        contextMenu.getItems().add(minimize);
        contextMenu.getItems().add(settings);
        contextMenu.getItems().add(exit);

        contextMenu.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                contextMenu.hide();
            }
        });

        return contextMenu;
    }

    private Stage createSettingsStage() throws IOException {
        // create
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/fxml/settings/settings.fxml"));
        Parent root = fxmlLoader.load();

        root.getStylesheets().add(getClass().getResource("/ui/fxml/style.css").toExternalForm());
        Scene settingsScene = new Scene(root);
        Stage settingsStage = new Stage();

        // setup
        settingsStage.setTitle("Settings");
        settingsStage.setScene(settingsScene);
        settingsStage.setHeight(600);
        settingsStage.setWidth(900);

        // preload the settings page
        settingsStage.show();
        settingsStage.hide();

        return settingsStage;
    }
}
