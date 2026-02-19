package com.focussphere.views;

import com.focussphere.App;
import com.focussphere.db.DatabaseManager;
import com.focussphere.model.ScheduleEvent;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ScheduleView extends VBox {

    private final App app;
    private Pane timelinePane;
    private Label dateLabel;
    private static final double HOUR_HEIGHT = 60;

    public ScheduleView(App app) {
        this.app = app;
        setSpacing(16);
        setPadding(new Insets(24));
        getStyleClass().add("view-container");
        buildUI();
    }

    private void buildUI() {
        Label title = new Label("\uD83D\uDCC5  Schedule");
        title.getStyleClass().add("view-title");

        dateLabel = new Label(LocalDate.now().toString());
        dateLabel.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:13;");

        Button addBtn = new Button("+ Add Block");
        addBtn.getStyleClass().add("accent-button");
        addBtn.setOnAction(e -> showEventDialog());

        HBox header = new HBox(15, title, dateLabel,
                new Region() {
                    {
                        HBox.setHgrow(this, Priority.ALWAYS);
                    }
                }, addBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        // Timeline
        timelinePane = new Pane();
        timelinePane.setPrefHeight(24 * HOUR_HEIGHT);
        timelinePane.setMinHeight(24 * HOUR_HEIGHT);
        timelinePane.getStyleClass().add("timeline-pane");

        ScrollPane scroll = new ScrollPane(timelinePane);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("view-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // Auto-scroll to current hour
        javafx.application.Platform.runLater(() -> {
            double scrollTo = (LocalTime.now().getHour() - 1) * HOUR_HEIGHT /
                    (24 * HOUR_HEIGHT);
            scroll.setVvalue(Math.max(0, scrollTo));
        });

        getChildren().addAll(header, scroll);
    }

    public void refresh() {
        timelinePane.getChildren().clear();
        String today = LocalDate.now().toString();

        // Draw hour lines
        for (int h = 0; h < 24; h++) {
            String label = String.format("%02d:00", h);
            Label hourLabel = new Label(label);
            hourLabel.setStyle("-fx-text-fill:#64748b; -fx-font-size:11;");
            hourLabel.setLayoutX(10);
            hourLabel.setLayoutY(h * HOUR_HEIGHT + 2);

            Region line = new Region();
            line.setPrefHeight(1);
            line.setPrefWidth(2000);
            line.setStyle("-fx-background-color:#334155;");
            line.setLayoutX(60);
            line.setLayoutY(h * HOUR_HEIGHT);

            timelinePane.getChildren().addAll(hourLabel, line);
        }

        // Current time indicator
        int nowMin = LocalTime.now().getHour() * 60 + LocalTime.now().getMinute();
        double nowY = (nowMin / 60.0) * HOUR_HEIGHT;

        Region nowLine = new Region();
        nowLine.setPrefHeight(2);
        nowLine.setPrefWidth(2000);
        nowLine.setStyle("-fx-background-color:#ef4444;");
        nowLine.setLayoutX(60);
        nowLine.setLayoutY(nowY);

        Label nowDot = new Label("\u25CF");
        nowDot.setStyle("-fx-text-fill:#ef4444; -fx-font-size:12;");
        nowDot.setLayoutX(52);
        nowDot.setLayoutY(nowY - 7);

        timelinePane.getChildren().addAll(nowLine, nowDot);

        // Events
        List<ScheduleEvent> events = DatabaseManager.getEventsForDate(today);
        for (ScheduleEvent ev : events) {
            double y = (ev.getStartMinute() / 60.0) * HOUR_HEIGHT;
            double h = (ev.getDurationMinutes() / 60.0) * HOUR_HEIGHT;
            h = Math.max(h, 20);

            VBox block = new VBox(2);
            block.setStyle("-fx-background-color:" + ev.getColor() + "cc;" +
                    "-fx-background-radius:8; -fx-padding:6 10;");
            block.setPrefHeight(h);
            block.setPrefWidth(300);
            block.setLayoutX(80);
            block.setLayoutY(y);

            Label evTitle = new Label(ev.getTitle());
            evTitle.setStyle("-fx-text-fill:white; -fx-font-weight:bold; -fx-font-size:12;");

            Label evTime = new Label(ev.getStartTime() + " — " + ev.getEndTime());
            evTime.setStyle("-fx-text-fill:rgba(255,255,255,0.8); -fx-font-size:10;");

            block.getChildren().addAll(evTitle, evTime);

            // Right-click to delete
            ContextMenu ctx = new ContextMenu();
            MenuItem del = new MenuItem("Delete");
            del.setOnAction(e -> {
                DatabaseManager.deleteEvent(ev.getId());
                app.showToast("Event deleted", false);
                refresh();
            });
            ctx.getItems().add(del);
            block.setOnContextMenuRequested(e -> ctx.show(block, e.getScreenX(), e.getScreenY()));

            timelinePane.getChildren().add(block);
        }
    }

    private void showEventDialog() {
        Dialog<ScheduleEvent> dlg = new Dialog<>();
        dlg.setTitle("Add Schedule Block");
        dlg.setHeaderText("Create a new time block");

        TextField titleField = new TextField();
        titleField.setPromptText("Event title");

        // Time spinners
        Spinner<Integer> startH = new Spinner<>(0, 23, 9);
        startH.setPrefWidth(70);
        Spinner<Integer> startM = new Spinner<>(0, 59, 0, 15);
        startM.setPrefWidth(70);

        Spinner<Integer> endH = new Spinner<>(0, 23, 10);
        endH.setPrefWidth(70);
        Spinner<Integer> endM = new Spinner<>(0, 59, 0, 15);
        endM.setPrefWidth(70);

        // Color picker
        HBox colorRow = new HBox(8);
        String[] colors = { "#6366f1", "#ec4899", "#10b981", "#f59e0b" };
        ToggleGroup colorGroup = new ToggleGroup();
        String[] selectedColor = { colors[0] };

        for (String col : colors) {
            RadioButton rb = new RadioButton();
            rb.setToggleGroup(colorGroup);
            rb.setStyle("-fx-background-color:" + col + "; -fx-background-radius:12;" +
                    "-fx-min-width:24; -fx-min-height:24; -fx-cursor:hand;");
            rb.setOnAction(e -> selectedColor[0] = col);
            if (col.equals(colors[0]))
                rb.setSelected(true);
            colorRow.getChildren().add(rb);
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0, 3, 1);
        grid.add(new Label("Start:"), 0, 1);
        grid.add(startH, 1, 1);
        grid.add(new Label(":"), 2, 1);
        grid.add(startM, 3, 1);
        grid.add(new Label("End:"), 0, 2);
        grid.add(endH, 1, 2);
        grid.add(new Label(":"), 2, 2);
        grid.add(endM, 3, 2);
        grid.add(new Label("Color:"), 0, 3);
        grid.add(colorRow, 1, 3, 3, 1);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK && !titleField.getText().isBlank()) {
                String st = String.format("%02d:%02d", startH.getValue(), startM.getValue());
                String et = String.format("%02d:%02d", endH.getValue(), endM.getValue());
                return new ScheduleEvent(titleField.getText(), st, et,
                        selectedColor[0], LocalDate.now().toString());
            }
            return null;
        });

        dlg.showAndWait().ifPresent(ev -> {
            DatabaseManager.addEvent(ev);
            app.showToast("Event added!", true);
            refresh();
        });
    }
}