package truman.android.example.jniconversion;

import android.util.Log;

import java.util.ArrayList;

public final class MyImplNative {

    private static final String TAG = MyImplNative.class.getSimpleName() + ".2ruman";
    private static final String LIB_NAME = "jni_conv";

    private static boolean isNativeOK;

    static {
        isNativeOK = initialize();
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
        Log.d(TAG, result ? "Loading completed!" : "Loading failed...");
        return result;
    }

    public static boolean isInitialized() {
        if (!isNativeOK) {
            Log.e(TAG, "Not initialized...");
        }
        return isNativeOK;
    }

    public static String getWelcomeMessage() {
        if (!isInitialized()) {
            return null;
        }
        String ret = null;
        try {
            ret = nativeGetWelcomeMessage();
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static ArrayList<String> getStrList() {
        if (!isInitialized()) {
            return null;
        }
        ArrayList<String> ret = null;
        try {
            ret = nativeGetStrDataList();
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static ArrayList<BasicData> getDataList() {
        if (!isInitialized()) {
            return null;
        }
        ArrayList<BasicData> ret = null;
        try {
            ret = nativeGetDataList();
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static int printStrList(ArrayList<String> list) {
        if (!isInitialized()) {
            return -1;
        }
        int ret = -1;
        try {
            ret = nativePrintStrList(list);
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
        return ret;
    }
    private static native String nativeGetWelcomeMessage();
    private static native ArrayList<String> nativeGetStrDataList();
    private static native ArrayList<BasicData> nativeGetDataList();
    private static native int nativePrintStrList(ArrayList<String> list);
}
