package com.focussphere.views;

import com.focussphere.App;
import com.focussphere.db.DatabaseManager;
import com.focussphere.model.Expense;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.*;

public class ExpensesView extends HBox {

    private final App app;
    private TextField descField, amountField, categoryField;
    private DatePicker datePicker;
    private VBox transactionList;
    private Label totalLabel, budgetLabel;
    private ProgressBar budgetBar;
    private double monthlyBudget = 1000;

    public ExpensesView(App app) {
        this.app = app;
        setSpacing(20);
        setPadding(new Insets(24));
        getStyleClass().add("view-container");

        String savedBudget = DatabaseManager.getSetting("monthlyBudget");
        if (savedBudget != null) {
            try {
                monthlyBudget = Double.parseDouble(savedBudget);
            } catch (Exception ignore) {
            }
        }

        buildUI();
    }

    private void buildUI() {
        // === LEFT: Form ===
        VBox leftPane = new VBox(14);
        leftPane.setPrefWidth(340);
        leftPane.setMinWidth(300);

        Label title = new Label("\uD83D\uDCB0  Add Expense");
        title.getStyleClass().add("view-title");

        descField = new TextField();
        descField.setPromptText("Description");

        amountField = new TextField();
        amountField.setPromptText("Amount ($)");

        datePicker = new DatePicker(LocalDate.now());

        categoryField = new TextField();
        categoryField.setPromptText("Category (Food, Transport...)");

        Button addBtn = new Button("+ Add Expense");
        addBtn.getStyleClass().add("accent-button");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e -> addExpense());

        VBox formCard = new VBox(10, descField, amountField, datePicker, categoryField, addBtn);
        formCard.getStyleClass().add("card");
        formCard.setPadding(new Insets(16));

        leftPane.getChildren().addAll(title, formCard);

        // === RIGHT: Transactions ===
        VBox rightPane = new VBox(14);
        HBox.setHgrow(rightPane, Priority.ALWAYS);

        // Budget bar
        Button setBudgetBtn = new Button("Set Budget: $" + String.format("%.0f", monthlyBudget));
        setBudgetBtn.getStyleClass().add("accent-button-small");
        setBudgetBtn.setOnAction(e -> {
            TextInputDialog dlg = new TextInputDialog(String.valueOf(monthlyBudget));
            dlg.setTitle("Monthly Budget");
            dlg.setHeaderText("Set your monthly spending limit");
            dlg.setContentText("Amount ($):");
            dlg.showAndWait().ifPresent(val -> {
                try {
                    monthlyBudget = Double.parseDouble(val);
                    DatabaseManager.saveSetting("monthlyBudget", val);
                    setBudgetBtn.setText("Set Budget: $" + String.format("%.0f", monthlyBudget));
                    refresh();
                } catch (NumberFormatException ignore) {
                }
            });
        });

        budgetBar = new ProgressBar(0);
        budgetBar.setMaxWidth(Double.MAX_VALUE);
        budgetBar.setPrefHeight(20);

        budgetLabel = new Label("$0 / $" + String.format("%.0f", monthlyBudget));
        budgetLabel.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:12;");

        VBox budgetCard = new VBox(8, setBudgetBtn, budgetBar, budgetLabel);
        budgetCard.getStyleClass().add("card");
        budgetCard.setPadding(new Insets(12));

        // Total
        totalLabel = new Label("$0.00");
        totalLabel.setStyle("-fx-text-fill:#f1f5f9; -fx-font-size:24; -fx-font-weight:bold;");

        Label totalSub = new Label("Total Spent");
        totalSub.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:12;");

        HBox totalRow = new HBox(10, totalLabel, totalSub);
        totalRow.setAlignment(Pos.CENTER_LEFT);

        // Transaction list
        transactionList = new VBox(6);
        ScrollPane scroll = new ScrollPane(transactionList);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("view-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        rightPane.getChildren().addAll(budgetCard, totalRow, scroll);

        getChildren().addAll(leftPane, rightPane);
    }

    private void addExpense() {
        String desc = descField.getText().trim();
        String amtStr = amountField.getText().trim();
        if (desc.isEmpty() || amtStr.isEmpty())
            return;

        double amt;
        try {
            amt = Double.parseDouble(amtStr);
        } catch (NumberFormatException e) {
            app.showToast("Invalid amount!", false);
            return;
        }

        String date = datePicker.getValue() != null ? datePicker.getValue().toString() : LocalDate.now().toString();
        String cat = categoryField.getText().trim();
        if (cat.isEmpty())
            cat = "General";

        DatabaseManager.addExpense(new Expense(desc, amt, date, cat));
        descField.clear();
        amountField.clear();
        categoryField.clear();
        app.showToast("Expense added!", true);
        refresh();
    }

    public void refresh() {
        transactionList.getChildren().clear();
        List<Expense> expenses = DatabaseManager.getAllExpenses();
        double total = DatabaseManager.getTotalExpenses();

        totalLabel.setText(String.format("$%.2f", total));

        double ratio = monthlyBudget > 0 ? total / monthlyBudget : 0;
        budgetBar.setProgress(Math.min(ratio, 1.0));
        budgetLabel.setText(String.format("$%.0f / $%.0f (%.0f%%)",
                total, monthlyBudget, ratio * 100));

        // Color budget bar
        String barColor;
        if (ratio < 0.5)
            barColor = "#22c55e";
        else if (ratio < 0.8)
            barColor = "#f59e0b";
        else
            barColor = "#ef4444";
        budgetBar.setStyle("-fx-accent:" + barColor + ";");

        for (Expense e : expenses) {
            transactionList.getChildren().add(createExpenseCard(e));
        }

        if (expenses.isEmpty()) {
            Label empty = new Label("No expenses recorded yet.");
            empty.setStyle("-fx-text-fill:#64748b; -fx-font-size:14; -fx-padding:20;");
            transactionList.getChildren().add(empty);
        }
    }

    private HBox createExpenseCard(Expense e) {
        // Category color from hash
        String color = categoryColor(e.getCategory());

        Region catDot = new Region();
        catDot.setMinSize(8, 8);
        catDot.setMaxSize(8, 8);
        catDot.setStyle("-fx-background-color:" + color + "; -fx-background-radius:4;");

        Label catLabel = new Label(e.getCategory());
        catLabel.setStyle("-fx-text-fill:" + color + "; -fx-font-size:11;");

        Label descLabel = new Label(e.getDescription());
        descLabel.setStyle("-fx-text-fill:#f1f5f9; -fx-font-weight:bold; -fx-font-size:14;");
        descLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(descLabel, Priority.ALWAYS);

        Label dateLabel = new Label(e.getDate());
        dateLabel.setStyle("-fx-text-fill:#64748b; -fx-font-size:11;");

        Label amtLabel = new Label(String.format("-$%.2f", e.getAmount()));
        amtLabel.setStyle("-fx-text-fill:#ef4444; -fx-font-weight:bold; -fx-font-size:14;");

        Button delBtn = new Button("\u2715");
        delBtn.getStyleClass().add("icon-button-danger");
        delBtn.setOnAction(ev -> {
            DatabaseManager.deleteExpense(e.getId());
            app.showToast("Expense removed", false);
            refresh();
        });

        VBox left = new VBox(2, new HBox(6, catDot, catLabel), descLabel);
        HBox.setHgrow(left, Priority.ALWAYS);
        VBox right = new VBox(2, amtLabel, dateLabel);
        right.setAlignment(Pos.CENTER_RIGHT);

        HBox card = new HBox(10, left, right, delBtn);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("task-card");
        card.setPadding(new Insets(12, 16, 12, 16));
        return card;
    }

    private String categoryColor(String cat) {
        if (cat == null || cat.isBlank())
            return "#6366f1";
        int hash = Math.abs(cat.hashCode());
        String[] colors = { "#6366f1", "#ec4899", "#14b8a6", "#f59e0b",
                "#8b5cf6", "#ef4444", "#06b6d4", "#84cc16" };
        return colors[hash % colors.length];
    }
}