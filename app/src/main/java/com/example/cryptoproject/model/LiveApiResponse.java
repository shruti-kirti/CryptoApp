package com.example.cryptoproject.model;
import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class LiveApiResponse {
    @SerializedName("rates")
    private Map<String, Double> rates;

    public Map<String, Double> getRates() {
        return rates;
    }
}
