package com.truman.demo.userchecker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 * Version : 1.0.0
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity" +  AppMain.getSuffix();

    private Button mBtnRun;
    private Button mBtnReset;
    private TextView mTvStatus;
    private EditText mEtUserId;

    private Handler mUIHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate() - TID : " +  android.os.Process.myTid());

        mUIHandler = new UIHandler(this);

        mBtnRun = findViewById(R.id.btn_start);
        mBtnRun.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
        mBtnReset = findViewById(R.id.btn_stop);
        mBtnReset.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop();
            }
        });
        mTvStatus = findViewById(R.id.tv_status);
        mTvStatus.setMovementMethod(new ScrollingMovementMethod());
        mEtUserId = findViewById(R.id.et_userid);
        mEtUserId.setText(Integer.toString(UserChecker.getMyUserId()));

        updateTv("Hello!\n");
    }

    private void updateTv(String text) {
        mUIHandler.sendMessage(
                mUIHandler.obtainMessage(UIHandler.MSG_UPDATE_TV, text));
    }

    private void appendTv(String text) {
        mUIHandler.sendMessage(
                mUIHandler.obtainMessage(UIHandler.MSG_APPEND_TV, text));
    }

    private void start() {
        if (AppMain.isTaskRunning()) {
            appendTv("Checker service is already running!\n");
            return;
        }

        int userId = 0; // Default UserId
        try {
            userId = Integer.parseInt(mEtUserId.getText().toString());
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse user id");
        }
        appendTv("Parsed user id is " + userId + "\n");
        appendTv("Starting checker service...\n");
        appendTv("Check notification bar now!\n");

        Intent serviceIntent = new Intent(this, ForeNotifier.class);
        serviceIntent.putExtra(UserChecker.EXTRA_USER_ID, userId);
        startForegroundService(serviceIntent);
    }

    private void stop() {
        appendTv("Stopping checker service...\n");

        Intent serviceIntent = new Intent(this, ForeNotifier.class);
        stopService(serviceIntent);
    }

    private void handleUpdateTv(String text) {
        if (text == null)
            return;
        mTvStatus.setText(text);
    }

    private void handleAppendTv(String text) {
        if (text == null)
            return;
        mTvStatus.append(text);
    }

    private static class UIHandler extends Handler {
        private final String TAG = "UIHandler" + AppMain.getSuffix();
        private final WeakReference<MainActivity> mActivity;

        private static final int MSG_UPDATE_TV = 1;
        private static final int MSG_APPEND_TV = 2;

        UIHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            MainActivity activity = mActivity.get();
            if (activity == null) {
                Log.e(TAG, "MainActivity is not available");
                return;
            }

            switch (msg.what) {
                case MSG_UPDATE_TV: {
                    String text = msg.obj == null ?
                            "null" : msg.obj.toString();
                    activity.handleUpdateTv(text);
                    break;
                }
                case MSG_APPEND_TV: {
                    String text = msg.obj == null ?
                            "null" : msg.obj.toString();
                    activity.handleAppendTv(text);
                    break;
                }
                default:
                    Log.e(TAG, "Invalid message");
                    break;
            }
        }
    }
}
