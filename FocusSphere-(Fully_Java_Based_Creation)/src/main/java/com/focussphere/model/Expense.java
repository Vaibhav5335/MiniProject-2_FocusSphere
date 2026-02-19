package com.focussphere.model;

public class Expense {
    private int id;
    private String description;
    private double amount;
    private String date;
    private String category;

    public Expense() {}

    public Expense(String description, double amount, String date, String category) {
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }

    public double getAmount() { return amount; }
    public void setAmount(double a) { this.amount = a; }

    public String getDate() { return date; }
    public void setDate(String d) { this.date = d; }

    public String getCategory() { return category; }
    public void setCategory(String c) { this.category = c; }
}