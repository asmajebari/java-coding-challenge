package com.crewmeister.cmcodingchallenge.currency.models;

public class CurrencyConversionData {
    private double amount;
    private String date;
    private String currency;

    public CurrencyConversionData(double amount, String date, String currency) {
        this.amount = amount;
        this.date = date;
        this.currency = currency;
    }

    public CurrencyConversionData() {
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
