package com.lazydash.audio.visualizer.spectrum.ui.code.spectral;

import com.lazydash.audio.visualizer.spectrum.core.model.FrequencyBar;
import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;
import com.lazydash.audio.visualizer.spectrum.system.config.WindowProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class SpectralView extends GridPane {
    private List<FrequencyView> frequencyViewList = new ArrayList<>();

    public void configure() {
        this.setAlignment(Pos.BOTTOM_CENTER);
        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        RowConstraints rowConstraintsAmplitudes = new RowConstraints();
        rowConstraintsAmplitudes.setVgrow(Priority.NEVER);
        this.getRowConstraints().add(rowConstraintsAmplitudes);

        WindowProperty.heightProperty.addListener((observable, oldValue, newValue) -> {
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
                    new Stop(0, Color.WHITE),
                    new Stop(1, Color.WHITE.interpolate(frequencyBar.getColor(), AppConfig.motionBlur / 100d))
//                    new Stop(1, frequencyBar.getColor())
            };

            LinearGradient linearGradient = new LinearGradient(0.5, 0, 0.5, 1, true, CycleMethod.NO_CYCLE, stops);
//            LinearGradient linearGradient = new LinearGradient(0.5, 1 - (AppConfig.motionBlur / 100d), 0.5, 1, true, CycleMethod.NO_CYCLE, stops);

            frequencyView.getShadow().setHeight(frequencyView.getRectangle().getHeight() - Math.round(frequencyBar.getHeight()));
            frequencyView.getShadow().setFill(linearGradient);

            frequencyView.getRectangle().setHeight(Math.round(frequencyBar.getHeight()));
            frequencyView.getRectangle().setFill(frequencyBar.getColor());

            frequencyView.setHzValue(frequencyBar.getHz());

            frequencyView.getShadow().setWidth(Math.round(rectangleWidth));
            frequencyView.getRectangle().setWidth(Math.round(rectangleWidth));
            frequencyView.getHzLabel().setFont(Font.font(rectangleWidth / 2.6d));
        }
    }

    private void createBars(List<FrequencyBar> frequencyBarList) {
        frequencyViewList.clear();
        this.getChildren().clear();

        for (int i = 0; i < frequencyBarList.size(); i++) {
            FrequencyView frequencyView = new FrequencyView();
            frequencyViewList.add(frequencyView);

            VBox vBox = new VBox();
            vBox.setAlignment(Pos.BOTTOM_CENTER);
            vBox.getChildren().add(frequencyView.getShadow());
            vBox.getChildren().add(frequencyView.getRectangle());

            this.add(vBox, i, 0);
            this.add(frequencyView.getHzLabel(), i, 1);

            GridPane.setValignment(frequencyView.getRectangle(), VPos.BOTTOM);
            GridPane.setHalignment(frequencyView.getHzLabel(), HPos.CENTER);
        }
    }
}
