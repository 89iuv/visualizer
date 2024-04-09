package com.lazydash.audio.visualizer.ui.code.spectral;

import com.lazydash.audio.visualizer.core.model.FrequencyBar;
import com.lazydash.audio.visualizer.system.config.AppConfig;
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
    private final List<FrequencyView> frequencyViewList = new ArrayList<>();
    private final Color backgroundColor = Color.TRANSPARENT;
    private final Background background = new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY));

    public void configure() {
        this.setAlignment(Pos.BOTTOM_CENTER);
        this.setBackground(background);

        this.hoverProperty().addListener((observable, oldValue, newValue) -> {
            Color borderColor = Color.TRANSPARENT;
            if (!AppConfig.windowDecorations && AppConfig.enableAlwaysOnTop) {
                if (newValue) {
                    borderColor = Color.GRAY;
                }
            }

            this.setBorder(new Border(new BorderStroke(borderColor, borderColor, borderColor, borderColor,
                    BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY, new BorderWidths(1), Insets.EMPTY)));

        });

        if (!AppConfig.windowDecorations) {
            Color borderColor = Color.GRAY;
            this.setBorder(new Border(new BorderStroke(borderColor, borderColor, borderColor, borderColor,
                    BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY, new BorderWidths(1), Insets.EMPTY)));
        }

        RowConstraints rowConstraintsAmplitudes = new RowConstraints();
        rowConstraintsAmplitudes.setVgrow(Priority.NEVER);
        this.getRowConstraints().add(rowConstraintsAmplitudes);

        this.heightProperty().addListener((observable, oldValue, newValue) -> {
            AppConfig.maxBarHeight = newValue.intValue() - AppConfig.hzLabelHeight;
        });

    }

    public void updateBorder() {
        Color borderColor = Color.TRANSPARENT;
        if (!AppConfig.windowDecorations && AppConfig.enableAlwaysOnTop) {
            borderColor = Color.GRAY;
        }

        this.setBorder(new Border(new BorderStroke(borderColor, borderColor, borderColor, borderColor,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(1), Insets.EMPTY)));
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

        double rectangleWidth = (this.getWidth() / (frequencyViewList.size() + 1) - AppConfig.barGap);

        for (int i = 0; i < frequencyViewList.size(); i++) {
            FrequencyView frequencyView = frequencyViewList.get(i);
            FrequencyBar frequencyBar = frequencyBarList.get(i);

            double shadowHeight = frequencyView.getRectangle().getHeight() - frequencyBar.getHeight();
            if (AppConfig.motionBlur != 0 && shadowHeight > 0) {
                Stop[] stops = new Stop[]{
                        new Stop(0, backgroundColor),
                        new Stop(1, frequencyBar.getColor()),
                };

                LinearGradient linearGradient = new LinearGradient(0.5, 0, 0.5,  1, true, CycleMethod.NO_CYCLE, stops);

                frequencyView.getShadow().setFill(linearGradient);
                frequencyView.getShadow().setHeight(Math.round(shadowHeight));
                frequencyView.getShadow().setOpacity(AppConfig.motionBlur / 100d);
                frequencyView.getShadow().setWidth(rectangleWidth);
            } else {
                frequencyView.getShadow().setHeight(0);
            }

            frequencyView.getRectangle().setFill(frequencyBar.getColor());
            frequencyView.getRectangle().setHeight(Math.round(frequencyBar.getHeight()));
            frequencyView.getRectangle().setWidth(rectangleWidth);

            frequencyView.setHzValue(frequencyBar.getHz());
            frequencyView.getHzLabel().setFill(Color.valueOf(AppConfig.textColor));
            frequencyView.getHzLabel().setFont(Font.font(rectangleWidth / 2.5d));

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
