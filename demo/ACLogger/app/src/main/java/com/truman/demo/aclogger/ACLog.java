package com.truman.demo.aclogger;

import android.util.Log;

/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 * Version : 1.0.1.0
 */
public class ACLog {

    private static final String TAG = "ACLog";
    private static final boolean REFLECT = true;

    private ACLog() {
    }

    public static void d(String msg) {
        d(null, msg);
    }

    public static void d(String tag, String msg) {
        String dMsg = ACLogUtil.makeDebugMessage(msg);
        ACLogger.enqMessage(dMsg);
        if (REFLECT && msg != null) {
            Log.d(tag != null ? tag : TAG, msg);
        }
    }

    public static void i() {
        i(null, null, new Throwable());
    }

    public static void i(String msg) {
        i(null, msg, new Throwable());
    }

    public static void i(String tag, String msg) {
        i(tag, msg, new Throwable());
    }

    private static void i(String tag, String msg, Throwable t) {
        String[] iMsgs = ACLogUtil.makeInfoMessages(msg, t);
        for (String iMsg : iMsgs) {
            ACLogger.enqMessage(iMsg);
        }
        if (REFLECT && msg != null) {
            Log.i(tag != null ? tag : TAG, msg);
        }
    }

    public static void e(Exception e) {
        e(null, e);
    }

    public static void e(String msg, Exception e) {
        e(null, msg, e);
    }

    public static void e(String tag, String msg, Exception e) {
        String[] eMsgs = ACLogUtil.makeErrorMessages(msg, e);
        for (String eMsg : eMsgs) {
            ACLogger.enqMessage(eMsg);
        }
        if (REFLECT && msg != null) {
            Log.e(tag != null ? tag : TAG, msg);
            e.printStackTrace();
        }
    }

    public static String getPath() {
        return ACLogger.getPath();
    }

    public static void setPath(String path) {
        ACLogger.setPath(path);
    }
}
