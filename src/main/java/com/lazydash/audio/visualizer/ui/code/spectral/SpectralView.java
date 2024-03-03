package com.lazydash.audio.visualizer.ui.code.spectral;

import com.lazydash.audio.visualizer.core.model.FrequencyBar;
import com.lazydash.audio.visualizer.system.config.AppConfig;
import com.lazydash.audio.visualizer.ui.model.WindowPropertiesService;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class SpectralView extends GridPane {
    private final List<FrequencyView> frequencyViewList = new ArrayList<>();
    private Color backgroundColor = Color.TRANSPARENT;
    private Background background = new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY));

    public void configure() {
        this.setAlignment(Pos.BOTTOM_CENTER);
        this.setBackground(background);
        this.setBorder(Border.stroke(Color.GRAY));

        RowConstraints rowConstraintsAmplitudes = new RowConstraints();
        rowConstraintsAmplitudes.setVgrow(Priority.NEVER);
        this.getRowConstraints().add(rowConstraintsAmplitudes);

        WindowPropertiesService.heightProperty.addListener((observable, oldValue, newValue) -> {
            AppConfig.maxBarHeight = newValue.intValue() - AppConfig.hzLabelHeight;
        });
    }

    public void updateState(List<FrequencyBar> frequencyBarList) {
        if (frequencyBarList.size() != frequencyViewList.size()) {
            createBars(frequencyBarList);
            updateBars(frequencyBarList);

        } else {
            updateBars(frequencyBarList);

        }
    }

    private void updateBars(List<FrequencyBar> frequencyBarList) {
        this.setHgap(AppConfig.barGap);

        for (int i = 0; i < frequencyViewList.size(); i++) {
            FrequencyView frequencyView = frequencyViewList.get(i);
            FrequencyBar frequencyBar = frequencyBarList.get(i);
            double rectangleWidth = (this.getWidth() / (frequencyViewList.size() + 1)) - AppConfig.barGap;

            Stop[] stops = new Stop[]{
                    new Stop(0, backgroundColor),
                    new Stop(1, frequencyBar.getColor()),
            };

            LinearGradient linearGradient = new LinearGradient(0.5, 0, 0.5, 1, true, CycleMethod.NO_CYCLE, stops);

            frequencyView.getShadow().setHeight(Math.round(frequencyView.getRectangle().getHeight() - Math.round(frequencyBar.getHeight())));
            frequencyView.getShadow().setFill(linearGradient);
            frequencyView.getShadow().setOpacity(AppConfig.motionBlur / 100d);
            frequencyView.getShadow().setWidth(Math.round(rectangleWidth));

            frequencyView.getRectangle().setHeight(Math.round(frequencyBar.getHeight()));
            frequencyView.getRectangle().setFill(frequencyBar.getColor());
            frequencyView.getRectangle().setWidth(Math.round(rectangleWidth));

            frequencyView.setHzValue(frequencyBar.getHz());
            frequencyView.getHzLabel().setFont(Font.font(rectangleWidth / 2.5d));
            frequencyView.getHzLabel().setFill(Color.BLACK);

        }
    }

    private void createBars(List<FrequencyBar> frequencyBarList) {
        frequencyViewList.clear();
        this.getChildren().clear();

        for (int i = 0; i < frequencyBarList.size(); i++) {
            FrequencyView frequencyView = new FrequencyView();
            frequencyViewList.add(frequencyView);

            VBox amplitudeVBox = new VBox();
            amplitudeVBox.setAlignment(Pos.BOTTOM_CENTER);
            amplitudeVBox.getChildren().add(frequencyView.getShadow());
            amplitudeVBox.getChildren().add(frequencyView.getRectangle());
            this.add(amplitudeVBox, i, 0);

            VBox hzVBox = new VBox();
            hzVBox.setAlignment(Pos.BOTTOM_CENTER);
            hzVBox.getChildren().add(frequencyView.getHzLabel());
            this.add(hzVBox, i, 1);

            GridPane.setValignment(frequencyView.getRectangle(), VPos.BOTTOM);
            GridPane.setHalignment(frequencyView.getHzLabel(), HPos.CENTER);
        }
    }
}
