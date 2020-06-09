package com.truman.demo.aclogger;

import android.app.Application;
import android.os.Binder;
import android.os.Environment;

import androidx.annotation.Nullable;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ACLogUtil {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String DELIMITER = ","; // For CSV
    private static final String TYPE_INFO  = "I";
    private static final String TYPE_DEBUG = "D";
    private static final String TYPE_ERROR = "E";

    public static String makeDebugMessage(@Nullable String msg) {
        return getCurrentTime() + DELIMITER + TYPE_DEBUG + DELIMITER + safe(msg);
    }

    public static String[] makeInfoMessages(@Nullable String msg, Throwable throwable) {
        String prefix = getCurrentTime() + DELIMITER + TYPE_INFO + DELIMITER;
        String info = prefix;
        if (throwable != null) {
            try {
                StackTraceElement[] stacks = throwable.getStackTrace();
                String currInfo = stacks.length > 1 ?
                        stacks[1].getClassName() + "." + stacks[1].getMethodName() + "()" : "()";
                String prevInfo = stacks.length > 2 ?
                        stacks[2].getClassName() + "." + stacks[2].getMethodName() + "()" : "()";
                String uid = String.valueOf(Binder.getCallingUid());
                String pid = String.valueOf(Binder.getCallingPid());
                String tid = String.valueOf(android.os.Process.myTid());
                String userId = android.os.Process.myUserHandle().toString()
                        .replaceAll("[^0-9]", "");

                info += makePairs("UserId", userId, "UID", uid, "PID", pid, "TID", tid, "Curr", currInfo, "Prev", prevInfo);
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }

        if (msg == null) {
            return new String[] { info };
        } else {
            return new String[] { prefix + msg, info };
        }
    }

    public static String[] makeErrorMessages(@Nullable String msg, Exception e) {
        String prefix = getCurrentTime() + DELIMITER + TYPE_ERROR + DELIMITER;
        String[] ret = new String[0];

        if (e == null) {
            return ret;
        }

        try {
            // Obtain the exception stack
            StackTraceElement[] elements = e.getStackTrace();

            // Calculate array length
            int i = 0,
                    seqsLen = (msg != null ? 1 : 0) + 1 + elements.length; // Msg(1 or 0) + exception + stacks

            // Fill the string array to return
            ret = new String[seqsLen];
            if (msg != null)
                ret[i++] = prefix + msg;
            ret[i++] = prefix + e.toString();
            for (StackTraceElement element : elements) {
                ret[i++] = prefix + element.toString();
            }
        } catch (Exception unexpected) {}

        return ret;
    }

    public static String makePairs(Object... objs) {
        if (objs == null) {
            return "null";
        } else if (objs.length == 0) {
            return "[:]";
        }

        StringBuilder sb = new StringBuilder((objs.length + 1) >> 1);
        String pair = "";
        boolean isOpen = false;

        for (int i = 0 ; i < objs.length ; i++) {
            String element;
            if (objs[i] == null) {
                element = "null";
            } else if(objs[i] instanceof byte[]) { // Special handling for byte array!
                element = bytesToHex((byte[]) objs[i]);
            } else {
                element = objs[i].toString();
            }
            if (!isOpen) {
                pair = "[ " + element + " : ";
            } else {
                pair += element + " ]";
                sb.append(pair);
            }
            isOpen = !isOpen;
        }
        // Door is still open. Close the door!
        if (isOpen) {
            sb.append(pair += "]");
        }
        return sb.toString();
    }

    public static String getCurrentTime() {
        long currTime = System.currentTimeMillis();
        Date date = new Date(currTime);
        Format dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        return dateFormat.format(date);
    }

    public static String safe(String val) {
        return (val != null) ? val : "null";
    }

    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) return "null";
        if (bytes.length == 0) return "";

        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /*
     * TODO : Need to find any alternative way to proper storage 'cause getting external storage
     *        directory had been deprecated for security concern, precisely user privacy.
     */
    static File getExternalCacheDir() {
        try {
            return getApplicationReflected().getExternalCacheDir();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Environment.getExternalStorageDirectory();
    }

    private static Application getApplicationReflected() throws Exception {
        return (Application) Class.forName("android.app.ActivityThread")
                .getMethod("currentApplication").invoke(null, (Object[]) null);
    }
}
