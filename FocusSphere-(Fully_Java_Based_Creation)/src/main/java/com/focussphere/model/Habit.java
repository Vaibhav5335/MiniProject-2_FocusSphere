package com.focussphere.model;

import java.util.*;

public class Habit {
    private int id;
    private String name;
    private String completedDays; // comma-separated dates YYYY-MM-DD
    private String createdAt;

    public Habit() {
        this.completedDays = "";
    }

    public Habit(String name) {
        this.name = name;
        this.completedDays = "";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String n) { this.name = n; }

    public String getCompletedDays() { return completedDays; }
    public void setCompletedDays(String c) { this.completedDays = c; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String c) { this.createdAt = c; }

    public Set<String> getCompletedDaysSet() {
        Set<String> set = new HashSet<>();
        if (completedDays != null && !completedDays.isBlank()) {
            for (String d : completedDays.split(",")) {
                set.add(d.trim());
            }
        }
        return set;
    }

    public void toggleDay(String dateStr) {
        Set<String> days = getCompletedDaysSet();
        if (days.contains(dateStr)) {
            days.remove(dateStr);
        } else {
            days.add(dateStr);
        }
        this.completedDays = String.join(",", days);
    }

    public int getCurrentStreak() {
        Set<String> days = getCompletedDaysSet();
        int streak = 0;
        java.time.LocalDate d = java.time.LocalDate.now();
        while (days.contains(d.toString())) {
            streak++;
            d = d.minusDays(1);
        }
        return streak;
    }
}