package com.lazydash.audio.spectrum.ui.code.spectral;

import com.lazydash.audio.spectrum.core.model.FrequencyBar;
import com.lazydash.audio.spectrum.system.config.AppConfig;
import com.lazydash.audio.spectrum.system.config.WindowProperty;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SpectralView extends GridPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpectralView.class);

    private final int minWidth = 25;
    private List<FrequencyView> frequencyViewList = new ArrayList<>();

    public void configure() {
        this.setAlignment(Pos.BOTTOM_CENTER);
        this.setHgap(AppConfig.getBarGap());

        WindowProperty.heightProperty.addListener((observable, oldValue, newValue) -> {
            AppConfig.setMaxBarHeight(newValue.doubleValue() - AppConfig.getHzLabelHeight());
        });

        WindowProperty.widthProperty.addListener((observable, oldValue, newValue) -> {
            this.setPrefWidth(newValue.doubleValue());
        });

        WindowProperty.widthProperty.addListener((observable, oldValue, newValue) -> {
            frequencyViewList.forEach(frequencyView -> {
                int rectangleWidth = (int) (this.getWidth() / frequencyViewList.size()) - AppConfig.getBarGap();
                if (rectangleWidth < minWidth) {
                    rectangleWidth = minWidth;
                }
                frequencyView.getRectangle().setWidth(rectangleWidth);
            });
        });
    }

    public void updateState(List<FrequencyBar> frequencyBarList) {
        if (frequencyBarList.size() != frequencyViewList.size()) {
            createBars(frequencyBarList);
//            LOGGER.info("create bars");

        } else {
            updateBars(frequencyBarList);
//            LOGGER.info("update bars");
        }
    }

    private void updateBars(List<FrequencyBar> frequencyBarList) {
        for (int i = 0; i < frequencyViewList.size(); i++) {
            FrequencyView frequencyView = frequencyViewList.get(i);
            FrequencyBar frequencyBar = frequencyBarList.get(i);
            frequencyView.setBarColor(frequencyBar.getColor());
            frequencyView.setBarHeight(frequencyBar.getHeight());
            frequencyView.setHzValue(frequencyBar.getHz());
        }
    }

    private void createBars(List<FrequencyBar> frequencyBarList) {
        frequencyViewList.clear();
        this.getChildren().clear();
        this.getColumnConstraints().clear();

        for (int i = 0; i < frequencyBarList.size(); i++) {
            FrequencyView frequencyView = new FrequencyView();
            FrequencyBar frequencyBar = frequencyBarList.get(i);
            frequencyView.setBarColor(frequencyBar.getColor());
            frequencyView.setBarHeight(frequencyBar.getHeight());
            frequencyView.setHzValue(frequencyBar.getHz());
            frequencyView.setHzHeight(AppConfig.getHzLabelHeight());

            int width = (int) (WindowProperty.widthProperty.getValue() / frequencyBarList.size()) - AppConfig.getBarGap();
            if (width < minWidth) {
                width = minWidth;
            }
            frequencyView.getRectangle().setWidth(width);

            frequencyViewList.add(frequencyView);

            // todo encapsulate rectangle and hzLabel in FrequencyView
            this.add(frequencyView.getRectangle(), i, 0);
            this.add(frequencyView.getHzLabel(), i, 1);

            GridPane.setValignment(frequencyView.getRectangle(), VPos.BOTTOM);
            GridPane.setHalignment(frequencyView.getHzLabel(), HPos.CENTER);

        }
    }
}
