package com.truman.demo.userchecker;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class AppMain extends Application {
    private static final String APP_NAME = "UserChecker";
    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "AppMain" +  TAG_SUFFIX;
    private static boolean sIsRunning = false;
    private static Context sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        sAppContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static String getName() {
        return APP_NAME;
    }

    public static String getSuffix() {
        return TAG_SUFFIX;
    }

    public static void setTaskRunning(boolean isRunning) {
        sIsRunning = isRunning;
    }

    public static boolean isTaskRunning() {
        return sIsRunning;
    }
}
