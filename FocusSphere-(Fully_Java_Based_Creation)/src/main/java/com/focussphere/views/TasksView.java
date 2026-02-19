package com.focussphere.views;

import com.focussphere.App;
import com.focussphere.db.DatabaseManager;
import com.focussphere.model.Task;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;

public class TasksView extends VBox {

    private final App app;
    private TextField titleField;
    private DatePicker datePicker;
    private ComboBox<String> priorityBox;
    private ComboBox<String> filterBox;
    private VBox taskListBox;
    private Label statsLabel;

    // Stat labels
    private Label completedCount, pendingCount, highCount, overdueCount;

    public TasksView(App app) {
        this.app = app;
        setSpacing(16);
        setPadding(new Insets(24));
        getStyleClass().add("view-container");
        buildUI();
    }

    private void buildUI() {
        // Header
        Label title = new Label("\u2705  Tasks");
        title.getStyleClass().add("view-title");

        statsLabel = new Label("0% Done");
        statsLabel.getStyleClass().add("stats-pill");

        HBox header = new HBox(15, title, statsLabel);
        header.setAlignment(Pos.CENTER_LEFT);

        // Form
        titleField = new TextField();
        titleField.setPromptText("What needs to be done? (#tag to add tags)");
        titleField.getStyleClass().add("task-input");
        HBox.setHgrow(titleField, Priority.ALWAYS);

        datePicker = new DatePicker();
        datePicker.setPromptText("Due date");
        datePicker.setPrefWidth(140);

        priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll("Low", "Medium", "High");
        priorityBox.setValue("Medium");
        priorityBox.setPrefWidth(110);

        Button addBtn = new Button("+ Add");
        addBtn.getStyleClass().add("accent-button");
        addBtn.setOnAction(e -> addTask());

        titleField.setOnAction(e -> addTask());

        HBox form = new HBox(10, titleField, datePicker, priorityBox, addBtn);
        form.setAlignment(Pos.CENTER_LEFT);
        form.getStyleClass().add("card");

        // Filters
        filterBox = new ComboBox<>();
        filterBox.getItems().addAll("All", "Active", "Completed");
        filterBox.setValue("All");
        filterBox.setOnAction(e -> refresh());

        Button clearDone = new Button("Clear Completed");
        clearDone.getStyleClass().add("danger-button-small");
        clearDone.setOnAction(e -> {
            DatabaseManager.deleteCompletedTasks();
            refresh();
            app.showToast("Completed tasks cleared", true);
        });

        HBox filterRow = new HBox(10, new Label("Filter:"), filterBox,
                new Region() {
                    {
                        HBox.setHgrow(this, Priority.ALWAYS);
                    }
                }, clearDone);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        // Task list
        taskListBox = new VBox(8);
        ScrollPane scroll = new ScrollPane(taskListBox);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("view-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // Stats row
        completedCount = new Label("0");
        pendingCount = new Label("0");
        highCount = new Label("0");
        overdueCount = new Label("0");

        HBox statsRow = new HBox(12,
                createMiniStat("Completed", completedCount, "#22c55e"),
                createMiniStat("Pending", pendingCount, "#3b82f6"),
                createMiniStat("High Priority", highCount, "#f59e0b"),
                createMiniStat("Overdue", overdueCount, "#ef4444"));
        statsRow.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(header, form, filterRow, scroll, statsRow);
    }

    private VBox createMiniStat(String label, Label value, String color) {
        Label l = new Label(label);
        l.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:11;");
        value.setStyle("-fx-text-fill:" + color + "; -fx-font-size:22; -fx-font-weight:bold;");
        VBox box = new VBox(4, l, value);
        box.getStyleClass().add("mini-stat-card");
        HBox.setHgrow(box, Priority.ALWAYS);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private void addTask() {
        String text = titleField.getText().trim();
        if (text.isEmpty())
            return;

        // Extract tags (#tag)
        StringBuilder tags = new StringBuilder();
        StringBuilder cleanTitle = new StringBuilder();
        for (String word : text.split("\\s+")) {
            if (word.startsWith("#") && word.length() > 1) {
                if (tags.length() > 0)
                    tags.append(",");
                tags.append(word.substring(1));
            } else {
                if (cleanTitle.length() > 0)
                    cleanTitle.append(" ");
                cleanTitle.append(word);
            }
        }

        Task t = new Task();
        t.setTitle(cleanTitle.toString());
        t.setPriority(priorityBox.getValue());
        if (datePicker.getValue() != null)
            t.setDueDate(datePicker.getValue().toString());
        if (tags.length() > 0)
            t.setTags(tags.toString());

        DatabaseManager.addTask(t);
        titleField.clear();
        datePicker.setValue(null);
        priorityBox.setValue("Medium");
        app.showToast("Task added!", true);
        refresh();
    }

    public void refresh() {
        taskListBox.getChildren().clear();
        List<Task> tasks = DatabaseManager.getAllTasks();
        String filter = filterBox.getValue();

        int total = tasks.size();
        long done = tasks.stream().filter(Task::isCompleted).count();

        for (Task t : tasks) {
            if ("Active".equals(filter) && t.isCompleted())
                continue;
            if ("Completed".equals(filter) && !t.isCompleted())
                continue;
            taskListBox.getChildren().add(createTaskCard(t));
        }

        // Update stats
        int pct = total > 0 ? (int) (done * 100 / total) : 0;
        statsLabel.setText(pct + "% Done");

        completedCount.setText(String.valueOf(DatabaseManager.countCompletedTasks()));
        pendingCount.setText(String.valueOf(DatabaseManager.countPendingTasks()));
        highCount.setText(String.valueOf(DatabaseManager.countHighPriorityTasks()));
        overdueCount.setText(String.valueOf(DatabaseManager.countOverdueTasks()));

        if (taskListBox.getChildren().isEmpty()) {
            Label empty = new Label("No tasks yet. Add one above!");
            empty.setStyle("-fx-text-fill:#64748b; -fx-font-size:14; -fx-padding:30;");
            taskListBox.getChildren().add(empty);
        }
    }

    private HBox createTaskCard(Task t) {
        CheckBox cb = new CheckBox();
        cb.setSelected(t.isCompleted());
        cb.setOnAction(e -> {
            DatabaseManager.updateTaskCompleted(t.getId(), cb.isSelected());
            refresh();
        });

        Label titleLbl = new Label(t.getTitle());
        titleLbl.getStyleClass().add("task-title");
        titleLbl.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(titleLbl, Priority.ALWAYS);
        if (t.isCompleted()) {
            titleLbl.setStyle(titleLbl.getStyle() +
                    "-fx-strikethrough:true; -fx-text-fill:#64748b;");
        }

        HBox badges = new HBox(6);
        badges.setAlignment(Pos.CENTER_RIGHT);

        // Priority badge
        String pColor = switch (t.getPriority()) {
            case "High" -> "#ef4444";
            case "Low" -> "#22c55e";
            default -> "#f59e0b";
        };
        Label pBadge = new Label(t.getPriority());
        pBadge.setStyle("-fx-background-color:" + pColor + "22; -fx-text-fill:" + pColor + ";" +
                "-fx-padding:2 8; -fx-background-radius:10; -fx-font-size:11;");
        badges.getChildren().add(pBadge);

        // Due date badge
        if (t.getDueDate() != null && !t.getDueDate().isBlank()) {
            boolean overdue = !t.isCompleted() &&
                    LocalDate.parse(t.getDueDate()).isBefore(LocalDate.now());
            Label dBadge = new Label("\uD83D\uDCC5 " + t.getDueDate());
            dBadge.setStyle("-fx-text-fill:" + (overdue ? "#ef4444" : "#94a3b8") +
                    "; -fx-font-size:11; -fx-padding:2 6;");
            badges.getChildren().add(dBadge);
        }

        // Tags
        if (t.getTags() != null && !t.getTags().isBlank()) {
            for (String tag : t.getTags().split(",")) {
                Label tBadge = new Label("#" + tag.trim());
                tBadge.setStyle("-fx-background-color:#6366f122; -fx-text-fill:#818cf8;" +
                        "-fx-padding:2 6; -fx-background-radius:8; -fx-font-size:10;");
                badges.getChildren().add(tBadge);
            }
        }

        // Action buttons
        Button focusBtn = new Button("\u23F1");
        focusBtn.getStyleClass().add("icon-button");
        focusBtn.setTooltip(new Tooltip("Focus with Pomodoro"));
        focusBtn.setOnAction(e -> app.startPomodoroWithTask(t.getTitle()));

        Button delBtn = new Button("\uD83D\uDDD1");
        delBtn.getStyleClass().add("icon-button-danger");
        delBtn.setTooltip(new Tooltip("Delete"));
        delBtn.setOnAction(e -> {
            DatabaseManager.deleteTask(t.getId());
            app.showToast("Task deleted", false);
            refresh();
        });

        HBox card = new HBox(10, cb, titleLbl, badges, focusBtn, delBtn);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("task-card");
        card.setPadding(new Insets(12, 16, 12, 16));

        return card;
    }

    public void focusInput() {
        titleField.requestFocus();
    }
}