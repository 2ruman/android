package com.truman.demo.aclogger;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.PrintWriter;

/**
 * A service for dump test. For the test, you need the command below after binding this service.
 *
 * adb shell dumpsys activity service com.truman.demo.aclogger/.MainService
 */
public class MainService extends Service {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "MainService" +  TAG_SUFFIX;

    private final ServiceBinder mServiceBinder = new ServiceBinder();

    public final class ServiceBinder extends Binder {
        public MainService getService() {
            return MainService.this;
        }
    }

    /*
     * We don't need this callback for dump test
     *
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() - flags : " + flags + ", startId : " + startId + ", intent :" + intent);
        return START_NOT_STICKY;
    }
     */

    @Override
    public IBinder onBind(Intent intent) {
        ACLog.d(TAG, String.format("onBind() - PID : %d, TID : %d",
                Binder.getCallingPid(), android.os.Process.myTid()));
        return mServiceBinder;
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        Log.d(TAG, "dump() - " + (args == null ? "null" : "argc : " + args.length));
        ACLog.dump(pw);
    }
}