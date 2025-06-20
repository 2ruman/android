package truman.android.example.sob_service.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import truman.android.example.sob_service.ui.MainActivity;

public class MyService extends Service {

    private static final String TAG = MyService.class.getSimpleName() + ".2ruman";
    private static final String SERVICE_NOTI_TITLE = "My Service";
    private static final String SERVICE_NOTI_TEXT = "My Service is running";

    private MyServiceNotification myServiceNotification;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        myServiceNotification = new MyServiceNotification(getApplicationContext(), MyService.class);
        myServiceNotification.setLinkedActivityClass(MainActivity.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");

        becomeForeground();

        runMyServer(); // For example

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTimeout(int startId) {
        super.onTimeout(startId);

        Log.d(TAG, "onTimeout()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        stopMyServer(); // For example
    }

    private void becomeForeground() {
        Log.d(TAG, "becomeForeground() - SDK :" + Build.VERSION.SDK_INT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(myServiceNotification.getNotificationId(),
                    myServiceNotification.getNotification(SERVICE_NOTI_TITLE, SERVICE_NOTI_TEXT),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(myServiceNotification.getNotificationId(),
                    myServiceNotification.getNotification(SERVICE_NOTI_TITLE, SERVICE_NOTI_TEXT));
        }
    }

    private void runMyServer() {
        Log.d(TAG, "Server is not implemented");
    }

    private void stopMyServer() {
        Log.d(TAG, "Server is not implemented");
    }
}
