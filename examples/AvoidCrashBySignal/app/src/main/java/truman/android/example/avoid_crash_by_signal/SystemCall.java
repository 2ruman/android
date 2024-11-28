package truman.android.example.avoid_crash_by_signal;

import android.util.Log;

public class SystemCall {

    private static final String TAG = SystemCall.class.getSimpleName();
    private static final String LIB_NAME = "syscall_jni";

    private static final boolean isInitialized;

    static {
        isInitialized = initialize();
    }

    private static boolean initialize() {
        boolean result = false;
        Log.d(TAG, "Loading native library : " + LIB_NAME);
        try {
            System.loadLibrary(LIB_NAME);
            result = true;
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Loading completed : " + result);
        return result;
    }

    public static int setUid(int uid) {
        if (!isInitialized) {
            return -1;
        }
        return nativeSetUid(uid);
    }

    public static int setUidUnhandled(int uid) {
        if (!isInitialized) {
            return -1;
        }
        return nativeSetUidUnhandled(uid);
    }

    public static native int nativeSetUid(int uid);
    public static native int nativeSetUidUnhandled(int uid);
}
