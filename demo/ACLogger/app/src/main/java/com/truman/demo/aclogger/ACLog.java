package com.truman.demo.aclogger;

import android.util.Log;

/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 * Version : 1.0.0
 */
public class ACLog {

    private static final String TAG = "ACLog";
    private static final boolean DEBUG = ACLogUtil.DEBUG_LOG;

    private ACLog() {
    }

    public static void i() {
        i(null);
    }

    public static void i(String msg) {
        String[] iMsgs = ACLogUtil.makeInfoMessages(msg);
        for (String iMsg : iMsgs) {
            ACLogger.enqMessage(iMsg);
        }
        if (DEBUG && msg != null) {
            Log.i(TAG+".i", msg);
        }
    }

    public static void d(String msg) {
        String dMsg = ACLogUtil.makeDebugMessage(msg);
        ACLogger.enqMessage(dMsg);
        if (DEBUG && msg != null) {
            Log.d(TAG+".d", msg);
        }
    }

    public static void e(Exception e) {
        e(null, e);
    }

    public static void e(String msg, Exception e) {
        String[] eMsgs = ACLogUtil.makeErrorMessages(msg, e);
        for (String eMsg : eMsgs) {
            ACLogger.enqMessage(eMsg);
        }
        if (DEBUG && msg != null) {
            Log.e(TAG+".e", msg);
            e.printStackTrace();
        }
    }
}
