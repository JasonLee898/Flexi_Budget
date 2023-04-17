package com.example.myapplication;

public class DataIncome {

    private String item, date, id;
    private double amount;

    public DataIncome() { }

    public DataIncome(String item, String date, String id, Double amount) {
        this.item = item;
        this.date = date;
        this.id = id;
        this.amount = amount;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
