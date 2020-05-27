package com.truman.demo.aclogger;

import android.util.Log;

import static com.truman.demo.aclogger.ACLogUtil.LEVEL_INFO;
import static com.truman.demo.aclogger.ACLogUtil.LEVEL_DEBUG;
//import static com.truman.demo.aclogger.ACLogUtil.LEVEL_ERROR;

/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 * Version : 1.0.0
 */
public class ACLog {

    private static final String TAG = "ACLog";
    private static final boolean DEBUG = ACLogUtil.LOG_DEBUG;

    private ACLog() {
    }

    public static void i(String msg) {
        String seq = ACLogUtil.makeSequence(msg, LEVEL_INFO);
        ACLogger.enqMessage(seq);
        if (DEBUG && msg != null) {
            Log.i(TAG+".i", msg);
        }
    }

    public static void d(String msg) {
        String seq = ACLogUtil.makeSequence(msg, LEVEL_DEBUG);
        ACLogger.enqMessage(seq);
        if (DEBUG && msg != null) {
            Log.d(TAG+".d", msg);
        }
    }

    public static void e(Exception e) {
        e(null, e);
    }

    public static void e(String msg, Exception e) {
        String[] seqs = ACLogUtil.makeErrorSequences(msg, e);
        for (String seq : seqs) {
            ACLogger.enqMessage(seq);
        }
        if (DEBUG && msg != null) {
            Log.e(TAG+".e", msg); e.printStackTrace();
        }
    }
}
