package com.truman.demo.dbcache;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "MainActivity" +  TAG_SUFFIX;

    private final DatabaseCacheTest mDatabaseCacheTest = new DatabaseCacheTest(this);

    private Button mBtnRun;
    private Button mBtnReset;
    private TextView mTvStatus;

    private final Handler mUIHandler = new UIHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate()");

        mBtnRun = findViewById(R.id.btn_run);
        mBtnRun.setOnClickListener(view -> run());
        mBtnReset = findViewById(R.id.btn_reset);
        mBtnReset.setOnClickListener(view -> reset());
        mTvStatus = findViewById(R.id.tv_status);
        mTvStatus.setMovementMethod(new ScrollingMovementMethod());
    }

    private void run() {
        Log.d(TAG, "run() - Inside");

        appendTv("Start test!\n");
        mDatabaseCacheTest.test();
        appendTv("Test finished...\n");
    }

    private void reset() {
        Log.d(TAG, "reset() - Inside");

        updateTv("Reset!\n");
        mDatabaseCacheTest.reset();
    }

    private void updateTv(String text) {
        mUIHandler.sendMessage(
                mUIHandler.obtainMessage(UIHandler.MSG_UPDATE_TV, text));
    }

    private void appendTv(String text) {
        mUIHandler.sendMessage(
                mUIHandler.obtainMessage(UIHandler.MSG_APPEND_TV, text));
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
        private final String TAG = "UIHandler" + TAG_SUFFIX;
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