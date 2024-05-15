package truman.android.example.remoteservice;

import truman.android.example.remoteservice.IMyListener;
import truman.android.example.remoteservice.MyData;

interface IMyRemoteService {
    int getVersion();
    MyData getData();
    void registerListener(IMyListener listener);
    void unregisterListener();
}