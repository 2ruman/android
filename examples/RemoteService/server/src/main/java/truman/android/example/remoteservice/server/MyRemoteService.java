package truman.android.example.remoteservice.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import truman.android.example.remoteservice.IMyListener;
import truman.android.example.remoteservice.IMyRemoteService;
import truman.android.example.remoteservice.MyData;

import static truman.android.example.remoteservice.R.string.bind_action;

public class MyRemoteService extends Service {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = MyRemoteService.class.getSimpleName() + TAG_SUFFIX;

    private MyServiceImpl mServiceImpl;

    private final IMyRemoteService.Stub mBinder = new IMyRemoteService.Stub() {
        @Override
        public int getVersion() {
            return mServiceImpl.getVersion();
        }

        @Override
        public MyData getData() {
            return mServiceImpl.getData();
        }

        @Override
        public void registerListener(IMyListener listener) {
            mServiceImpl.registerListener(listener);
        }

        @Override
        public void unregisterListener() {
            mServiceImpl.unregisterListener();
        }
    };

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        mServiceImpl = new MyServiceImpl(); // Lazy init
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        if (getString(bind_action).equals(intent.getAction())) {
            mServiceImpl.schedule();
            return mBinder;
        }
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind()");
        mServiceImpl.cancel();
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }
}
