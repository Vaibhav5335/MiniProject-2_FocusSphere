package com.focussphere.views;

import com.focussphere.App;
import com.focussphere.db.DatabaseManager;
import com.focussphere.model.Note;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotesView extends HBox {

    private final App app;
    private VBox noteListBox;
    private TextField searchField;
    private TextField titleField;
    private TextArea contentArea;
    private Label wordCountLabel, saveStatusLabel;
    private VBox editorPane;
    private VBox emptyState;
    private Note currentNote;
    private Timer debounceTimer;

    public NotesView(App app) {
        this.app = app;
        setSpacing(0);
        getStyleClass().add("view-container");
        buildUI();
    }

    private void buildUI() {
        // === LEFT PANE: Document list ===
        VBox leftPane = new VBox(10);
        leftPane.getStyleClass().add("notes-sidebar");
        leftPane.setPadding(new Insets(16));
        leftPane.setPrefWidth(280);
        leftPane.setMinWidth(250);

        Button newBtn = new Button("+ New Document");
        newBtn.getStyleClass().add("accent-button");
        newBtn.setMaxWidth(Double.MAX_VALUE);
        newBtn.setOnAction(e -> createNewNote());

        searchField = new TextField();
        searchField.setPromptText("\uD83D\uDD0D Search notes...");
        searchField.getStyleClass().add("search-field");
        searchField.textProperty().addListener((obs, o, n) -> refreshList());

        noteListBox = new VBox(6);
        ScrollPane listScroll = new ScrollPane(noteListBox);
        listScroll.setFitToWidth(true);
        listScroll.getStyleClass().add("view-scroll");
        VBox.setVgrow(listScroll, Priority.ALWAYS);

        leftPane.getChildren().addAll(newBtn, searchField, listScroll);

        // === RIGHT PANE: Editor ===
        VBox rightPane = new VBox();
        rightPane.setPadding(new Insets(16));
        HBox.setHgrow(rightPane, Priority.ALWAYS);

        // Toolbar
        HBox toolbar = new HBox(6);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(0, 0, 10, 0));

        String[] fmtLabels = { "B", "I", "H", "List", "Code" };
        String[] fmtInsert = { "**bold**", "*italic*", "# Heading", "- item", "```code```" };
        for (int i = 0; i < fmtLabels.length; i++) {
            Button b = new Button(fmtLabels[i]);
            b.getStyleClass().add("fmt-button");
            int idx = i;
            b.setOnAction(e -> {
                if (contentArea != null) {
                    contentArea.appendText("\n" + fmtInsert[idx]);
                }
            });
            toolbar.getChildren().add(b);
        }

        // Title + Content
        titleField = new TextField();
        titleField.setPromptText("Note title...");
        titleField.getStyleClass().add("note-title-input");
        titleField.textProperty().addListener((obs, o, n) -> scheduleAutoSave());

        contentArea = new TextArea();
        contentArea.setPromptText("Start writing...");
        contentArea.setWrapText(true);
        contentArea.getStyleClass().add("note-content");
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        contentArea.textProperty().addListener((obs, o, n) -> {
            scheduleAutoSave();
            if (currentNote != null) {
                wordCountLabel.setText(currentNote.getWordCount() + " words — but let me recalc...");
                int wc = n == null || n.isBlank() ? 0 : n.trim().split("\\s+").length;
                wordCountLabel.setText(wc + " words");
            }
        });

        // Footer
        wordCountLabel = new Label("0 words");
        wordCountLabel.setStyle("-fx-text-fill:#64748b; -fx-font-size:11;");
        saveStatusLabel = new Label("");
        saveStatusLabel.setStyle("-fx-text-fill:#22c55e; -fx-font-size:11;");

        Button deleteNoteBtn = new Button("\uD83D\uDDD1  Delete");
        deleteNoteBtn.getStyleClass().add("danger-button-small");
        deleteNoteBtn.setOnAction(e -> {
            if (currentNote != null) {
                DatabaseManager.deleteNote(currentNote.getId());
                currentNote = null;
                showEmptyState();
                refreshList();
                app.showToast("Note deleted", false);
            }
        });

        HBox footer = new HBox(10, saveStatusLabel, wordCountLabel,
                new Region() {
                    {
                        HBox.setHgrow(this, Priority.ALWAYS);
                    }
                }, deleteNoteBtn);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(10, 0, 0, 0));

        editorPane = new VBox(8, toolbar, titleField, contentArea, footer);
        VBox.setVgrow(editorPane, Priority.ALWAYS);

        // Empty state
        emptyState = new VBox(10);
        emptyState.setAlignment(Pos.CENTER);
        VBox.setVgrow(emptyState, Priority.ALWAYS);
        Label emptyIcon = new Label("\uD83D\uDCDD");
        emptyIcon.setStyle("-fx-font-size:48;");
        Label emptyText = new Label("Select a note or create a new one");
        emptyText.setStyle("-fx-text-fill:#64748b; -fx-font-size:15;");
        emptyState.getChildren().addAll(emptyIcon, emptyText);

        rightPane.getChildren().add(emptyState);

        // Separator
        Separator sep = new Separator();
        sep.setOrientation(javafx.geometry.Orientation.VERTICAL);

        getChildren().addAll(leftPane, sep, rightPane);

        // Store right pane reference for swapping
        this.rightPaneRef = rightPane;
    }

    private VBox rightPaneRef;

    private void showEditor() {
        rightPaneRef.getChildren().clear();
        rightPaneRef.getChildren().add(editorPane);
        VBox.setVgrow(editorPane, Priority.ALWAYS);
    }

    private void showEmptyState() {
        rightPaneRef.getChildren().clear();
        rightPaneRef.getChildren().add(emptyState);
        VBox.setVgrow(emptyState, Priority.ALWAYS);
    }

    private void createNewNote() {
        Note n = new Note("Untitled", "");
        int id = DatabaseManager.addNote(n);
        n.setId(id);
        selectNote(n);
        refreshList();
        titleField.requestFocus();
        titleField.selectAll();
    }

    private void selectNote(Note n) {
        currentNote = n;
        showEditor();
        titleField.setText(n.getTitle());
        contentArea.setText(n.getContent() != null ? n.getContent() : "");
        int wc = n.getContent() == null || n.getContent().isBlank() ? 0
                : n.getContent().trim().split("\\s+").length;
        wordCountLabel.setText(wc + " words");
        saveStatusLabel.setText("");
    }

    private void scheduleAutoSave() {
        if (currentNote == null)
            return;
        saveStatusLabel.setText("Saving...");
        if (debounceTimer != null)
            debounceTimer.cancel();
        debounceTimer = new Timer();
        debounceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> {
                    currentNote.setTitle(titleField.getText());
                    currentNote.setContent(contentArea.getText());
                    DatabaseManager.updateNote(currentNote);
                    saveStatusLabel.setText("Saved \u2713");
                    refreshList();
                });
            }
        }, 600);
    }

    private void refreshList() {
        noteListBox.getChildren().clear();
        List<Note> notes = DatabaseManager.getAllNotes();
        String query = searchField.getText().toLowerCase().trim();

        for (Note n : notes) {
            if (!query.isEmpty()) {
                boolean match = n.getTitle().toLowerCase().contains(query) ||
                        (n.getContent() != null && n.getContent().toLowerCase().contains(query));
                if (!match)
                    continue;
            }

            VBox item = new VBox(2);
            item.getStyleClass().add("note-list-item");
            item.setPadding(new Insets(10, 12, 10, 12));
            item.setCursor(javafx.scene.Cursor.HAND);

            Label t = new Label(n.getTitle());
            t.setStyle("-fx-text-fill:#f1f5f9; -fx-font-weight:bold; -fx-font-size:13;");

            String preview = n.getContent() != null
                    ? n.getContent().substring(0, Math.min(n.getContent().length(), 60))
                    : "";
            Label p = new Label(preview);
            p.setStyle("-fx-text-fill:#64748b; -fx-font-size:11;");

            item.getChildren().addAll(t, p);

            if (currentNote != null && currentNote.getId() == n.getId()) {
                item.getStyleClass().add("note-list-item-active");
            }

            item.setOnMouseClicked(e -> selectNote(n));
            noteListBox.getChildren().add(item);
        }
    }

    public void refresh() {
        refreshList();
        if (currentNote == null)
            showEmptyState();
    }
}