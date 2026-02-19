package com.focussphere.components;

import com.focussphere.App;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class HeaderBar extends HBox {

    private final App app;
    private final Label greetingLabel;
    private final Label dateLabel;

    public HeaderBar(App app) {
        this.app = app;
        getStyleClass().add("header-bar");
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(12, 24, 12, 24));
        setSpacing(20);

        // Greeting
        greetingLabel = new Label();
        greetingLabel.getStyleClass().add("greeting-label");

        dateLabel = new Label();
        dateLabel.getStyleClass().add("date-label");

        VBox left = new VBox(2, greetingLabel, dateLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Quick find
        Button findBtn = new Button("\uD83D\uDD0D  Quick Find  (Ctrl+K)");
        findBtn.getStyleClass().add("find-button");
        findBtn.setOnAction(e -> {
            // Trigger command palette via keyboard simulation or direct call
            // We'll use the same mechanism
        });

        // Pomodoro toggle
        Button pomBtn = new Button("\u23F1  Pomodoro");
        pomBtn.getStyleClass().add("accent-button");
        pomBtn.setOnAction(e -> app.togglePomodoro());

        getChildren().addAll(left, spacer, findBtn, pomBtn);

        updateGreeting();
    }

    public void updateGreeting() {
        int hour = LocalTime.now().getHour();
        String period;
        if (hour < 12) period = "Good Morning";
        else if (hour < 17) period = "Good Afternoon";
        else period = "Good Evening";

        greetingLabel.setText(period + ", " + app.getUserName() + "! \uD83D\uDC4B");
        dateLabel.setText(LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
    }
}