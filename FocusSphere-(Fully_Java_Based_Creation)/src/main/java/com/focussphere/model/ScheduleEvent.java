package com.focussphere.model;

public class ScheduleEvent {
    private int id;
    private String title;
    private String startTime; // HH:mm
    private String endTime;   // HH:mm
    private String color;     // hex color
    private String date;      // YYYY-MM-DD

    public ScheduleEvent() {}

    public ScheduleEvent(String title, String startTime,
                         String endTime, String color, String date) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.color = color;
        this.date = date;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String t) { this.title = t; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String s) { this.startTime = s; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String e) { this.endTime = e; }

    public String getColor() { return color; }
    public void setColor(String c) { this.color = c; }

    public String getDate() { return date; }
    public void setDate(String d) { this.date = d; }

    /** Duration in minutes */
    public int getDurationMinutes() {
        try {
            String[] s = startTime.split(":");
            String[] e = endTime.split(":");
            int startMin = Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]);
            int endMin   = Integer.parseInt(e[0]) * 60 + Integer.parseInt(e[1]);
            return Math.max(endMin - startMin, 0);
        } catch (Exception ex) { return 60; }
    }

    /** Start minute from midnight */
    public int getStartMinute() {
        try {
            String[] s = startTime.split(":");
            return Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]);
        } catch (Exception ex) { return 0; }
    }
}