package com.truman.demo.imagedownloader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 * Version : 1.0.0
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "MainActivity" + TAG_SUFFIX;
    private static final String MY_PROFILE_URL =
            "https://avatars3.githubusercontent.com/u/18167482?s=400&v=4";

    private ImageView mIvFrame;
    private EditText mEtUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate()");

        mIvFrame = findViewById(R.id.iv_frame);
        mEtUrl = findViewById(R.id.et_url);
        mEtUrl.setText(MY_PROFILE_URL);
        findViewById(R.id.btn_download).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageDownloader.getInstance().download(
                                mEtUrl.getText().toString(), mIvFrame);
                    }
                });
        findViewById(R.id.btn_reset).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageDownloader.getInstance().requsetPurge();
                        mIvFrame.setImageResource(R.mipmap.ic_launcher);
                        mEtUrl.setText(MY_PROFILE_URL);
                    }
                });
        ImageDownloader.getInstance().setLogger(new Logger() {
            @Override
            public void d(String tag, final String msg) {
                Log.d(tag, msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void e(String tag, final String msg) {
                Log.e(tag, msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
