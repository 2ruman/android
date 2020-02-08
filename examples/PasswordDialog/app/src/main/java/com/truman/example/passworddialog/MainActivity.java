package com.truman.example.passworddialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 * Version : 1.0.0
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "MainActivity" + TAG_SUFFIX;

    private Button mBtnRun;
    private Button mBtnReset;
    private TextView mTvStatus;

    private final Handler mUIHandler = new UIHandler(this);

    private static final String SAVED_PASSWORD = "abcd1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate()");

        mBtnRun = findViewById(R.id.btn_run);
        mBtnRun.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                run();
            }
        });
        mBtnReset = findViewById(R.id.btn_reset);
        mBtnReset.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });
        mTvStatus = findViewById(R.id.tv_status);
        mTvStatus.setMovementMethod(new ScrollingMovementMethod());

        eveDivulgesPassword();
    }

    private void eveDivulgesPassword() {
        updateTv("Eve : I've got a password of Alice...\n");
        appendTv("Eve : " + SAVED_PASSWORD + "...\n");
    }

    private void confirmPassword(String password) {
        if (SAVED_PASSWORD.equals(password)) {
            appendTv("You entered correct password.\n");
            Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_LONG).show();
        } else {
            appendTv("you entered wrong password.!\n");
            Toast.makeText(getApplicationContext(), "Access denied!", Toast.LENGTH_LONG).show();
        }
    }

    private void showPasswordDialog() {
        final EditText etPassword = new EditText(this);
        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etPassword.requestFocus();

        new AlertDialog.Builder(this)
                .setView(etPassword)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Authentication")
                .setMessage("Enter your password")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmPassword(etPassword.getText().toString());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                /*
                 * This is for checking how it will go when on each events.
                 *  1) dialog.dismiss() --> onDismiss()
                 *  2) dialog.cancel()  --> onCancel() --> onDismiss()
                 *
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Log.d(TAG, "Dismissed");
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Log.d(TAG, "Canceled");
                    }
                })
                */
                .show();
    }

    private void run() {
        Log.d(TAG, "run() - Inside");
        showPasswordDialog();
    }

    private void reset() {
        Log.d(TAG, "reset() - Inside");
        updateTv("");
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