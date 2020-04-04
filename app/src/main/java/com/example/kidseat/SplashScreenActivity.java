package com.example.kidseat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

// Splash Screen is a welcome screen with the app logo

public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));

        // close splash activity
        finish();

    }
}
