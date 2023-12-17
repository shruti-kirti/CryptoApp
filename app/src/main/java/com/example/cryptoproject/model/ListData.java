package com.example.cryptoproject.model;
import com.google.gson.annotations.SerializedName;

public class ListData {
    private String symbol;
    private String name;
    private String name_full;
    private String max_supply;
    private String icon_url;
    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getName_full() {
        return name_full;
    }

    public String getMax_supply() {
        return max_supply;
    }

    public String getIcon_url() {
        return icon_url;
    }

    // Additional getters and setters as needed...

    public void setName_full(String name_full) {
        this.name_full = name_full;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }


}
