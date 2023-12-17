package com.example.cryptoproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 6000; // Time in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        TextView textViewAppName = findViewById(R.id.textViewAppName);

        // Optional: You can apply custom fonts if needed
        // Typeface typeface = Typeface.createFromAsset(getAssets(), "your_custom_font.ttf");
        // textViewAppName.setTypeface(typeface);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);

                // Close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}

