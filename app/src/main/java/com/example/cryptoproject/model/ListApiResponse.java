package com.example.cryptoproject.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class ListApiResponse {
    private boolean success;
    private Map<String, ListData> crypto;
    public Map<String, ListData> getCrypto() {
        return crypto;
    }

    public void setCrypto(Map<String, ListData> crypto) {
        this.crypto = crypto;
    }
}
