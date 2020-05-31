package com.truman.demo.aclogger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "MainActivity" +  TAG_SUFFIX;

    private Button mBtnRun;
    private Button mBtnOpen;
    private TextView mTvStatus;

    private File mTargetFile;

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
        mBtnOpen = findViewById(R.id.btn_open);
        mBtnOpen.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                open();
            }
        });
        mTvStatus = findViewById(R.id.tv_status);

        // [ Choose one of targetable paths ]
        //
        // 1. Internal app data directory path
        // mTargetFile = new File(this.getFilesDir(), "aclog");
        //
        // 2. External app cache directory path
        mTargetFile = new File(ACLog.getPath());
        //
        // 3. External storage directory path
        // mTargetFile = new File(Environment.getExternalStorageDirectory(), "aclog");

        ACLog.setPath(mTargetFile.getAbsolutePath());
        mTvStatus.setText("Target log file path :\n" + ACLog.getPath());
    }

    private void run() {
        Log.d(TAG, "run() - Inside");

        runInternal();
    }

    private void runInternal() {
        ACLog.d(TAG, "Run!");
        ACLog.i(TAG, "Run!");
        ACLog.e(TAG, "Run!", new RuntimeException());

        new StressTestTask(10).execute();
    }

    private void open() {
        Log.d(TAG, "open() - Inside");

        Log.d(TAG, "File to open : " + mTargetFile.getAbsolutePath());
        openTargetFile(mTargetFile);
    }

    private void openTargetFile(File file){
        Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "text/*");

        startActivity(intent);
    }

    private class StressTestTask extends AsyncTask<Void, Void, Boolean> {
        private int mThreadCnt = 0;
        StressTestTask(int threadCnt) {
            if (threadCnt > 0)
                mThreadCnt = threadCnt;
        }

        @Override
        protected void onPreExecute() {
            mBtnRun.setEnabled(false);
            mBtnOpen.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            List<TestThread> threads = new ArrayList<>();
            for (int i = 1 ; i <= mThreadCnt ; i++) {
                TestThread thread = new TestThread(i);
                thread.start();
                threads.add(thread);
            }

            for (TestThread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {}
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mBtnRun.setEnabled(true);
            mBtnOpen.setEnabled(true);
        }
    }

    private static class TestThread extends Thread {
        static final int REPS = 10;
        static final int MIN_DELAY_MS = 100;
        static final int MAX_DELAY_MS = 1000;
        static final int MIN_BYTES = 20;
        static final int MAX_BYTES = 60;
        static final int TYPE_INFO = 0x1;
        static final int TYPE_DEBUG = 0x2;
        static final int TYPE_ERROR = 0x4;

        private String tag;
        private SecureRandom secureRandom = new SecureRandom();

        TestThread(int id) {
            this.tag = "Thread("+id+")";
        }

        public String getTag() {
            return tag;
        }

        private int getType() {
            int lottery = secureRandom.nextInt(10);
            if (lottery >= 9) {
                return TYPE_ERROR; // 10 % probability to be error log
            } else if (lottery > 6) {
                return TYPE_INFO;  // 20 % probability to be info log
            } else {
                return TYPE_DEBUG; // 70 % probability to be debug log
            }
        }

        @Override
        public void run() {

            for (int i = 0 ; i < REPS ; i++) {
                try {
                    int delay = secureRandom.nextInt(MAX_DELAY_MS - MIN_DELAY_MS) + MIN_DELAY_MS;
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                }
                int numBytes = secureRandom.nextInt(MAX_BYTES - MIN_BYTES) + MIN_BYTES;
                byte[] bytes = new byte[numBytes];
                secureRandom.nextBytes(bytes);

                switch(getType()) {
                    case TYPE_INFO:
                        ACLog.i(getTag(), ACLogUtil.bytesToHex(bytes));
                        // ACLog.i(getTag() + " : " + ACLogUtil.bytesToHex(bytes));
                        break;
                    case TYPE_DEBUG:
                        ACLog.d(getTag(), ACLogUtil.bytesToHex(bytes));
                        // ACLog.d(getTag() + " : " + ACLogUtil.bytesToHex(bytes));
                        break;
                    case TYPE_ERROR:
                        ACLog.e(getTag(), ACLogUtil.bytesToHex(bytes), generateFakeException());
                        // ACLog.e(getTag() + " : " + ACLogUtil.bytesToHex(bytes), generateFakeException());
                        break;
                }
            }
        }
    }
    private static Exception generateFakeException() {
        try {
            fake1();
        } catch (Exception e) {
            return e;
        }
        return null;
    }
    private static void fake1() throws SecurityException {
        fake2();
    }
    private static void fake2() throws SecurityException {
        fake3();
    }
    private static void fake3() throws SecurityException {
        fake4();
    }
    private static void fake4() throws SecurityException {
        throw new SecurityException("Fake exception...!");
    }
}
