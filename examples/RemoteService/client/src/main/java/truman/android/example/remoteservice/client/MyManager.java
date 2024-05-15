package truman.android.example.remoteservice.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import truman.android.example.remoteservice.IMyListener;
import truman.android.example.remoteservice.IMyRemoteService;
import truman.android.example.remoteservice.MyData;

import static truman.android.example.remoteservice.R.string.bind_action;
import static truman.android.example.remoteservice.R.string.service_class_name;
import static truman.android.example.remoteservice.R.string.service_package_name;

public class MyManager {

    private final Context mContext;
    private final String mBindAction;
    private final String mServiceClassName;
    private final String mServicePackageName;

    private IMyRemoteService mService;

    public MyManager(Context context) {
        mContext = context;
        mBindAction = context.getString(bind_action);
        mServiceClassName = context.getString(service_class_name);
        mServicePackageName = context.getString(service_package_name);
    }

    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IMyRemoteService.Stub.asInterface(service);
            Ui.stopProgress();
            Ui.println("Service connected!");
            Ui.log(name.toString());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            Ui.println("Service disconnected!");
            Ui.log(name.toString());
        }

        @Override
        public void onBindingDied(ComponentName name) {
            mService = null;
            Ui.println("Binding died...");
            Ui.log(name.toString());
        }

        @Override
        public void onNullBinding(ComponentName name) {
            mService = null;
            Ui.println("Null binding...");
            Ui.log(name.toString());
        }
    };

    public int getVersion(int defaultVal) {
        int ret = defaultVal;
        try {
            ret = getService().getVersion();
        } catch (RemoteException e) {
            Ui.println("Failed to get version: " + e);
        }
        return ret;
    }

    public MyData getData() {
        MyData md = null;
        try {
            md = getService().getData();
        } catch (RemoteException e) {
            Ui.println("Failed to get data: " + e);
        }
        return md;
    }

    public boolean registerListener(IMyListener listener) {
        boolean result = false;
        try {
            getService().registerListener(listener);
            result = true;
        } catch (RemoteException e) {
            Ui.println("Failed to register listener: " + e);
        }
        return result;
    }

    public boolean unregisterListener() {
        boolean result = false;
        try {
            getService().unregisterListener();
            result = true;
        } catch (RemoteException e) {
            Ui.println("Failed to unregister listener: " + e);
        }
        return result;
    }

    private IMyRemoteService getService() throws RemoteException {
        if (mService == null) {
            throw new RemoteException("Service not connected");
        }
        return mService;
    }

    public void bindService() {
        if (mService != null) {
            Ui.println("Service already connected...");
            return;
        }
        Ui.startProgress();
        Intent intent = new Intent();
        intent.setClassName(mServicePackageName, mServiceClassName);
        intent.setAction(mBindAction);
        boolean isBinding = mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        if (!isBinding) {
            Ui.println("Failed in binding...");
            Ui.stopProgress();
        }
    }

    public void unbindService() {
        if (mService == null) {
            Ui.println("Service already disconnected...");
            return;
        }
        mContext.unbindService(mConnection);
        mService = null;
    }
}
