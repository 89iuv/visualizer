package com.lazydash.audio.visualizer.ui.code.spectral;

import com.lazydash.audio.visualizer.core.model.FrequencyBar;
import com.lazydash.audio.visualizer.system.config.AppConfig;
import com.lazydash.audio.visualizer.system.config.WindowConfig;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public class SpectralView extends GridPane {
    private List<FrequencyView> frequencyViewList = new ArrayList<>();

    public void configure() {
        this.setAlignment(Pos.BOTTOM_CENTER);
        this.setHgap(AppConfig.getBarGap());

        WindowConfig.heightProperty.addListener((observable, oldValue, newValue) -> {
            AppConfig.setMaxBarHeight(newValue.doubleValue() - AppConfig.getHzLabelHeight());
        });

        WindowConfig.widthProperty.addListener((observable, oldValue, newValue) -> {
            this.setPrefWidth(newValue.doubleValue());
        });

        WindowConfig.widthProperty.addListener((observable, oldValue, newValue) -> {
            frequencyViewList.forEach(frequencyView -> {
                frequencyView.getRectangle().setWidth((int) (this.getWidth() / frequencyViewList.size()) - AppConfig.getBarGap());
            });
        });
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
            frequencyView.setBarHeight(frequencyBar.getHeight());
        }
    }

    private void createBars(List<FrequencyBar> frequencyBarList) {
        frequencyViewList.clear();
        this.getChildren().clear();

        for (int i = 0; i < frequencyBarList.size(); i++) {
            FrequencyView frequencyView = new FrequencyView();
            FrequencyBar frequencyBar = frequencyBarList.get(i);
            frequencyView.setBarColor(frequencyBar.getColor());
            frequencyView.setBarHeight(frequencyBar.getHeight());
            frequencyView.setHzValue(frequencyBar.getHz());
            frequencyView.setHzHeight(AppConfig.getHzLabelHeight());

            frequencyView.getRectangle().setWidth((int) (WindowConfig.widthProperty.getValue() / frequencyBarList.size()) - AppConfig.getBarGap());

            frequencyViewList.add(frequencyView);

            // todo encapsulate rectangle and hzLabel in FrequencyView
            this.add(frequencyView.getRectangle(), i, 0);
            this.add(frequencyView.getHzLabel(), i, 1, 1, 1);

            GridPane.setValignment(frequencyView.getRectangle(), VPos.BOTTOM);
            GridPane.setHalignment(frequencyView.getHzLabel(), HPos.CENTER);
        }
    }
}
