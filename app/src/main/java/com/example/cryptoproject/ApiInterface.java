package com.example.cryptoproject;

// ApiInterface.java
import com.example.cryptoproject.model.ListApiResponse;
import com.example.cryptoproject.model.LiveApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    @GET("http://api.coinlayer.com/live?access_key=7af63c066d64c5c865dc5dd2b656e71e")
    Call<LiveApiResponse> getLiveRates();

    @GET("http://api.coinlayer.com/list?access_key=7af63c066d64c5c865dc5dd2b656e71e")
    Call<ListApiResponse> getListData();
}
