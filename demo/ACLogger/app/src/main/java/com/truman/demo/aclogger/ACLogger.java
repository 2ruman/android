package com.truman.demo.aclogger;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

public final class ACLogger {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "ACLogger" + TAG_SUFFIX;
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final int ACCUM_TIME_MS = 3000;

    private static final int STATE_IDLE = 0;
    private static final int STATE_ACCUMULATING = 1;
    private static final int STATE_SAVING = 2;

    static String getPath() {
        return Logger.getPath();
    }

    static void setPath(String path) {
        Logger.setPath(path);
    }

    private static Logger mLogger = new Logger();

    static void enqMessage(String msg) {
        synchronized (Logger.getLock()) {
            if (Logger.getStateLocked() == STATE_IDLE) {
                Logger.setStateLocked(STATE_ACCUMULATING);
                mLogger = new Logger();
                mLogger.setDaemon(true);
                mLogger.start();
            }
            // if (Logger.getStateLocked() == STATE_ACCUMULATING
            //      || Logger.getStateLocked() == STATE_SAVING)
            if (mLogger != null) {
                mLogger.add(msg);
            }
        }
    }

    private static class Logger extends Thread {
        /*
         * [ Caution! ]
         *
         *     cLock object is used as a locker for logger control.
         *     qLock object is used as a locker for logger-queue handling.
         *
         *     Follow the locking order : cLock --> qLock
         */
        private static final Object cLock = new Object(); // Logger Control Lock
        private static final Object qLock = new Object(); // Logger Queue Lock
        private static Queue<String> mLogQ = new LinkedList<>();
        private static Queue<String> mSavQ;
        private static final int MAX_LINES = 300; // Buffer limit for system efficiency

        private static int mState = 0;
        private static AtomicReference<String> sFilePath = new AtomicReference<>();

        private static Object getLock() {
            return cLock;
        }

        private static int getStateLocked() {
            return mState;
        }

        private static void setStateLocked(int state) {
            mState = state;
        }

        private static void preventBOFLocked(Queue<String> logQ) {
            if (logQ.size() >= MAX_LINES) {
                LogE("Log buffer reached the limit! Clearing the buffer...");
                logQ.clear();

                // Recording what happened at this moment, however this message below also can
                // be lost by the prevention of next reaching.
                logQ.add(ACLogUtil.makeDebugMessage(
                        "ACLog: Unfortunately buffer cleared to prevent overflow!"));
            }
        }

        private void add(String msg) {
            synchronized (qLock) {
                preventBOFLocked(mLogQ);
                mLogQ.add(msg);
            }
        }

        @Override
        public void run() {
            for (;;) {
                LogD("Start accumulating...");
                try {
                    Thread.sleep(ACCUM_TIME_MS);
                } catch (InterruptedException e) {
                    LogE("Logger interrupted!");
                    return;
                }

                synchronized (qLock) {
                    mSavQ = mLogQ;
                    mLogQ = new LinkedList<>();
                }

                LogD("Start saving...");
                synchronized (cLock) {
                    setStateLocked(STATE_SAVING);
                }

                // The saving queue is valid only in this runnable region,
                // thus no lock is required, and after saving the queue shall be disposed for GC.
                ACLogFile.saveFile(getPath(), mSavQ);
                mSavQ.clear();
                mSavQ = null;

                synchronized (cLock) {
                    synchronized (qLock) {
                        if (!mLogQ.isEmpty()) {
                            LogD("Back to accumulate!");
                            setStateLocked(STATE_ACCUMULATING);
                            continue;
                        } else {
                            LogD("Finished!");
                            setStateLocked(STATE_IDLE);
                        }
                    }
                }
                break;
            }
        }

        private static String getPath() {
            String path = sFilePath.get();
            if (path == null) {
                return ACLogFile.getDefaultPath();
            }
            return path;
        }

        private static void setPath(String path) {
            sFilePath.set(path);
        }
    }

    public static void dump(@NonNull PrintWriter pw) {
        ACLogFile.dump(getPath(), pw);
    }

    private static void LogD(@NonNull String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }

    private static void LogE(@NonNull String msg) {
        Log.e(TAG, msg);
    }
}
