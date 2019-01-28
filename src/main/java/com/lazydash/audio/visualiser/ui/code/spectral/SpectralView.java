package com.lazydash.audio.visualiser.ui.code.spectral;

import com.lazydash.audio.visualiser.core.model.ColorBand;
import com.lazydash.audio.visualiser.system.config.AppConfig;
import com.lazydash.audio.visualiser.system.config.ColorConfig;
import com.lazydash.audio.visualiser.system.config.WindowConfig;
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

    public Color getGlobalColorLight(int startHz, int endHz) {
        return getGlobalColor(startHz, endHz, false);
    }

    public Color getGlobalColorDark(int startHz, int endHz) {
        return getGlobalColor(startHz, endHz, true);
    }

    private Color getGlobalColor(int startHz, int endHz, boolean darkMode) {
        double sumIntensity = 0;
        double maxIntensity = 0;

        double sumRed = 0;
        double sumGreen = 0;
        double sumBlue = 0;

        int nrBars = 0;

        if (frequencyViewList.size() == 0){
            return Color.WHITE;
        }

        for (FrequencyView frequencyView : frequencyViewList) {
            if (startHz <= Integer.valueOf(frequencyView.getHzLabel().getText())
            && Integer.valueOf(frequencyView.getHzLabel().getText()) <= endHz ) {

                // this can happen when changing the max bar height config from the ui
                double barHeight = frequencyView.getRectangle().getHeight();
                if (barHeight > AppConfig.getMaxBarHeight()) {
                    barHeight = AppConfig.getMaxBarHeight();
                }

                double barIntensity = barHeight / AppConfig.getMaxBarHeight();

                Color barColor = (Color) frequencyView.getRectangle().getFill();
                sumRed = sumRed + (barColor.getRed() * barIntensity);
                sumGreen = sumGreen + (barColor.getGreen() * barIntensity);
                sumBlue = sumBlue + (barColor.getBlue() * barIntensity);

                sumIntensity = sumIntensity + barIntensity;

                if (barIntensity > maxIntensity) {
                    maxIntensity = barIntensity;
                }

                nrBars++;
            }
        }

        double avgRed = sumRed / nrBars;
        double avgGreen = sumGreen / nrBars;
        double avgBlue = sumBlue / nrBars;

        double avgIntensity = sumIntensity / nrBars;

        Color baseColor = ColorConfig.baseColor;
        Color mixColor = baseColor.interpolate(Color.color(avgRed, avgGreen, avgBlue, 1), maxIntensity);

        Color hsb;
        if (darkMode) {
            hsb = Color.hsb(mixColor.getHue(), mixColor.getSaturation(), avgIntensity);

        } else {
            hsb = Color.hsb(mixColor.getHue(), avgIntensity, 1);
        }

        return hsb;
    }
}
