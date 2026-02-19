package com.focussphere.components;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class ToastNotification {

    public static void show(StackPane parent, String message, boolean success) {
        Label toast = new Label((success ? "\u2705  " : "\u274C  ") + message);
        toast.getStyleClass().add("toast");
        toast.setStyle(toast.getStyle() +
            "-fx-background-color:" + (success ? "#166534" : "#991b1b") + ";" +
            "-fx-text-fill:white; -fx-padding:12 24; -fx-background-radius:12;" +
            "-fx-font-size:14; -fx-font-weight:bold;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.4),10,0,0,4);");

        StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
        StackPane.setMargin(toast, new Insets(0, 0, 30, 0));
        toast.setOpacity(0);
        parent.getChildren().add(toast);

        // Slide up + fade in
        TranslateTransition slide = new TranslateTransition(Duration.millis(300), toast);
        slide.setFromY(30);
        slide.setToY(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toast);
        fadeIn.setToValue(1);

        ParallelTransition in = new ParallelTransition(slide, fadeIn);
        in.play();

        // Auto dismiss after 2.5s
        PauseTransition hold = new PauseTransition(Duration.seconds(2.5));
        hold.setOnFinished(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toast);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(ev -> parent.getChildren().remove(toast));
            fadeOut.play();
        });
        hold.play();
    }
}