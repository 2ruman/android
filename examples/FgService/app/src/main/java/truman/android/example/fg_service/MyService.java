package truman.android.example.fg_service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {

    private static final String TAG = MyService.class.getSimpleName() + ".2ruman";
    private static final String SERVICE_NOTI_TITLE = "My Service";
    private static final String SERVICE_NOTI_TEXT = "My Service is running";

    private ServiceNotification myServiceNotification;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        myServiceNotification = new ServiceNotification(getApplicationContext(), MyService.class);
        myServiceNotification.setLinkedActivityClass(MainActivity.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");

        becomeForeground();

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

        Log.d(TAG, "onTimeout() - Importance = " + Utils.getProcessImportance(this));

        Ui.println("Timeout occurred");

        if (Utils.getProcessImportance(this) >=
                ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND_SERVICE) {
            Ui.println("Stop service");
            stopSelf();
        } else {
            becomeForeground();
        }
    }

    private void becomeForeground() {
        Log.d(TAG, "becomeForeground() - SDK :" + Build.VERSION.SDK_INT);

        Ui.println("Become foreground");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(myServiceNotification.getNotificationId(),
                    myServiceNotification.getNotification(SERVICE_NOTI_TITLE, SERVICE_NOTI_TEXT),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE);
        } else {
            startForeground(myServiceNotification.getNotificationId(),
                    myServiceNotification.getNotification(SERVICE_NOTI_TITLE, SERVICE_NOTI_TEXT));
        }
    }
}