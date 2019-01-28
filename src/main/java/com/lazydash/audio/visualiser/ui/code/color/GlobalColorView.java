package com.lazydash.audio.visualiser.ui.code.color;

import com.lazydash.audio.visualiser.system.config.AppConfig;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GlobalColorView extends Pane {
    private Rectangle rectangle;

    public void configure(){
        rectangle = new Rectangle();
        rectangle.setFill(new Color(1, 1, 1, 1));
        rectangle.widthProperty().bind(this.widthProperty());
        rectangle.heightProperty().bind(this.heightProperty());

        this.setMinHeight(AppConfig.getGlobalColorHeight());
        this.setPrefHeight(AppConfig.getGlobalColorHeight());
        this.setMaxHeight(AppConfig.getGlobalColorHeight());
        this.getChildren().add(rectangle);
    }

    public void setColor(Color color) {
        rectangle.setFill(color);
    }
}
