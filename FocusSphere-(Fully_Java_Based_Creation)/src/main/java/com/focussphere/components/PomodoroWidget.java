package com.focussphere.components;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class PomodoroWidget extends VBox {

    private Label timerLabel;
    private Label taskLabel;
    private Label modeLabel;
    private Button playBtn, resetBtn, skipBtn;
    private ToggleButton modeToggle;

    private Timeline timeline;
    private int secondsLeft = 25 * 60;
    private boolean running = false;
    private boolean isWorkMode = true;

    public PomodoroWidget() {
        getStyleClass().add("pomodoro-widget");
        setSpacing(12);
        setPadding(new Insets(20));
        setMaxWidth(280);
        setMaxHeight(320);
        setAlignment(Pos.CENTER);

        // Title
        Label title = new Label("\u23F1 Pomodoro Timer");
        title.setStyle("-fx-font-size:16; -fx-font-weight:bold; -fx-text-fill:#f1f5f9;");

        // Task name
        taskLabel = new Label("No task selected");
        taskLabel.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:12;");
        taskLabel.setWrapText(true);

        // Mode label
        modeLabel = new Label("Deep Work");
        modeLabel.setStyle("-fx-text-fill:#6366f1; -fx-font-weight:bold; -fx-font-size:13;");

        // Timer display
        timerLabel = new Label("25:00");
        timerLabel.setStyle("-fx-font-size:48; -fx-font-weight:bold; -fx-text-fill:#f1f5f9;" +
                            "-fx-font-family:'Consolas';");

        // Mode toggle
        modeToggle = new ToggleButton("Switch to Break");
        modeToggle.getStyleClass().add("mode-toggle-btn");
        modeToggle.setOnAction(e -> toggleMode());

        // Controls
        playBtn = new Button("\u25B6");
        resetBtn = new Button("\u21BA");
        skipBtn = new Button("\u23ED");

        playBtn.getStyleClass().add("pomo-ctrl-btn");
        resetBtn.getStyleClass().add("pomo-ctrl-btn");
        skipBtn.getStyleClass().add("pomo-ctrl-btn");

        playBtn.setOnAction(e -> {
            if (running) pauseTimer(); else startTimer();
        });
        resetBtn.setOnAction(e -> resetTimer());
        skipBtn.setOnAction(e -> {
            toggleMode();
            resetTimer();
        });

        HBox controls = new HBox(12, playBtn, resetBtn, skipBtn);
        controls.setAlignment(Pos.CENTER);

        getChildren().addAll(title, taskLabel, modeLabel, timerLabel, controls, modeToggle);

        // Timer animation
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    private void tick() {
        secondsLeft--;
        if (secondsLeft <= 0) {
            pauseTimer();
            timerLabel.setStyle(timerLabel.getStyle().replace("#f1f5f9", "#22c55e"));
            secondsLeft = 0;
        }
        updateDisplay();
    }

    private void updateDisplay() {
        int m = secondsLeft / 60;
        int s = secondsLeft % 60;
        timerLabel.setText(String.format("%02d:%02d", m, s));
    }

    public void startTimer() {
        running = true;
        playBtn.setText("\u23F8"); // ⏸
        timeline.play();
    }

    public void pauseTimer() {
        running = false;
        playBtn.setText("\u25B6"); // ▶
        timeline.pause();
    }

    public void resetTimer() {
        pauseTimer();
        secondsLeft = isWorkMode ? 25 * 60 : 5 * 60;
        timerLabel.setStyle("-fx-font-size:48; -fx-font-weight:bold; -fx-text-fill:#f1f5f9;" +
                            "-fx-font-family:'Consolas';");
        updateDisplay();
    }

    private void toggleMode() {
        isWorkMode = !isWorkMode;
        modeLabel.setText(isWorkMode ? "Deep Work" : "Short Break");
        modeToggle.setText(isWorkMode ? "Switch to Break" : "Switch to Work");
        modeLabel.setStyle("-fx-font-weight:bold; -fx-font-size:13; -fx-text-fill:" +
                           (isWorkMode ? "#6366f1" : "#22c55e") + ";");
        resetTimer();
    }

    public void setTaskName(String name) {
        taskLabel.setText(name);
    }
}