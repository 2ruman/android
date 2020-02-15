package com.truman.example.splashscreenbasic;

import android.app.Application;

import java.util.concurrent.atomic.AtomicBoolean;

public class AppMain extends Application {

    private static AtomicBoolean sIsColdStarting = new AtomicBoolean(true);

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static boolean isColdStarting() {
        return sIsColdStarting.getAndSet(false);
    }
}
