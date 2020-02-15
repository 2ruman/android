package com.truman.example.splashscreenbasic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import java.util.concurrent.TimeUnit;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "SplashActivity" + TAG_SUFFIX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View decorView = getWindow().getDecorView();
        int visibility = 0
//              | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(visibility);

        super.onCreate(savedInstanceState);

        new PrimaryTask().execute();
    }

    private class PrimaryTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            return primaryTask();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                moveToMain();
            } else {
                finish();
            }
        }
    }

    private boolean primaryTask() {
        // Do what you need before move to main activity
        if (AppMain.isColdStarting()) {
            Log.d(TAG, "Cold starting...");
            SystemClock.sleep(TimeUnit.SECONDS.toMillis(3));
        } else {
            Log.d(TAG, "Not cold starting!");
        }
        return true;
    }

    private void moveToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
