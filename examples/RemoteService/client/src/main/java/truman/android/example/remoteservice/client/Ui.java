package truman.android.example.remoteservice.client;

import android.util.Log;

import java.lang.ref.WeakReference;

public final class Ui {

    private static final boolean REFLECTION = true;
    private static final String TAG_SUFFIX = ".2ruman"; // For grep

    private static WeakReference<Out> out = new WeakReference<>(null);

    public interface Out {
        void print(String s);

        void println(String s);

        void clear();

        void startProgress();

        void stopProgress();
    }

    public static void setOut(Out o) {
        synchronized (Ui.class) {
            out = new WeakReference<>(o);
        }
    }

    public static void log(String s) {
        logTagged(s);
    }

    // Must keep the depth! (Caller --> Wrapper --> this)
    private static void logTagged(String s) {
        String tag = "Unknown";
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        if (stacks != null && stacks.length > 3) {
            String className = stacks[3].getClassName();
            tag = className.substring(className.lastIndexOf(".") + 1);
        }
        Log.d(tag + TAG_SUFFIX, s);
    }

    private static void reflect(String s) {
        if (REFLECTION) {
            logTagged(s);
        }
    }

    public static void print(String s) {
        synchronized (Ui.class) {
            Out o = out.get();
            if (o != null) o.print(s);
        }
    }

    public static void println(String s) {
        reflect(s);
        synchronized (Ui.class) {
            Out o = out.get();
            if (o != null) o.println(s);
        }
    }

    public static void clear() {
        synchronized (Ui.class) {
            Out o = out.get();
            if (o != null) o.clear();
        }
    }

    public static void startProgress() {
        synchronized (Ui.class) {
            Out o = out.get();
            if (o != null) o.startProgress();
        }
    }

    public static void stopProgress() {
        synchronized (Ui.class) {
            Out o = out.get();
            if (o != null) o.stopProgress();
        }
    }
}
