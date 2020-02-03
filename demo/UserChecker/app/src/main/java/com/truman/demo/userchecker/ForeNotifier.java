package com.truman.demo.userchecker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ForeNotifier extends Service {

    private static final String TAG = "ForeNotifier" + AppMain.getSuffix();

    private UserChecker mUserChecker;
    private UpdateThread mUpdateThread;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() - TID : " + android.os.Process.myTid());

        AppMain.setTaskRunning(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() - TID : " + android.os.Process.myTid());

        startForeground(NotiManager.getId(), NotiManager.getInstance()
                .init("User Checker", "User State").get());

        mUserChecker = new UserChecker(this,
                intent.getIntExtra(UserChecker.EXTRA_USER_ID, -1));
        mUpdateThread = new UpdateThread();
        mUpdateThread.start();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() - TID : " + android.os.Process.myTid());

        mUpdateThread.interrupt();
        mUpdateThread.finish();

        AppMain.setTaskRunning(false);
    }

    private class UpdateThread extends Thread {
        static final int BREAK_MILLS = 2000;
        boolean mRunning;
        int mReps = 0;

        private UpdateThread() {
            mRunning = true;
        }

        public void finish() {
            mRunning = false;
        }

        public void run() {
            Log.d(TAG, "run() - TID : " +  android.os.Process.myTid());

            while (mRunning) {
                String title = "User State (#" + (++mReps) + ")";
                try {
                    Thread.sleep(BREAK_MILLS);

                    NotiManager.getInstance()
                            .setTitle(title)
                            .setContents(
                                    mUserChecker.isRunning(),
                                    mUserChecker.isUserRunningOrStopping(),
                                    mUserChecker.isUserUnlocked()
                            ).show();
                    // Debug
                    Log.d(TAG, "Reps : # " + mReps);
                    Log.d(TAG, mUserChecker.isRunning());
                    Log.d(TAG, mUserChecker.isUserRunningOrStopping());
                    Log.d(TAG, mUserChecker.isUserUnlocked());
                } catch (InterruptedException e) {
                    Log.d(TAG, "Thread interrupted!!!");
                    mRunning = false;
                } catch (Exception e) {
                    // Possible to cause java.lang.SecurityException:
                    // You need INTERACT_ACROSS_USERS or MANAGE_USERS permission to...
                    Log.e(TAG, "Unexpected error : " + e.getMessage());
                    e.printStackTrace();
                    mRunning = false;
                }
                if (!mRunning) {
                    NotiManager.getInstance()
                            .setTitle(title + " - Terminated").show();
                    AppMain.setTaskRunning(false);
                }
            }
        }
    }
}
