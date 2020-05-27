package com.truman.demo.aclogger;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.Queue;

public class ACLogger {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "ACLog" + TAG_SUFFIX;
    private static final boolean DEBUG = ACLogUtil.LOGGER_DEBUG;

    private static final int ACCUM_TIME_MS = 3000;

    private static final int STATE_IDLE = 0;
    private static final int STATE_ACCUMULATING = 1;
    private static final int STATE_SAVING = 2;

    // TODO : Implement/open API to set a path for saving the log file
    public static void setPath(String path) {
    }

    static Logger mLogger = new Logger();

    public static void enqMessage(String msg) {
        synchronized (Logger.getLock()) {
            if (Logger.getStateLocked() == STATE_IDLE) {
                Logger.setStateLocked(STATE_ACCUMULATING);
                mLogger = new Logger();
                mLogger.setDaemon(true);
                mLogger.start();
                mLogger.add(msg);
            } else if (Logger.getStateLocked() == STATE_ACCUMULATING
                    || Logger.getStateLocked() == STATE_SAVING) {
                mLogger.add(msg);
            }
        }
    }

    private static class Logger extends Thread {
        /*
         * [ Caution! ]
         *
         *     sLock object used as a locker for Logger control.
         *     mLogQ is used as a locker for log buffer control.
         *
         *     Locking order : sLock --> mLogQ
         */
        private static final Object sLock = new Object(); // Logger Control Lock
        private static Queue<String> mLogQ = new LinkedList<>();
        private static Queue<String> mSavQ;
        private static final int MAX_LINES = 300;

        private static int mState = 0;

        public static Object getLock() {
            return sLock;
        }

        public static int getStateLocked() {
            return mState;
        }

        public static void setStateLocked(int state) {
            mState = state;
        }

        private static void preventBOFLocked(Queue<String> logQ) {
            if (logQ.size() >= MAX_LINES) {
                Log.e(TAG, "Log buffer reached the limit!");
                logQ.clear();
            }
        }

        public static void add(String msg) {
            synchronized (mLogQ) {
                preventBOFLocked(mLogQ);
                mLogQ.add(msg);
            }
        }

        @Override
        public void run() {
            for (;;) {
                LogD("Start accumulating");
                try {
                    Thread.sleep(ACCUM_TIME_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }

                synchronized (mLogQ) {
                    mSavQ = mLogQ;
                    mLogQ = new LinkedList<>();
                }

                LogD("Start saving");
                synchronized (sLock) {
                    setStateLocked(STATE_SAVING);
                }

                ACLogFile.saveFile(mSavQ);

                synchronized (sLock) {
                    synchronized (mLogQ) {
                        if (!mLogQ.isEmpty()) {
                            LogD("Back to accumulate");
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
    }

    private static void LogD(@NonNull String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }
}
