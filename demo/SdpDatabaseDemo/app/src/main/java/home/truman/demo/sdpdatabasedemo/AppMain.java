package home.truman.demo.sdpdatabasedemo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class AppMain extends Application {
    private static final String TAG = "AppMain" +  AppConstants.TAG_SUFFIX;

    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        Log.d(TAG, "onCreate()");
    }
}
