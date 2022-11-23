package com.crewmeister.cmcodingchallenge.currency.dto;

public class ConversionResult {
    private String convertedAmount;

    public String getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(String convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public ConversionResult(String convertedAmount) {
        this.convertedAmount = convertedAmount;
    }
}
