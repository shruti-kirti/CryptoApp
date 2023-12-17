package com.example.cryptoproject;
// Currency.java
// Currency.java

public class Currency {
    private String fullName;
    private String iconUrl;
    private double exchangeRate;

    // Constructor
    public Currency(String fullName, String iconUrl, double exchangeRate) {
        this.fullName = fullName;
        this.iconUrl = iconUrl;
        this.exchangeRate = exchangeRate;
    }

    // Getter methods
    public String getFullName() {
        return fullName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }
}

