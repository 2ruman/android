package com.truman.demo.aclogger;

import android.app.Application;
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
    public static final String TYPE_INFO  = "I";
    public static final String TYPE_DEBUG = "D";
    public static final String TYPE_ERROR = "E";

    // Debugging Control
    public static final boolean DEBUG_LOG = true;
    public static final boolean DEBUG_LOGGER = true;
    public static final boolean DEBUG_LOGFILE = true;

    public static String[] makeInfoMessages(@Nullable String msg) {
        String prefix = getCurrentTime() + DELIMITER + TYPE_INFO + DELIMITER;
        String info = prefix + makeCallingInfo();

        if (msg == null) {
            return new String[] { info };
        } else {
            return new String[] { prefix + msg, info };
        }
    }

    public static String makeDebugMessage(@Nullable String msg) {
        return getCurrentTime() + DELIMITER + TYPE_DEBUG + DELIMITER + safe(msg);
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

    private static String makeCallingInfo() {
        String ret = "";
        try {
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            StackTraceElement curr = stacks[2];
            StackTraceElement prev = stacks[3];
            String currInfo = curr.getClassName() + "." + curr.getMethodName() + "()";
            String prevInfo = prev.getClassName() + "." + prev.getMethodName() + "()";
            ret = makePairs("Curr", currInfo, "Prev", prevInfo);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        return ret;
    }

    private static String getCurrentTime() {
        long currTime = System.currentTimeMillis();
        Date date = new Date(currTime);
        Format dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        return dateFormat.format(date);
    }

    private static String safe(String val) {
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
    public static File getExternalCacheDir() {
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
