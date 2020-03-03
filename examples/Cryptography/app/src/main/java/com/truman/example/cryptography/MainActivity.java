package com.truman.example.cryptography;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "MainActivity" +  TAG_SUFFIX;

    private static final int MSG_UPDATE_STATUS = 1;
    private static final int MSG_APPEND_STATUS = 2;

    private Button mBtnGenKey;
    private Button mBtnPBKDF;
    private Button mBtnEncAesCbc;
    private Button mBtnDecAesCbc;
    private Button mBtnEncAesGcm;
    private Button mBtnDecAesGcm;
    private EditText mEtPassword;
    private EditText mEtKey;
    private EditText mEtPlainText;
    private EditText mEtCipherText;
    private TextView mTvResult;

    private final Handler mUIHandler = new UIHandler(this);

    private static final String DEFAULT_PASSWORD = "Default Password";
    private static final byte[] DEFAULT_SALT = "Default Salt 16B".getBytes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate()");

        initUI();
    }

    private void initUI() {
        mBtnPBKDF = findViewById(R.id.btn_pbkdf);
        mBtnPBKDF.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbkdf();
            }
        });
        mEtPassword = findViewById(R.id.et_password);
        mEtPassword.setText(DEFAULT_PASSWORD);
        mEtKey = findViewById(R.id.et_key);
        mTvResult = findViewById(R.id.tv_status);
    }

    private void pbkdf() {
        String password = mEtPassword.getText().toString();
        if (password == null || password.isEmpty()) {
            appendStatus("Failed in PBKDF due to invalid password...");
            return;
        }
        byte[] passwordBytes = password.getBytes();
        byte[] hashedBytes;
        String hashed;
        try {
            hashedBytes = CryptoManager.PBKDF2(passwordBytes, DEFAULT_SALT);
            hashed = CryptoManager.bytesToHex(hashedBytes);
        } catch (RuntimeException e) {
            appendStatus("Failed in PBKDF: " + e.getMessage());
            return;
        }
        mEtKey.setText(hashed);
        CryptoManager.zeroize(passwordBytes);
        CryptoManager.zeroize(hashedBytes);
    }

    private void updateStatus(String text) {
        mUIHandler.sendMessage(
                mUIHandler.obtainMessage(MSG_UPDATE_STATUS, text));
    }

    private void appendStatus(String text) {
        mUIHandler.sendMessage(
                mUIHandler.obtainMessage(MSG_APPEND_STATUS, text));
    }

    private void handleUpdateStatus(String text) {
        if (text == null)
            return;
        mTvResult.setText(text);
    }

    private void handleAppendStatus(String text) {
        if (text == null)
            return;
        mTvResult.append(text);
    }

    private static class UIHandler extends Handler {
        private final String TAG = "UIHandler" + TAG_SUFFIX;
        private final WeakReference<MainActivity> mActivity;

        UIHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity == null) {
                Log.e(TAG, "MainActivity is not available");
                return;
            }

            switch (msg.what) {
                case MSG_UPDATE_STATUS: {
                    String text = msg.obj == null ?
                            "null" : msg.obj.toString();
                    activity.handleUpdateStatus(text);
                    break;
                }
                case MSG_APPEND_STATUS: {
                    String text = msg.obj == null ?
                            "null" : msg.obj.toString();
                    activity.handleAppendStatus(text);
                    break;
                }
                default:
                    Log.e(TAG, "Invalid message");
                    break;
            }
        }
    }
}
