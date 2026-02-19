package com.focussphere;

import com.focussphere.components.*;
import com.focussphere.db.DatabaseManager;
import com.focussphere.views.*;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {

    private static App instance;
    private Stage primaryStage;
    private BorderPane root;
    private StackPane contentArea;
    private StackPane rootStack;

    private Sidebar sidebar;
    private HeaderBar headerBar;
    private PomodoroWidget pomodoroWidget;

    private DashboardView dashboardView;
    private TasksView tasksView;
    private NotesView notesView;
    private HabitsView habitsView;
    private ExpensesView expensesView;
    private ScheduleView scheduleView;
    private AnalyticsView analyticsView;

    private boolean darkMode = true;
    private String currentView = "Dashboard";
    private String userName = "User";

    @Override
    public void start(Stage stage) {
        instance = this;
        primaryStage = stage;

        DatabaseManager.initialize();
        loadSettings();

        // Build views
        dashboardView  = new DashboardView(this);
        tasksView      = new TasksView(this);
        notesView      = new NotesView(this);
        habitsView     = new HabitsView(this);
        expensesView   = new ExpensesView(this);
        scheduleView   = new ScheduleView(this);
        analyticsView  = new AnalyticsView(this);

        // Layout
        sidebar    = new Sidebar(this);
        headerBar  = new HeaderBar(this);
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");

        root = new BorderPane();
        root.getStyleClass().add("root-pane");
        root.setLeft(sidebar);
        root.setTop(headerBar);
        root.setCenter(contentArea);

        rootStack = new StackPane(root);
        rootStack.getStyleClass().add("dark-theme");

        // Pomodoro floating widget
        pomodoroWidget = new PomodoroWidget();
        pomodoroWidget.setVisible(false);
        StackPane.setAlignment(pomodoroWidget, Pos.TOP_RIGHT);
        StackPane.setMargin(pomodoroWidget, new Insets(70, 20, 0, 0));
        rootStack.getChildren().add(pomodoroWidget);

        navigateTo("Dashboard");

        Scene scene = new Scene(rootStack, 1300, 820);
        String css = getClass().getResource("/styles.css") != null
                ? getClass().getResource("/styles.css").toExternalForm()
                : null;
        if (css != null) scene.getStylesheets().add(css);

        // Keyboard shortcuts
        scene.setOnKeyPressed(e -> {
            if (e.isControlDown() || e.isMetaDown()) {
                switch (e.getCode()) {
                    case DIGIT1 -> navigateTo("Dashboard");
                    case DIGIT2 -> navigateTo("Tasks");
                    case DIGIT3 -> navigateTo("Notes");
                    case DIGIT4 -> navigateTo("Habits");
                    case DIGIT5 -> navigateTo("Expenses");
                    case DIGIT6 -> navigateTo("Schedule");
                    case DIGIT7 -> navigateTo("Analytics");
                    case N      -> { navigateTo("Tasks"); tasksView.focusInput(); }
                    case K      -> showCommandPalette();
                    default     -> {}
                }
            }
            if (e.getCode() == KeyCode.ESCAPE) pomodoroWidget.setVisible(false);
        });

        stage.setTitle("FocusSphere — Productivity Dashboard");
        stage.setScene(scene);
        stage.setMinWidth(950);
        stage.setMinHeight(620);
        stage.show();

        showLoadingScreen();
    }

    public void navigateTo(String view) {
        currentView = view;
        contentArea.getChildren().clear();
        switch (view) {
            case "Dashboard"  -> { dashboardView.refresh();  contentArea.getChildren().add(dashboardView); }
            case "Tasks"      -> { tasksView.refresh();      contentArea.getChildren().add(tasksView); }
            case "Notes"      -> { notesView.refresh();      contentArea.getChildren().add(notesView); }
            case "Habits"     -> { habitsView.refresh();     contentArea.getChildren().add(habitsView); }
            case "Expenses"   -> { expensesView.refresh();   contentArea.getChildren().add(expensesView); }
            case "Schedule"   -> { scheduleView.refresh();   contentArea.getChildren().add(scheduleView); }
            case "Analytics"  -> { analyticsView.refresh();  contentArea.getChildren().add(analyticsView); }
        }
        sidebar.setActiveButton(view);
        headerBar.updateGreeting();
    }

    public void toggleDarkMode() {
        darkMode = !darkMode;
        if (darkMode) {
            rootStack.getStyleClass().remove("light-theme");
            rootStack.getStyleClass().add("dark-theme");
        } else {
            rootStack.getStyleClass().remove("dark-theme");
            rootStack.getStyleClass().add("light-theme");
        }
        DatabaseManager.saveSetting("darkMode", String.valueOf(darkMode));
    }

    public void togglePomodoro() {
        pomodoroWidget.setVisible(!pomodoroWidget.isVisible());
    }

    public void startPomodoroWithTask(String title) {
        pomodoroWidget.setTaskName(title);
        pomodoroWidget.setVisible(true);
        pomodoroWidget.startTimer();
    }

    public void showToast(String msg, boolean success) {
        ToastNotification.show(rootStack, msg, success);
    }

    private void showLoadingScreen() {
        VBox loading = new VBox(15);
        loading.setAlignment(Pos.CENTER);
        loading.setStyle("-fx-background-color: #0f172a;");

        Label logo = new Label("\u26A1 FocusSphere");
        logo.setStyle("-fx-font-size:36; -fx-font-weight:bold; -fx-text-fill:white;");

        ProgressIndicator spin = new ProgressIndicator();
        spin.setMaxSize(50, 50);

        Label txt = new Label("Loading your workspace...");
        txt.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:14;");

        loading.getChildren().addAll(logo, spin, txt);
        rootStack.getChildren().add(loading);

        PauseTransition p = new PauseTransition(Duration.seconds(1.5));
        p.setOnFinished(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(500), loading);
            ft.setToValue(0);
            ft.setOnFinished(ev -> rootStack.getChildren().remove(loading));
            ft.play();
        });
        p.play();
    }

    private void showCommandPalette() {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Quick Find");
        dlg.setHeaderText("Search tasks and notes (Ctrl+K)");
        dlg.setContentText("Search:");
        dlg.showAndWait().ifPresent(query -> {
            if (query.isBlank()) return;
            String q = query.toLowerCase();
            for (var t : DatabaseManager.getAllTasks()) {
                if (t.getTitle().toLowerCase().contains(q)) {
                    navigateTo("Tasks");
                    return;
                }
            }
            for (var n : DatabaseManager.getAllNotes()) {
                if (n.getTitle().toLowerCase().contains(q) ||
                    (n.getContent() != null && n.getContent().toLowerCase().contains(q))) {
                    navigateTo("Notes");
                    return;
                }
            }
            showToast("No results found.", false);
        });
    }

    private void loadSettings() {
        String n = DatabaseManager.getSetting("userName");
        if (n != null && !n.isBlank()) userName = n;
        String d = DatabaseManager.getSetting("darkMode");
        if (d != null) darkMode = Boolean.parseBoolean(d);
    }

    // Getters / Setters
    public static App getInstance() { return instance; }
    public Stage getPrimaryStage()  { return primaryStage; }
    public boolean isDarkMode()     { return darkMode; }
    public String getUserName()     { return userName; }
    public StackPane getRootStack() { return rootStack; }
    public String getCurrentView()  { return currentView; }

    public void setUserName(String name) {
        userName = name;
        DatabaseManager.saveSetting("userName", name);
        headerBar.updateGreeting();
    }

    public static void main(String[] args) { launch(args); }
}