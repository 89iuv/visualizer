package com.lazydash.audio.visualizer.ui.code.spectral;

import com.lazydash.audio.visualizer.core.model.FrequencyBar;
import com.lazydash.audio.visualizer.system.config.AppConfig;
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

    public void updateState(List<FrequencyBar> frequencyBarList) {
        if (frequencyBarList.size() != frequencyViewList.size()) {
            createBars(frequencyBarList);

        } else {
            updateBars(frequencyBarList);
        }
    }

    private void updateBars(List<FrequencyBar> frequencyBarList) {
        for (int i = 0; i < frequencyViewList.size(); i++) {
            FrequencyView frequencyView = frequencyViewList.get(i);
            FrequencyBar frequencyBar = frequencyBarList.get(i);
            frequencyView.setBarColor(frequencyBar.getColor());
            frequencyView.setHzValue(frequencyBar.getHz());
            frequencyView.setBarHeight(frequencyBar.getHeight());

        }
    }

    private void createBars(List<FrequencyBar> frequencyBarList) {
        frequencyViewList.clear();
        this.getChildren().clear();
        this.setHgap(AppConfig.getBarGap());

        for (int i = 0; i < frequencyBarList.size(); i++) {
            FrequencyView frequencyView = new FrequencyView();
            FrequencyBar frequencyBar = frequencyBarList.get(i);
            frequencyView.setBarColor(frequencyBar.getColor());
            frequencyView.setHzValue(frequencyBar.getHz());
            frequencyView.setBarHeight(frequencyBar.getHeight());
            frequencyView.setHzHeight(AppConfig.getHzLabelHeight());

            frequencyViewList.add(frequencyView);

            // todo encapsulate rectangle and hzLabel in FrequencyView
            this.add(frequencyView.getRectangle(), i, 0);
            this.add(frequencyView.getHzLabel(), i, 1);

            GridPane.setValignment(frequencyView.getRectangle(), VPos.BOTTOM);
            GridPane.setHalignment(frequencyView.getHzLabel(), HPos.CENTER);

            frequencyView.getRectangle().widthProperty().bind(
                    WindowConfig.widthProperty
                            .divide(frequencyBarList.size())
                            .subtract(AppConfig.getBarGap() + 1));
        }
    }

}
