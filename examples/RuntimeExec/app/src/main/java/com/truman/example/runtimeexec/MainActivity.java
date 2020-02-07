package com.truman.example.runtimeexec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
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
 * Version : 1.0.2
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "MainActivity" +  TAG_SUFFIX;

    private Button mBtnRun;
    private Button mBtnReset;
    private TextView mTvStatus;
    private EditText mEtCommand;

    private final Handler mUIHandler = new UIHandler(this);

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
        mEtCommand = findViewById(R.id.et_cmd);
        mEtCommand.requestFocus();
    }

    private final IShellCommandCallback callback = new IShellCommandCallback.Stub() {
        @Override
        public void onReadLine(String line) throws RemoteException {
            appendTv(line + "\n");
        }

        @Override
        public void onFailure(String errorMessage) throws RemoteException {
            appendTv(errorMessage + "\n");
        }

        @Override
        public void onReadPage(byte[] page, int length) throws RemoteException {
            // Page to String
            String pageStr = new String(page, 0, length);
            appendTv(pageStr);
        }

        @Override
        public void onReadLineAsync(String line) throws RemoteException {
            // Just in case!
        }

        @Override
        public void onFailureAsync(String errorMessage) throws RemoteException {
            // Just in case!
        }

        @Override
        public void onReadPageAsync(byte[] page, int length) throws RemoteException {
            // Just in case!
        }
    };

    private void run() {
        Log.d(TAG, "run() - Inside");
        ShellCommand.getInstance().run(mEtCommand.getText().toString(), callback);
    }

    private void reset() {
        Log.d(TAG, "reset() - Inside");
        ShellCommand.getInstance().finish();
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
