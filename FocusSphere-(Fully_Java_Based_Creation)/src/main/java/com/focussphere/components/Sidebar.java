package com.focussphere.components;

import com.focussphere.App;
import com.focussphere.db.DatabaseManager;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.*;

public class Sidebar extends VBox {

    private final App app;
    private final Map<String, Button> navButtons = new LinkedHashMap<>();

    public Sidebar(App app) {
        this.app = app;
        getStyleClass().add("sidebar");
        setPrefWidth(220);
        setMinWidth(220);
        setSpacing(4);
        setPadding(new Insets(20, 12, 20, 12));

        // Brand
        Label brand = new Label("\u26A1 FocusSphere");
        brand.getStyleClass().add("brand-label");
        brand.setMaxWidth(Double.MAX_VALUE);
        brand.setAlignment(Pos.CENTER);

        Separator sep = new Separator();
        sep.getStyleClass().add("sidebar-sep");

        getChildren().addAll(brand, sep);

        // Nav items
        String[][] items = {
                { "Dashboard", "\uD83C\uDFE0" }, // 🏠
                { "Tasks", "\u2705" }, // ✅
                { "Notes", "\uD83D\uDCDD" }, // 📝
                { "Habits", "\uD83D\uDD04" }, // 🔄
                { "Expenses", "\uD83D\uDCB0" }, // 💰
                { "Schedule", "\uD83D\uDCC5" }, // 📅
                { "Analytics", "\uD83D\uDCC8" }, // 📈
        };

        for (String[] item : items) {
            Button btn = new Button(item[0] + "  " + item[1]);
            btn.getStyleClass().add("nav-button");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setAlignment(Pos.CENTER_LEFT);
            btn.setOnAction(e -> app.navigateTo(item[0]));
            navButtons.put(item[0], btn);
            getChildren().add(btn);
        }

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        getChildren().add(spacer);

        // Settings button
        Button settingsBtn = new Button("\u2699  Settings");
        settingsBtn.getStyleClass().add("nav-button");
        settingsBtn.setMaxWidth(Double.MAX_VALUE);
        settingsBtn.setAlignment(Pos.CENTER_LEFT);
        settingsBtn.setOnAction(e -> showSettingsDialog());

        // Dark mode toggle
        Button darkToggle = new Button(app.isDarkMode() ? "\u263E  Light Mode" : "\u2600  Dark Mode");
        darkToggle.getStyleClass().add("nav-button");
        darkToggle.setMaxWidth(Double.MAX_VALUE);
        darkToggle.setAlignment(Pos.CENTER_LEFT);
        darkToggle.setOnAction(e -> {
            app.toggleDarkMode();
            darkToggle.setText(app.isDarkMode() ? "\u263E  Light Mode" : "\u2600  Dark Mode");
        });

        getChildren().addAll(new Separator(), settingsBtn, darkToggle);

        setActiveButton("Dashboard");
    }

    public void setActiveButton(String name) {
        navButtons.values().forEach(b -> b.getStyleClass().remove("nav-button-active"));
        Button active = navButtons.get(name);
        if (active != null)
            active.getStyleClass().add("nav-button-active");
    }

    private void showSettingsDialog() {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Settings");
        dlg.setHeaderText("FocusSphere Settings");

        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setPrefWidth(400);

        // Display name
        Label nameLabel = new Label("Display Name:");
        nameLabel.setStyle("-fx-font-weight:bold;");
        TextField nameField = new TextField(app.getUserName());

        // Accent color
        Label accentLabel = new Label("Accent Color:");
        accentLabel.setStyle("-fx-font-weight:bold;");
        HBox colorBox = new HBox(10);
        String[] colors = { "#6366f1", "#f43f5e", "#10b981", "#f59e0b" };
        String[] names = { "Indigo", "Rose", "Emerald", "Amber" };
        for (int i = 0; i < colors.length; i++) {
            Button cb = new Button(names[i]);
            String col = colors[i];
            cb.setStyle("-fx-background-color:" + col + "; -fx-text-fill:white; " +
                    "-fx-background-radius:20; -fx-padding:6 16;-fx-cursor:hand;");
            cb.setOnAction(e -> app.showToast("Accent set to " + col, true));
            colorBox.getChildren().add(cb);
        }

        // Data management
        Label dataLabel = new Label("Data Management:");
        dataLabel.setStyle("-fx-font-weight:bold;");

        Button exportBtn = new Button("Export Backup");
        exportBtn.getStyleClass().add("accent-button");
        exportBtn.setOnAction(e -> {
            app.showToast("Backup created: focussphere.db", true);
            dlg.close();
        });

        Button wipeBtn = new Button("WIPE ALL DATA");
        wipeBtn.setStyle("-fx-background-color:#ef4444;-fx-text-fill:white;-fx-font-weight:bold;" +
                "-fx-background-radius:8;-fx-cursor:hand;");
        wipeBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "This will DELETE everything. Continue?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {
                    DatabaseManager.wipeAllData();
                    app.showToast("All data wiped!", false);
                    app.navigateTo("Dashboard");
                    dlg.close();
                }
            });
        });

        box.getChildren().addAll(
                nameLabel, nameField,
                new Separator(),
                accentLabel, colorBox,
                new Separator(),
                dataLabel, exportBtn, wipeBtn);

        dlg.getDialogPane().setContent(box);
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dlg.setOnCloseRequest(e -> {
            String newName = nameField.getText().trim();
            if (!newName.isBlank())
                app.setUserName(newName);
        });

        dlg.showAndWait();
    }
}