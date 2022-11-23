package com.crewmeister.cmcodingchallenge.currency.dto;

public class DatabaseMessage {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DatabaseMessage(String message) {
        this.message = message;
    }
}
