package home.truman.demo.sdpdatabasedemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 * Version : 1.1.1
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity" +  AppConstants.TAG_SUFFIX;

    private SdpManager mSdpManager;
    private SdpDbHandler mSdpDbHandler;
    private PermissionManager mPermissionManager;

    private final Handler mUIHandler = new UIHandler(this);
    private final Handler mWorkHandler = new WorkHandler(this);

    private BroadcastReceiver mReceiver;

    private Button mBtnLock;
    private Button mBtnUnlock;
    private TextView mTvState;
    private TextView mTvStatus;

    private Button mBtnInsert;
    private Button mBtnSelect;
    private Button mBtnDelete;
    private Button mBtnDump;
    private Button mBtnGo;
    private Button mBtnStop;
    private EditText mEtDataNum;
    private EditText mEtInterval;

    private long mIntervalTime = AppConstants.DEFAULT_INTERVAL_TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate()");

        android.os.Process.myTid();
        if (!init()) {
            Log.e(TAG, "onCreate() - Failed to initialize app");
            Toast.makeText(this.getApplicationContext(),
                    "Failed to initialize app", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        unregisterReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        if (mSdpDbHandler != null) {
            mSdpDbHandler.close();
        }
    }

    private boolean init() {
        mSdpManager = new SdpManager();
        if (!mSdpManager.init()) {
            return false;
        }

        mSdpDbHandler = new SdpDbHandler(this);

        mPermissionManager = new PermissionManager(this);
        if (!mPermissionManager.checkStoragePermission()) {
            mPermissionManager.requestStoragePermission();
        }

        mBtnLock = (Button) findViewById(R.id.btn_lock);
        mBtnLock.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                lock();
            }
        });

        mBtnUnlock = (Button) findViewById(R.id.btn_unlock);
        mBtnUnlock.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                unlock();
            }
        });

        mEtDataNum = (EditText) findViewById(R.id.et_data_num);

        mBtnInsert = (Button) findViewById(R.id.btn_insert);
        mBtnInsert.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                insert();
            }
        });

        mBtnSelect = (Button) findViewById(R.id.btn_select);
        mBtnSelect.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                select();
            }
        });

        mBtnDelete = (Button) findViewById(R.id.btn_delete);
        mBtnDelete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete();
            }
        });

        mBtnDump = (Button) findViewById(R.id.btn_dump);
        mBtnDump.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dump();
            }
        });

        mEtInterval = (EditText) findViewById(R.id.et_interval);

        mBtnGo = (Button) findViewById(R.id.btn_go);
        mBtnGo.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                go();
            }
        });

        mBtnStop = (Button) findViewById(R.id.btn_stop);
        mBtnStop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop();
            }
        });
        mBtnStop.setEnabled(false);

        mTvState = (TextView) findViewById(R.id.tv_state);
        updateState();

        mTvStatus = (TextView) findViewById(R.id.tv_status);

        return true;
    }

    private void traceResult(String op, boolean res) {
        String msg = "" + op + " - " + AppUtils.resultToStr(res);
        Log.e(TAG, msg);
        appendStatus(AppUtils.getCurrentDate() + " " + msg);
    }

    private void traceResult(String op, String res) {
        String msg = "" + op + " - " + res;
        Log.e(TAG, msg);
        appendStatus(AppUtils.getCurrentDate() + " " + msg);
    }

    private void lock() {
        boolean result = mSdpManager.lock();
        traceResult("lock", result);
        updateState();
    }

    private void unlock() {
        boolean result = mSdpManager.unlock();
        traceResult("unlock", result);
        updateState();
    }

    private void insert() {
        int insertCnt = 0;
        try {
            insertCnt = Integer.parseInt(mEtDataNum.getText().toString());
        } catch(Exception e) {
            Log.e(TAG, "Failed to get data number... maybe due to number format exception");
            // e.printStackTrace();
        }
        if (insertCnt < 1 || insertCnt > AppConstants.MAX_INSERT_DATA_NUM) {
            insertCnt = AppConstants.DEFAULT_INSERT_DATA_NUM;
        }
        mSdpDbHandler.inserts(insertCnt);
        traceResult("insert", "# "+ insertCnt + " of data inserted!");
    }

    private void select() {
        int selectCnt = mSdpDbHandler.select();
        traceResult("select", "# "+ selectCnt + " of data selected!");
    }

    private void delete() {
        int deleteCnt = mSdpDbHandler.delete();
        traceResult("delete", "# "+ deleteCnt + " of data deleted!");
    }

    private void close() {
        mSdpDbHandler.close();
        traceResult("close","done!");
    }

    private void dump() {
        mWorkHandler.sendEmptyMessage(WorkHandler.MSG_DUMP_NOW);
    }

    private void handleDumpNow() {
        handleBeforeDump();
        mSdpDbHandler.export();
        handleAfterDump();
        traceResult("dump","done!");
    }

    private void handleBeforeDump() {
        Log.d(TAG, "handleBeforeDump() - Disable all functions");
        mWorkHandler.removeMessages(WorkHandler.MSG_AFTER_GO);

        mEtDataNum.setEnabled(false);
        mBtnInsert.setEnabled(false);
        mBtnSelect.setEnabled(false);
        mBtnDelete.setEnabled(false);
        mBtnDump.setEnabled(false);
        mEtInterval.setEnabled(false);
        mBtnGo.setEnabled(false);
        mBtnStop.setEnabled(false);

        Log.d(TAG, "handleBeforeDump() - Select db for sync");
        select();
        Log.d(TAG, "handleBeforeDump() - close db for sync");
        // close();
    }

    private void handleAfterDump() {
        Log.d(TAG, "handleBeforeDump() - Disable all functions");

        mEtDataNum.setEnabled(true);
        mBtnInsert.setEnabled(true);
        mBtnSelect.setEnabled(true);
        mBtnDelete.setEnabled(true);
        mBtnDump.setEnabled(true);
        mEtInterval.setEnabled(true);
        mBtnGo.setEnabled(true);
        mBtnStop.setEnabled(true);
    }

    private void go() {
        beforeGo();
    }

    private void beforeGo() {
        mWorkHandler.sendEmptyMessage(WorkHandler.MSG_BEFORE_GO);
    }

    private void handleUpdateIntervalTime() {
        try {
            mIntervalTime = Integer.parseInt(mEtInterval.getText().toString());
        } catch(Exception e) {
            Log.e(TAG, "Failed to get interval time... maybe due to number format exception");
            // e.printStackTrace();
        }
        if (mIntervalTime < 1 || mIntervalTime > AppConstants.MAX_INTERVAL_TIME) {
            mIntervalTime = AppConstants.DEFAULT_INTERVAL_TIME;
        }
        Log.d(TAG, "handleUpdateIntervalTime() - Interval Time(sec) : " + mIntervalTime);
    }

    private void handleBeforeGo() {
        Log.d(TAG, "handleBeforeGo() - Disable all functions without go");
        handleDisableFunctions();
        handleUpdateIntervalTime();
        handleAfterGo();
    }

    private void handleDisableFunctions() {
        Log.d(TAG, "handleDisableFunctions() - Disable all functions without go");

        mEtDataNum.setEnabled(false);
        mBtnInsert.setEnabled(false);
        mBtnSelect.setEnabled(false);
        mBtnDelete.setEnabled(false);
        mBtnDump.setEnabled(false);
        mEtInterval.setEnabled(false);
        mBtnGo.setEnabled(false);
        mBtnStop.setEnabled(true);
    }

    private void handleAfterGo() {
        insert();
        Log.d(TAG, "handleAfterGo() - Next insert scheduled after " + mIntervalTime + " sec");
        mWorkHandler.sendEmptyMessageDelayed(WorkHandler.MSG_AFTER_GO, mIntervalTime * 1000);
    }

    private void stop() {
        mWorkHandler.sendEmptyMessage(WorkHandler.MSG_STOP_NOW);
    }

    private void handleStopNow() {
        mWorkHandler.removeMessages(WorkHandler.MSG_AFTER_GO);
        handleEnableFunctions();
    }

    private void handleEnableFunctions() {
        Log.d(TAG, "handleEnableFunctions() - Enable all functions without stop");

        mEtDataNum.setEnabled(true);
        mBtnInsert.setEnabled(true);
        mBtnSelect.setEnabled(true);
        mBtnDelete.setEnabled(true);
        mBtnDump.setEnabled(true);
        mEtInterval.setEnabled(true);
        mBtnGo.setEnabled(true);
        mBtnStop.setEnabled(false);
    }

    private void updateState() {
        mUIHandler.sendMessage(
                mUIHandler.obtainMessage(UIHandler.MSG_UPDATE_STATE));
    }

    private void handleUpdateState() {
        int color = ContextCompat.getColor(this, R.color.colorWhite);
        String state = getString(R.string.na);

        if (mSdpManager.getState() ==  SdpManager.STATE_LOCKED) {
            color = ContextCompat.getColor(this, R.color.colorRed);
            state = getString(R.string.locked);
        } else if (mSdpManager.getState() ==  SdpManager.STATE_UNLOCKED) {
            color = ContextCompat.getColor(this, R.color.colorGreen);
            state = getString(R.string.unlocked);
        }
        mTvState.setBackgroundColor(color);
        mTvState.setText(state);
    }

    private void appendStatus(String text) {
        mUIHandler.sendMessage(
                mUIHandler.obtainMessage(UIHandler.MSG_APPEND_STATUS, text));
    }

    private void handleAppendStatus(String text) {
        if (text == null)
            return;
        mTvStatus.append(text);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (mPermissionManager != null) {
            mPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private static class UIHandler extends Handler {

        private final String TAG = "UIHandler" + AppConstants.TAG_SUFFIX;
        private final WeakReference<MainActivity> mActivity;

        static final int MSG_UPDATE_STATE  = 1;
        static final int MSG_APPEND_STATUS = 2;

        UIHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity == null) {
                Log.e(TAG, "MainActivity is not available");
                return;
            }

            switch (msg.what) {
                case MSG_UPDATE_STATE:
                    activity.handleUpdateState();
                    break;
                case MSG_APPEND_STATUS:
                    String text = msg.obj == null ?
                            "null/n" : msg.obj.toString() + "\n";
                    activity.handleAppendStatus(text);
                    break;
                default:
                    Log.e(TAG, "Invalid message");
                    break;
                }
        }
    }

    private static class WorkHandler extends Handler {

        private final String TAG = "WorkHandler" + AppConstants.TAG_SUFFIX;
        private final WeakReference<MainActivity> mActivity;

        static final int MSG_BEFORE_GO  = 3;
        static final int MSG_AFTER_GO   = 4;
        static final int MSG_STOP_NOW   = 5;
        static final int MSG_DUMP_NOW   = 6;

        WorkHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity == null) {
                Log.e(TAG, "MainActivity is not available");
                return;
            }

            switch (msg.what) {
                case MSG_BEFORE_GO:
                    activity.handleBeforeGo();
                    break;
                case MSG_AFTER_GO :
                    activity.handleAfterGo();
                    break;
                case MSG_STOP_NOW:
                    activity.handleStopNow();
                    break;
                case MSG_DUMP_NOW:
                    activity.handleDumpNow();
                    break;
                default:
                    Log.e(TAG, "Invalid message");
                    break;
            }
        }
    }

    private void registerReceiver() {
        if (mReceiver != null) {
            return;
        }

        Log.d(TAG, "registerReceiver()");

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SdpManager.ACTION_SDP_STATE_CHANGED);
        intentFilter.addAction(SdpManager.ACTION_SDP_REMOVE_ENGINE);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (SdpManager.ACTION_SDP_STATE_CHANGED.equals(intent.getAction())) {
                    Log.d(TAG, "onReceive() - ACTION_SDP_STATE_CHANGED");
                    int engineId = intent.getIntExtra(SdpManager.EXTRA_SDP_ENGINE_ID, -1);
                    int engineState = intent.getIntExtra(SdpManager.EXTRA_SDP_ENGINE_STATE, SdpManager.STATE_NA);
                    if (engineId >= 0 && mSdpManager.getId() == engineId) {
                        Log.d(TAG, "onReceive - Engine ID    : " + engineId);
                        Log.d(TAG, "onReceive - Engine State : " + engineState);

                        boolean result = mSdpManager.updateStateToDB(mSdpDbHandler.getDB(), engineState);
                        Log.d(TAG, "onReceive() - updateStateToDB() : " + result);
                    } else {
                        Log.d(TAG, "onReceive - Invalid Engine ID : " + engineId);
                    }
                } else if (SdpManager.ACTION_SDP_REMOVE_ENGINE.equals(intent.getAction())) {
                    Log.d(TAG, "onReceive() - ACTION_SDP_REMOVE_ENGINE");
                    boolean result = mSdpManager.remove();
                    Log.d(TAG, "onReceive - engine removed! : " + result);
                }
            }
        };
        registerReceiver(mReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        if (mReceiver != null) {
            Log.d(TAG, "unregisterReceiver()");

            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }
}
