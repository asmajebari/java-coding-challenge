package com.crewmeister.cmcodingchallenge.currency.models;

public class CurrencyConversionRates {
    private double conversionRate;

    public CurrencyConversionRates(double conversionRate) {
        this.conversionRate = conversionRate;
    }

    public double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(double conversionRate) {
        this.conversionRate = conversionRate;
    }
}
