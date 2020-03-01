package com.truman.example.atomicfilerw;

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

/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 * Version : 1.0.0
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "MainActivity" +  TAG_SUFFIX;

    private static final int MSG_UPDATE_RESULT = 1;
    private static final int MSG_APPEND_RESULT = 2;

    private Button mBtnRead;
    private Button mBtnWrite;
    private Button mBtnScan;
    private Button mBtnException;
    private EditText mEtData;
    private TextView mTvResult;

    private final AtomicFileManager mAtomicFileManager = new AtomicFileManager(this);
    private final Handler mUIHandler = new UIHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate()");

        mBtnRead = findViewById(R.id.btn_read);
        mBtnRead.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                read();
            }
        });
        mBtnWrite = findViewById(R.id.btn_write);
        mBtnWrite.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                write();
            }
        });
        mBtnScan = findViewById(R.id.btn_scan);
        mBtnScan.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanDir();
            }
        });
        mBtnException = findViewById(R.id.btn_exception);
        mBtnException.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                exception();
            }
        });
        mEtData = findViewById(R.id.et_data);
        mTvResult = findViewById(R.id.tv_result);
    }

    private void read() {
        Log.d(TAG, "read() - Inside");
        updateResult("Read from file : " + AtomicFileManager.TEST_FILE_NAME + "\n");

        String dataStr = null;
        byte[] data = mAtomicFileManager.read(AtomicFileManager.TEST_FILE_NAME);
        if (data != null) {
            dataStr = new String(data);
        }

        appendResult(dataStr);
    }

    private void write() {
        Log.d(TAG, "write() - Inside");
        updateResult("Write to file : " + AtomicFileManager.TEST_FILE_NAME + "\n");

        String dataStr = mEtData.getText().toString();
        boolean result = mAtomicFileManager.write(dataStr.getBytes(), AtomicFileManager.TEST_FILE_NAME);

        appendResult("Write " + (result ? "Success!!!" : "Failed..."));
    }

    private void exception() {
        Log.d(TAG, "exception() - Inside");
        updateResult("Exception while writing to file : " + AtomicFileManager.TEST_FILE_NAME + "\n");

        mAtomicFileManager.writeToPrivateTest();
        appendResult("Test finished! Scan the directory!");
    }

    private void scanDir() {
        Log.d(TAG, "scan() - Inside");
        updateResult("Scan private directory....\n");

        String[] result = mAtomicFileManager.scanPrivate();
        if (result == null || result.length == 0) {
            appendResult("No files in the directory...");
        } else {
            for (String fileName : result) {
                appendResult("Found : " + fileName + "\n");
            }
        }
    }

    private void updateResult(String text) {
        mUIHandler.sendMessage(
                mUIHandler.obtainMessage(MSG_UPDATE_RESULT, text));
    }

    private void appendResult(String text) {
        mUIHandler.sendMessage(
                mUIHandler.obtainMessage(MSG_APPEND_RESULT, text));
    }

    private void handleUpdateResult(String text) {
        if (text == null)
            return;
        mTvResult.setText(text);
    }

    private void handleAppendResult(String text) {
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
                case MSG_UPDATE_RESULT: {
                    String text = msg.obj == null ?
                            "null" : msg.obj.toString();
                    activity.handleUpdateResult(text);
                    break;
                }
                case MSG_APPEND_RESULT: {
                    String text = msg.obj == null ?
                            "null" : msg.obj.toString();
                    activity.handleAppendResult(text);
                    break;
                }
                default:
                    Log.e(TAG, "Invalid message");
                    break;
            }
        }
    }
}