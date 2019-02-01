package com.lazydash.audio.visualizer.ui.code.spectral;

import com.lazydash.audio.visualizer.core.model.ColorBand;
import com.lazydash.audio.visualizer.system.config.AppConfig;
import com.lazydash.audio.visualizer.system.config.ColorConfig;
import com.lazydash.audio.visualizer.system.config.WindowConfig;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SpectralView extends GridPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpectralView.class);

    private List<FrequencyView> frequencyViewList = new ArrayList<>();

    public void configure(){
        this.setAlignment(Pos.BOTTOM_CENTER);
        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        this.heightProperty().addListener((observable, oldValue, newValue) ->
                AppConfig.setMaxBarHeight(newValue.doubleValue() - AppConfig.getHzLabelHeight()));
    }

    public void updateState(double[] hzBins, float[] amplitudeArray) {
        if (hzBins.length != frequencyViewList.size()) {
            createBars(hzBins);
            updateBarsColor();
        }

        for (int i = 0; i < frequencyViewList.size(); i++) {
            FrequencyView frequencyView = frequencyViewList.get(i);
            frequencyView.getHzLabel().setText(String.valueOf(Math.round(hzBins[i])));
            frequencyView.getRectangle().setHeight(Math.round(amplitudeArray[i]));

//            Color fill = (Color) frequencyView.getRectangle().getFill();
//            frequencyView.getRectangle().setFill(Color.color(fill.getRed(), fill.getGreen(), fill.getBlue(), amplitudeArray[i] / AppConfig.getMaxBarHeight()));
        }
    }

    private void createBars(double[] hzArray) {
        frequencyViewList.clear();
        this.getChildren().clear();
        this.setHgap(AppConfig.getBarGap());

        for (int i = 0; i < hzArray.length; i++) {
            FrequencyView frequencyView = new FrequencyView(
                    (int) Math.round(hzArray[i]),
                    AppConfig.getHzLabelHeight(),
                    AppConfig.getMinBarHeight(),
                    AppConfig.getBarWidth());

            frequencyViewList.add(frequencyView);

            this.add(frequencyView.getRectangle(), i, 0);
            this.add(frequencyView.getHzLabel(), i, 1);

            GridPane.setValignment(frequencyView.getRectangle(), VPos.BOTTOM);
            GridPane.setHalignment(frequencyView.getHzLabel(), HPos.CENTER);

            frequencyView.getRectangle().widthProperty().bind(WindowConfig.widthProperty.divide(hzArray.length).subtract(AppConfig.getBarGap() + 1));
        }
    }

    private void updateBarsColor() {
        for (ColorBand colorBand: ColorConfig.colorBands) {
            setBarsColor(
                    colorBand.getStartColor(),
                    colorBand.getEndColor(),
                    colorBand.getStartHz(),
                    colorBand.getEndHz()
            );
        }
    }

    private void setBarsColor(Color startColor, Color endColor, int startHz, int endHz) {
        int countFreq = 0;
        for (FrequencyView frequencyView : frequencyViewList) {
            if (startHz <= Integer.valueOf(frequencyView.getHzLabel().getText())
                    && Integer.valueOf(frequencyView.getHzLabel().getText()) <= endHz ) {
                countFreq++;
            }
        }

        double step = 0.0;
        double stepIncrement = 1d / countFreq;
        for (FrequencyView frequencyView : frequencyViewList) {
            if (startHz <= Integer.valueOf(frequencyView.getHzLabel().getText())
                    && Integer.valueOf(frequencyView.getHzLabel().getText()) <= endHz ) {
                frequencyView.getRectangle().setFill(startColor.interpolate(endColor, step));
                step = step + stepIncrement;
            }
        }
    }

}
