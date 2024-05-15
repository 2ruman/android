package truman.android.example.remoteservice.server;

import android.os.Binder;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import truman.android.example.remoteservice.IMyListener;
import truman.android.example.remoteservice.MyData;

public class MyServiceImpl {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = MyServiceImpl.class.getSimpleName() + TAG_SUFFIX;
    private static final int SERVICE_VERSION = 1;
    private static final long DELAY_MS = 1000;
    private static final long PERIOD_MS = 1000;

    private Timer mTimer;

    private final Map<String, IMyListener> mCallbacks = new HashMap<>();
    private final Object mLock = new Object();

    private final TimerTask mTask = new TimerTask() {
        int progress;

        @Override
        public void run() {
            int increment = new Random().ints(10, 20).findFirst().getAsInt();
            progress = (progress >= 100) ? increment : progress + increment;
            broadcast(Math.min(progress, 100));
        }
    };

    public int getVersion() {
        traceCallerInfo("getVersion");
        return SERVICE_VERSION;
    }

    public MyData getData() {
        traceCallerInfo("getData");
        return generateMyData();
    }

    public void schedule() {
        synchronized (mLock) {
            if (mTimer == null) {
                mTimer = new Timer(getClass().getSimpleName());
                mTimer.schedule(mTask, DELAY_MS, PERIOD_MS);
            }
        }
    }

    public void cancel() {
        synchronized (mLock) {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
        }
    }

    public void registerListener(IMyListener listener) {
        traceCallerInfo("registerListener");
        int uid = Binder.getCallingUid();
        int pid = Binder.getCallingPid();
        String key = uid + ":" + pid;
        synchronized (mLock) {
            mCallbacks.put(key, listener);
        }
    }

    public void unregisterListener() {
        traceCallerInfo("unregisterListener");
        int uid = Binder.getCallingUid();
        int pid = Binder.getCallingPid();
        String key = uid + ":" + pid;
        synchronized (mLock) {
            mCallbacks.remove(key);
        }
    }

    private void broadcast(int value) {
        Log.d(TAG, "broadcast(" + value + ")");

        synchronized (mLock) {
            Iterator<Map.Entry<String, IMyListener>> iterator = mCallbacks.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, IMyListener> entry = iterator.next();
                IMyListener listener = entry.getValue();
                try {
                    listener.onUpdate(value);
                } catch (NullPointerException | RemoteException e) {
                    Log.i(TAG, "Lost connection with " + entry.getKey());
                    iterator.remove();
                }
            }
        }
    }

    private static MyData generateMyData() {
        return new MyData(new Random().nextInt(), Long.MAX_VALUE, 0.7, "Hello, Kim!");
    }

    private static void traceCallerInfo(String functionName) {
        Log.i(TAG, String.format("Caller to [%s]- UID : %d, PID : %d",
                functionName, Binder.getCallingUid(), Binder.getCallingPid()));
    }
}