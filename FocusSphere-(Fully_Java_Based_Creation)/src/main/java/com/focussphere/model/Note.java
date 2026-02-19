package com.focussphere.model;

public class Note {
    private int id;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;

    public Note() {}

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String t) { this.title = t; }

    public String getContent() { return content; }
    public void setContent(String c) { this.content = c; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String c) { this.createdAt = c; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String u) { this.updatedAt = u; }

    public int getWordCount() {
        if (content == null || content.isBlank()) return 0;
        return content.trim().split("\\s+").length;
    }
}