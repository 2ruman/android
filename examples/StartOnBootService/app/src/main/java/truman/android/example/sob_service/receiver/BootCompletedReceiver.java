package truman.android.example.sob_service.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import truman.android.example.sob_service.service.MyService;

public class BootCompletedReceiver extends BroadcastReceiver {

    private static final String TAG = BootCompletedReceiver.class.getCanonicalName() + ".2ruman";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive() - " + intent.getAction());
        startMyService(context);
    }

    private void startMyService(Context context) {
        Intent intent = new Intent(context, MyService.class);
        context.startService(intent);
    }
}