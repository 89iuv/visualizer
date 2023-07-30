package com.lazydash.audio.visualizer.spectrum.ui.code.spectral;

import com.lazydash.audio.visualizer.spectrum.core.model.FrequencyBar;
import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;
import com.lazydash.audio.visualizer.spectrum.system.config.WindowProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Effect;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SpectralView extends GridPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpectralView.class);
    private List<FrequencyView> frequencyViewList = new ArrayList<>();

    public void configure() {
        this.setAlignment(Pos.BOTTOM_CENTER);
//        this.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        RowConstraints rowConstraintsAmplitudes = new RowConstraints();
        rowConstraintsAmplitudes.setVgrow(Priority.NEVER);
        this.getRowConstraints().add(rowConstraintsAmplitudes);

        WindowProperty.heightProperty.addListener((observable, oldValue, newValue) -> {
            AppConfig.maxBarHeight = newValue.intValue() - AppConfig.hzLabelHeight;
        });
    }

    public void updateState(List<FrequencyBar> frequencyBarList) {
        //this.setHgap(AppConfig.barGap);

        if (frequencyBarList.size() != frequencyViewList.size()) {
            createBars(frequencyBarList);
            updateBars(frequencyBarList);
//            LOGGER.info("create bars");

        } else {
            updateBars(frequencyBarList);
//            LOGGER.info("update bars");
        }
    }

    private void updateBars(List<FrequencyBar> frequencyBarList) {
        this.setHgap(AppConfig.barGap);

        for (int i = 0; i < frequencyViewList.size(); i++) {
            FrequencyView frequencyView = frequencyViewList.get(i);
            FrequencyBar frequencyBar = frequencyBarList.get(i);

            frequencyView.setHzValue(frequencyBar.getHz());

            frequencyView.getRectangle().setFill(frequencyBar.getColor());
//            frequencyView.getRectangle().setStroke(Color.GRAY);
//            frequencyView.getRectangle().setStrokeType(StrokeType.INSIDE);

            // rounding is needed because of the subpixel rendering
            frequencyView.getRectangle().setHeight(Math.round(frequencyBar.getHeight()));

            double rectangleWidth = (this.getWidth() / (frequencyViewList.size() + 1)) - AppConfig.barGap;
            frequencyView.getRectangle().setWidth(rectangleWidth);
            frequencyView.getHzLabel().setFont(Font.font(rectangleWidth / 2.6d));
        }
    }

    private void createBars(List<FrequencyBar> frequencyBarList) {
        frequencyViewList.clear();
        this.getChildren().clear();

        for (int i = 0; i < frequencyBarList.size(); i++) {
            FrequencyView frequencyView = new FrequencyView();
            frequencyViewList.add(frequencyView);

            //frequencyView.getHzLabel().setTextFill(Color.WHITE);

            this.add(frequencyView.getRectangle(), i, 0);
            this.add(frequencyView.getHzLabel(), i, 1);

            GridPane.setValignment(frequencyView.getRectangle(), VPos.BOTTOM);
            GridPane.setHalignment(frequencyView.getHzLabel(), HPos.CENTER);
        }
    }
}
