package com.focussphere.views;

import com.focussphere.App;
import com.focussphere.db.DatabaseManager;
import com.focussphere.model.Habit;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class HabitsView extends VBox {

    private final App app;
    private VBox habitListBox;

    public HabitsView(App app) {
        this.app = app;
        setSpacing(16);
        setPadding(new Insets(24));
        getStyleClass().add("view-container");
        buildUI();
    }

    private void buildUI() {
        Label title = new Label("\uD83D\uDD04  Habits");
        title.getStyleClass().add("view-title");

        Button addBtn = new Button("+ New Habit");
        addBtn.getStyleClass().add("accent-button");
        addBtn.setOnAction(e -> {
            TextInputDialog dlg = new TextInputDialog();
            dlg.setTitle("New Habit");
            dlg.setHeaderText("Track a new daily habit");
            dlg.setContentText("Habit name:");
            dlg.showAndWait().ifPresent(name -> {
                if (!name.isBlank()) {
                    DatabaseManager.addHabit(new Habit(name));
                    app.showToast("Habit created!", true);
                    refresh();
                }
            });
        });

        HBox header = new HBox(15, title,
                new Region() {
                    {
                        HBox.setHgrow(this, Priority.ALWAYS);
                    }
                }, addBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        habitListBox = new VBox(12);
        ScrollPane scroll = new ScrollPane(habitListBox);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("view-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        getChildren().addAll(header, scroll);
    }

    public void refresh() {
        habitListBox.getChildren().clear();
        List<Habit> habits = DatabaseManager.getAllHabits();

        if (habits.isEmpty()) {
            Label empty = new Label("No habits yet. Create one to start tracking!");
            empty.setStyle("-fx-text-fill:#64748b; -fx-font-size:14; -fx-padding:30;");
            habitListBox.getChildren().add(empty);
            return;
        }

        for (Habit h : habits) {
            habitListBox.getChildren().add(createHabitCard(h));
        }
    }

    private VBox createHabitCard(Habit h) {
        // Header row
        Label name = new Label(h.getName());
        name.setStyle("-fx-text-fill:#f1f5f9; -fx-font-size:16; -fx-font-weight:bold;");

        Label streak = new Label("\uD83D\uDD25 " + h.getCurrentStreak() + " day streak");
        streak.setStyle("-fx-text-fill:#f59e0b; -fx-font-size:12;");

        Button delBtn = new Button("\uD83D\uDDD1");
        delBtn.getStyleClass().add("icon-button-danger");
        delBtn.setOnAction(e -> {
            DatabaseManager.deleteHabit(h.getId());
            app.showToast("Habit deleted", false);
            refresh();
        });

        HBox headerRow = new HBox(10, name, streak,
                new Region() {
                    {
                        HBox.setHgrow(this, Priority.ALWAYS);
                    }
                }, delBtn);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        // 7-day grid
        HBox dayGrid = new HBox(8);
        dayGrid.setAlignment(Pos.CENTER_LEFT);
        LocalDate today = LocalDate.now();
        Set<String> completed = h.getCompletedDaysSet();

        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            String dateStr = day.toString();
            String dayInitial = day.getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, Locale.ENGLISH).substring(0, 2);

            VBox dayBox = new VBox(4);
            dayBox.setAlignment(Pos.CENTER);
            dayBox.setPrefSize(48, 56);
            dayBox.getStyleClass().add("habit-day");

            Label dl = new Label(dayInitial);
            dl.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:10;");

            Label dayNum = new Label(String.valueOf(day.getDayOfMonth()));

            if (completed.contains(dateStr)) {
                dayBox.getStyleClass().add("habit-day-active");
                dayNum.setStyle("-fx-text-fill:white; -fx-font-weight:bold; -fx-font-size:14;");
            } else {
                dayNum.setStyle("-fx-text-fill:#64748b; -fx-font-size:14;");
            }

            dayBox.getChildren().addAll(dl, dayNum);
            dayBox.setCursor(javafx.scene.Cursor.HAND);
            dayBox.setOnMouseClicked(e -> {
                h.toggleDay(dateStr);
                DatabaseManager.updateHabitDays(h.getId(), h.getCompletedDays());
                if (h.getCompletedDaysSet().contains(dateStr)) {
                    app.showToast(h.getName() + " — Day completed! \uD83C\uDF89", true);
                }
                refresh();
            });

            dayGrid.getChildren().add(dayBox);
        }

        VBox card = new VBox(12, headerRow, dayGrid);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(16));
        return card;
    }
}