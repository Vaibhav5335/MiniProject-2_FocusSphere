package com.focussphere.views;

import com.focussphere.App;
import com.focussphere.db.DatabaseManager;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;

public class DashboardView extends ScrollPane {

    private final App app;
    private VBox container;

    // Widgets
    private Label pendingLabel, expenseLabel, eventsLabel;
    private TextArea scratchpad;
    private Label weatherLabel;
    private Label quoteLabel;
    private HBox moodBox;

    private final String[] quotes = {
            "\"The secret of getting ahead is getting started.\" — Mark Twain",
            "\"Focus on being productive instead of busy.\" — Tim Ferriss",
            "\"It always seems impossible until it's done.\" — Nelson Mandela",
            "\"Do what you can, with what you have, where you are.\" — Theodore Roosevelt",
            "\"Your limitation—it's only your imagination.\"",
            "\"Push yourself, because no one else is going to do it for you.\"",
            "\"Great things never come from comfort zones.\"",
            "\"Dream it. Wish it. Do it.\""
    };

    public DashboardView(App app) {
        this.app = app;
        setFitToWidth(true);
        getStyleClass().add("view-scroll");
        setHbarPolicy(ScrollBarPolicy.NEVER);

        container = new VBox(20);
        container.setPadding(new Insets(24));
        container.getStyleClass().add("view-container");

        buildUI();
        setContent(container);
    }

    private void buildUI() {
        // Title
        Label title = new Label("\uD83C\uDFE0  Dashboard");
        title.getStyleClass().add("view-title");

        // === ROW 1: Stat Cards ===
        pendingLabel = new Label("0");
        pendingLabel.getStyleClass().add("stat-number");
        VBox pendingCard = createStatCard("\u2705 Pending Tasks", pendingLabel, "#6366f1");
        pendingCard.setOnMouseClicked(e -> app.navigateTo("Tasks"));

        expenseLabel = new Label("$0.00");
        expenseLabel.getStyleClass().add("stat-number");
        VBox expenseCard = createStatCard("\uD83D\uDCB0 Total Spent", expenseLabel, "#f43f5e");
        expenseCard.setOnMouseClicked(e -> app.navigateTo("Expenses"));

        eventsLabel = new Label("0");
        eventsLabel.getStyleClass().add("stat-number");
        VBox eventsCard = createStatCard("\uD83D\uDCC5 Events Today", eventsLabel, "#10b981");
        eventsCard.setOnMouseClicked(e -> app.navigateTo("Schedule"));

        HBox statsRow = new HBox(16, pendingCard, expenseCard, eventsCard);
        statsRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(pendingCard, Priority.ALWAYS);
        HBox.setHgrow(expenseCard, Priority.ALWAYS);
        HBox.setHgrow(eventsCard, Priority.ALWAYS);

        // === ROW 2: Scratchpad + Weather ===
        // Scratchpad
        Label scratchTitle = new Label("\uD83D\uDCDD Quick Notes");
        scratchTitle.getStyleClass().add("card-title");

        scratchpad = new TextArea();
        scratchpad.setPromptText("Jot down quick thoughts...");
        scratchpad.setPrefRowCount(5);
        scratchpad.getStyleClass().add("scratchpad");
        scratchpad.setWrapText(true);

        String saved = DatabaseManager.getSetting("scratchpad");
        if (saved != null)
            scratchpad.setText(saved);

        Label saveStatus = new Label("Auto-saved");
        saveStatus.setStyle("-fx-text-fill:#22c55e; -fx-font-size:11;");

        scratchpad.textProperty().addListener((obs, o, n) -> {
            saveStatus.setText("Saving...");
            DatabaseManager.saveSetting("scratchpad", n);
            saveStatus.setText("Auto-saved \u2713");
        });

        Button clearScratch = new Button("\uD83D\uDDD1");
        clearScratch.getStyleClass().add("icon-button");
        clearScratch.setOnAction(e -> {
            scratchpad.clear();
            DatabaseManager.saveSetting("scratchpad", "");
        });

        HBox scratchHeader = new HBox(10, scratchTitle, new Region() {
            {
                HBox.setHgrow(this, Priority.ALWAYS);
            }
        },
                saveStatus, clearScratch);
        scratchHeader.setAlignment(Pos.CENTER_LEFT);

        VBox scratchCard = new VBox(8, scratchHeader, scratchpad);
        scratchCard.getStyleClass().add("card");
        HBox.setHgrow(scratchCard, Priority.ALWAYS);

        // Weather
        weatherLabel = new Label("☀ 24°C  Sunny");
        weatherLabel.setStyle("-fx-font-size:28; -fx-text-fill:#f1f5f9; -fx-font-weight:bold;");
        Label weatherSub = new Label("Simulated Weather");
        weatherSub.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:12;");

        Button refreshWeather = new Button("\u21BB  Refresh");
        refreshWeather.getStyleClass().add("accent-button-small");
        refreshWeather.setOnAction(e -> randomizeWeather());

        VBox weatherCard = new VBox(10, new Label("\uD83C\uDF24 Weather") {
            {
                getStyleClass().add("card-title");
            }
        }, weatherLabel, weatherSub, refreshWeather);
        weatherCard.getStyleClass().add("card");
        weatherCard.setPrefWidth(240);

        HBox row2 = new HBox(16, scratchCard, weatherCard);

        // === ROW 3: Mood + Quote ===
        // Mood
        Label moodTitle = new Label("\uD83D\uDE0A  How are you feeling?");
        moodTitle.getStyleClass().add("card-title");
        moodBox = new HBox(12);
        moodBox.setAlignment(Pos.CENTER_LEFT);

        String[] moods = { "😄 Awesome", "🙂 Good", "😴 Tired", "😰 Stressed" };
        String latestMood = DatabaseManager.getLatestMood();

        for (String mood : moods) {
            Button mb = new Button(mood);
            mb.getStyleClass().add("mood-button");
            if (mood.contains(latestMood != null ? latestMood : "---")) {
                mb.getStyleClass().add("mood-active");
            }
            mb.setOnAction(e -> {
                moodBox.getChildren().forEach(n -> ((Button) n).getStyleClass().remove("mood-active"));
                mb.getStyleClass().add("mood-active");
                DatabaseManager.logMood(mood.split(" ")[1]);
                app.showToast("Mood logged: " + mood, true);
            });
            moodBox.getChildren().add(mb);
        }

        VBox moodCard = new VBox(10, moodTitle, moodBox);
        moodCard.getStyleClass().add("card");
        HBox.setHgrow(moodCard, Priority.ALWAYS);

        // Quote
        quoteLabel = new Label(quotes[new Random().nextInt(quotes.length)]);
        quoteLabel.setWrapText(true);
        quoteLabel.setStyle("-fx-text-fill:#cbd5e1; -fx-font-size:14; -fx-font-style:italic;");

        Button nextQuote = new Button("\u21BB");
        nextQuote.getStyleClass().add("icon-button");
        nextQuote.setOnAction(e -> quoteLabel.setText(quotes[new Random().nextInt(quotes.length)]));

        HBox quoteHeader = new HBox(10,
                new Label("\uD83D\uDCAC  Daily Quote") {
                    {
                        getStyleClass().add("card-title");
                    }
                },
                new Region() {
                    {
                        HBox.setHgrow(this, Priority.ALWAYS);
                    }
                }, nextQuote);
        quoteHeader.setAlignment(Pos.CENTER_LEFT);

        VBox quoteCard = new VBox(10, quoteHeader, quoteLabel);
        quoteCard.getStyleClass().add("card");
        HBox.setHgrow(quoteCard, Priority.ALWAYS);

        HBox row3 = new HBox(16, moodCard, quoteCard);

        container.getChildren().addAll(title, statsRow, row2, row3);
    }

    private VBox createStatCard(String label, Label value, String color) {
        Label l = new Label(label);
        l.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:12;");
        VBox card = new VBox(8, l, value);
        card.getStyleClass().add("stat-card");
        card.setStyle("-fx-border-color:" + color + "; -fx-border-width:0 0 0 4;" +
                "-fx-border-radius:12; -fx-cursor:hand;");
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    private void randomizeWeather() {
        String[][] w = {
                { "☀", "Sunny", "28" }, { "🌤", "Partly Cloudy", "22" },
                { "🌧", "Rainy", "16" }, { "⛈", "Thunderstorm", "14" },
                { "🌥", "Cloudy", "19" }, { "❄", "Snowy", "2" },
        };
        String[] pick = w[new Random().nextInt(w.length)];
        weatherLabel.setText(pick[0] + " " + pick[2] + "°C  " + pick[1]);
    }

    public void refresh() {
        // Update pending tasks count
        int pendingCount = DatabaseManager.countPendingTasks();
        pendingLabel.setText(String.valueOf(pendingCount));
        
        // Update total expenses with proper formatting
        double totalExpenses = DatabaseManager.getTotalExpenses();
        expenseLabel.setText(String.format("$%.2f", totalExpenses));
        
        // Update today's events count
        int todayEvents = DatabaseManager.countEventsToday();
        eventsLabel.setText(String.valueOf(todayEvents));
        
        // Update mood buttons to reflect current mood
        String currentMood = DatabaseManager.getLatestMood();
        if (currentMood != null) {
            moodBox.getChildren().forEach(node -> {
                if (node instanceof Button btn) {
                    btn.getStyleClass().remove("mood-active");
                    String btnText = btn.getText();
                    if (btnText.toLowerCase().contains(currentMood.toLowerCase())) {
                        btn.getStyleClass().add("mood-active");
                    }
                }
            });
        }
        
        // Update scratchpad with saved content
        String savedScratch = DatabaseManager.getSetting("scratchpad");
        if (savedScratch != null && !savedScratch.equals(scratchpad.getText())) {
            scratchpad.setText(savedScratch);
        }
    }
}