package com.truman.demo.aclogger;

import android.app.Application;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ACLogUtil {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String DELIMITER = ","; // For CSV
    public static final String LEVEL_INFO = "I";
    public static final String LEVEL_DEBUG = "D";
    public static final String LEVEL_ERROR = "E";

    // Debugging Control
    public static final boolean LOG_DEBUG = false;
    public static final boolean LOGGER_DEBUG = true;
    public static final boolean LOGFILE_DEBUG = true;

    public static String makeSequence(String msg, @NonNull String level) {
        return getCurrentTime() + DELIMITER + level + DELIMITER + safe(msg);
    }

    public static String[] makeErrorSequences(String msg, Exception e) {
        String prefix = getCurrentTime() + DELIMITER + LEVEL_ERROR + DELIMITER;
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
