package com.focussphere.model;

public class Task {
    private int id;
    private String title;
    private String description;
    private String dueDate;   // ISO format YYYY-MM-DD
    private String priority;  // Low, Medium, High
    private boolean completed;
    private String tags;      // comma-separated
    private String recurring; // null, Daily, Weekly, Monthly
    private String createdAt;

    public Task() {
        this.priority = "Medium";
        this.completed = false;
    }

    public Task(String title, String dueDate, String priority) {
        this.title = title;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = false;
    }

    // --- Getters & Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getRecurring() { return recurring; }
    public void setRecurring(String recurring) { this.recurring = recurring; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}