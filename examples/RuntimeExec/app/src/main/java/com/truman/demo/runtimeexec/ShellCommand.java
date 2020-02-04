package com.truman.demo.runtimeexec;

import androidx.annotation.NonNull;

import android.os.RemoteException;
import android.util.Log;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class ShellCommand {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "ShellCommand" +  TAG_SUFFIX;

    private static final int ONE_PAGE_SIZE = 4096;
    private static final long DEFAULT_WAIT_SEC = 0;

    private static ShellCommand sInstance;

    private CommandThread mRunningThread;

    private final Object mLock = new Object();

    private ShellCommand() {}

    public static synchronized ShellCommand getInstance() {
        if (sInstance == null) {
            sInstance = new ShellCommand();
        }
        return sInstance;
    }

    private final class CommandThread extends Thread {
        private String mCommand;
        private long mWaitSec;
        private IShellCommandCallback mCallback;
        private boolean mIsRunning;

        public CommandThread(@NonNull String command, long waitSec,
                             @NonNull IShellCommandCallback callback) {
            mCommand = command;
            mWaitSec = waitSec;
            mCallback = callback;
            mIsRunning = false;
        }

        public boolean isRunning() {
            return mIsRunning;
        }

        public void stopRunning() {
            mIsRunning = false;
        }

        public void run() {
            mIsRunning = true;
            try {
                Process proc = Runtime.getRuntime().exec(mCommand);
                if (mWaitSec > 0) {
                    proc.waitFor(mWaitSec, TimeUnit.SECONDS);
                }
                InputStream is = proc.getInputStream();
                int readLen;
                byte[] page = new byte[ONE_PAGE_SIZE];
                while (mIsRunning
                        && (readLen = is.read(page)) > 0) {
                    mCallback.onReadPage(page, readLen);
                }
                is.close();
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error : " + e.getMessage());
                e.printStackTrace();

                try {
                    mCallback.onFailure(e.getMessage());
                } catch (RemoteException re) {
                    Log.e(TAG, "Failed to call onFailure() callback...");
                    re.printStackTrace();
                }
            }
            mIsRunning = false;
        }
    }

    public void finish() {
        synchronized (mLock) {
            if (mRunningThread != null) {
                mRunningThread.stopRunning();
                mRunningThread = null;
            }
        }
    }

    public boolean run(String command, IShellCommandCallback callback) {
        return run(command, DEFAULT_WAIT_SEC, callback);
    }

    public boolean run(String command, long waitSec, IShellCommandCallback callback) {
        if (command == null || command.trim().isEmpty()) {
            Log.e(TAG, "Invalid command...");
            return false;
        }
        if (callback == null) {
            Log.e(TAG, "Invalid callback...");
            return false;
        }

        synchronized (mLock) {
            if (mRunningThread != null && mRunningThread.isRunning()) {
                Log.e(TAG, "Last command still being executed...");
                return false;
            }
            mRunningThread = new CommandThread(command, waitSec, callback);
            mRunningThread.start();
        }
        return true;
    }
}
