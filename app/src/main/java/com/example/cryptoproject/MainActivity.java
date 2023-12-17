package com.example.cryptoproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cryptoproject.model.ListApiResponse;
import com.example.cryptoproject.model.ListData;
import com.example.cryptoproject.model.LiveApiResponse;
import com.google.gson.Gson;

import java.lang.ref.Cleaner;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// MainActivity.java

// Import statements

public class MainActivity extends AppCompatActivity {
    private static final int MAX_RETRIES = 3; // Maximum number of retry attempts
    private int retryCount = 0; // Counter to track retry attempts
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView lastRefreshTextView;
    private Handler autoRefreshHandler;
    private static final int AUTO_REFRESH_INTERVAL = 3 * 60 * 1000;
    private static final String BASE_URL = "http://api.coinlayer.com/";
    private Retrofit retrofit;

    private static final String LIVE_API_URL = "http://api.coinlayer.com/live?access_key=7af63c066d64c5c865dc5dd2b656e71e";
    private static final String LIST_API_URL = "http://api.coinlayer.com/list?access_key=7af63c066d64c5c865dc5dd2b656e71e";

    private List<Currency> currencyList;
    private CurrencyAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        lastRefreshTextView = findViewById(R.id.lastRefreshTextView);

        // Set up swipe-to-refresh listener
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        // Set up auto-refresh handler
        autoRefreshHandler = new Handler();
        autoRefreshHandler.postDelayed(this::autoRefresh, AUTO_REFRESH_INTERVAL);


        ListView currencyListView = findViewById(R.id.currencyList);
        currencyList = new ArrayList<>();
        adapter = new CurrencyAdapter(this, R.layout.list_item, currencyList);
        currencyListView.setAdapter(adapter);

        fetchData();
        fetchDataWithRetry();
    }

    private void fetchDataWithRetry() {
        retryCount = 0;
        fetchLiveData();
    }

    private void fetchLiveData() {
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<LiveApiResponse> liveApiResponseCall = apiInterface.getLiveRates();
        liveApiResponseCall.enqueue(new Callback<LiveApiResponse>() {
            @Override
            public void onResponse(Call<LiveApiResponse> call, Response<LiveApiResponse> response) {
                if (response.isSuccessful()) {
                    // Process the successful response
                    LiveApiResponse liveApiResponse = response.body();
                    if (liveApiResponse != null) {
                        processLiveApiResponse(liveApiResponse);
                    } else {
                        Log.d("Debug", "LiveApiResponse is null");
                    }
                } else {
                    // Handle unsuccessful response
                    Log.d("Debug", "API request failed with code: " + response.code());
                    retryOrHandleFailure(MainActivity.this::fetchLiveData);
                }
            }

            @Override
            public void onFailure(Call<LiveApiResponse> call, Throwable t) {
                // Handle network failure
                t.printStackTrace();
                retryOrHandleFailure(MainActivity.this::fetchLiveData);
            }
        });
    }

    private void processLiveApiResponse(LiveApiResponse liveApiResponse) {
        // Assuming liveApiResponse contains a Map<String, Double> rates
        Map<String, Double> rates = liveApiResponse.getRates();

        if (rates != null && !rates.isEmpty()) {
            // Iterate over the rates and populate the currencyList
            for (Map.Entry<String, Double> entry : rates.entrySet()) {
                String symbol = entry.getKey();
                double exchangeRate = entry.getValue();
                Log.d("Debug", "Symbol: " + symbol + ", Exchange Rate: " + exchangeRate);

                // Fetch missing info from the LIST API
                fetchListDataAndUpdateUI(symbol, exchangeRate);
            }
        } else {
            Log.d("Debug", "Rates is null or empty");
        }
    }

    private void retryOrHandleFailure(Runnable retryAction) {
        if (retryCount < MAX_RETRIES) {
            // Retry the operation
            retryCount++;
            Log.d("Debug", "Retrying... Attempt #" + retryCount);
            retryAction.run();
        } else {
            // Maximum retries reached, handle failure
            Log.d("Debug", "Maximum retries reached. Unable to fetch data.");
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Unable to fetch data. Please try again later.", Toast.LENGTH_SHORT).show();
            });
        }

    }

    private void refreshData() {
        // Called when user performs swipe-to-refresh
        fetchData();
        updateLastRefreshTime();
        swipeRefreshLayout.setRefreshing(false); // Stop the refreshing animation
    }

    private void autoRefresh() {
        // Called for auto-refresh every 3 minutes
        fetchData();
        updateLastRefreshTime();
        autoRefreshHandler.postDelayed(this::autoRefresh, AUTO_REFRESH_INTERVAL);
    }

    private void updateLastRefreshTime() {
        // Update the UI with the current time as the last refresh time
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        lastRefreshTextView.setText("Last Refresh: " + currentTime);
    }


    private void fetchData() {
        Log.d("TAG", "Before retrofit initialization");
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.d("TAG", "After retrofit initialization");

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        // Fetch data from the LIVE API
        Call<LiveApiResponse> liveApiResponseCall = apiInterface.getLiveRates();
        liveApiResponseCall.enqueue(new Callback<LiveApiResponse>() {
            @Override
            public void onResponse(Call<LiveApiResponse> call, Response<LiveApiResponse> response) {
                if (response.isSuccessful()) {
                    LiveApiResponse liveApiResponse = response.body();
                    if (liveApiResponse != null) {
                        //     Log.d("Debug", "List API Response: " + new Gson().toJson(listApiResponse)); // Log entire response
                        Log.d("Debug", "Live API Response: " + new Gson().toJson(liveApiResponse)); // Log entire respons
                        Map<String, Double> rates = liveApiResponse.getRates();

                        if (rates != null && !rates.isEmpty()) {
                            // Iterate over the rates and populate the currencyList
                            for (Map.Entry<String, Double> entry : rates.entrySet()) {
                                String symbol = entry.getKey();
                                double exchangeRate = entry.getValue();
                                Log.d("Debug", "Symbol: " + symbol + ", Exchange Rate: " + exchangeRate);
                                // Fetch missing info from the LIST API
                                fetchListDataAndUpdateUI(symbol, exchangeRate);
                            }
                        } else {
                            Log.d("Debug", "Rates is null or empty");
                        }
                    } else {
                        Log.d("Debug", "LiveApiResponse is null");
                    }
                } else {
                    Log.d("Debug", "API request failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LiveApiResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void fetchListDataAndUpdateUI(String symbol, double exchangeRate) {
        // Fetch missing info from the LIST API
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<ListApiResponse> listApiResponseCall = apiInterface.getListData();
        listApiResponseCall.enqueue(new Callback<ListApiResponse>() {
            @Override
            public void onResponse(Call<ListApiResponse> call, Response<ListApiResponse> response) {
                if (response.isSuccessful()) {
                    ListApiResponse listApiResponse = response.body();

                    if (listApiResponse != null) {
                        Map<String, ListData> cryptoData = listApiResponse.getCrypto();
                        if (cryptoData != null && !cryptoData.isEmpty()) {
                            ListData listData = cryptoData.get(symbol);
                            if (listData != null) {
                                String fullName = listData.getName_full();
                                String iconUrl = listData.getIcon_url();

                                // Create Currency object and add it to the list
                                Currency currency = new Currency(fullName, iconUrl, exchangeRate);
                                currencyList.add(currency);

                                // Update the UI on the main thread
                                runOnUiThread(() -> adapter.notifyDataSetChanged());
                            } else {
                                Log.d("Debug", "No matching data for symbol: " + symbol);
                            }
                        } else {
                            Log.d("Debug", "CryptoData is null or empty");
                        }
                    } else {
                        Log.d("Debug", "ListApiResponse is null");
                    }
                } else {
                    Log.d("Debug", "API request failed with code: " + response.code());
                   // retryOrHandleFailure(MainActivity.this::fetchListDataAndUpdateUI);
                }
            }


            @Override
            public void onFailure(Call<ListApiResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


}

