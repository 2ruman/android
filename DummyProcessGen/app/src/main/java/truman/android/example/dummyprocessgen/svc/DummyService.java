package truman.android.example.dummyprocessgen.svc;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import truman.android.example.dummyprocessgen.common.ChainableDummy;
import truman.android.example.dummyprocessgen.common.Utils;

public class DummyService extends Service implements ChainableDummy {

    private Intent intent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(getTag(), "onStartCommand()");
        setIntent(intent);
        try {
            return super.onStartCommand(intent, flags, startId);
        } finally {
            chain();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean isChaining() {
        return getIntent().getBooleanExtra(IS_CHAINING_KEY, false);
    }

    @Override
    public void onChain() {
        startNextChain(this, this::startService);
    }

    @Override
    public void postChain() {
        if (shouldKillAfterChain()) {
            if (DEBUG) {
                Log.d(getTag(), "postChain : Kill myself!");
            }
            stopSelf();
            Utils.killMyself();
        }
    }

    @Override
    public boolean shouldKillAfterChain() {
        return getIntent().getBooleanExtra(KILL_AFTER_KEY, false);
    }

    private Intent getIntent() {
        return intent;
    }

    private void setIntent(Intent intent) {
        this.intent = intent;
    }
}