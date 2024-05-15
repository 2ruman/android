package truman.android.example.remoteservice.client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import truman.android.example.remoteservice.IMyListener;
import truman.android.example.remoteservice.MyData;

public class MainActivity extends AppCompatActivity implements Ui.Out {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "MainActivity" + TAG_SUFFIX;

    private ProgressBar mPbProgress;
    private TextView mTvStatus;
    private MyManager mMyManager;

    private final IMyListener mMyListener = new IMyListener.Stub() {
        @Override
        public void onUpdate(int value) {
            println("Progress : " + value);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate()");

        findViewById(R.id.btn_bind).setOnClickListener((v) -> bind());
        findViewById(R.id.btn_unbind).setOnClickListener((v) -> unbind());
        findViewById(R.id.btn_get_version).setOnClickListener((v) -> getVersion());
        findViewById(R.id.btn_get_data).setOnClickListener((v) -> getData());
        findViewById(R.id.btn_reg_listener).setOnClickListener((v) -> registerListener());
        findViewById(R.id.btn_unreg_listener).setOnClickListener((v) -> unregisterListener());

        mPbProgress = findViewById(R.id.pb_progress);
        mPbProgress.setOnClickListener((v) -> v.setVisibility(View.INVISIBLE));
        mTvStatus = findViewById(R.id.tv_status);
        mTvStatus.setMovementMethod(new ScrollingMovementMethod());

        Ui.setOut(this);
        mMyManager = new MyManager(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        mMyManager.unbindService();
    }

    private void bind() {
        Log.d(TAG, "bind() - Inside");
        println("Bind to service");
        mMyManager.bindService();
    }

    private void unbind() {
        Log.d(TAG, "unbind() - Inside");
        println("Unbind from service");
        mMyManager.unbindService();
    }

    private void getVersion() {
        Log.d(TAG, "getVersion() - Inside");
        int version = mMyManager.getVersion(0);
        println("Service Version : " + version);
    }

    private void getData() {
        Log.d(TAG, "getData() - Inside");
        MyData md = mMyManager.getData();
        println("Data :" + "\n" + md);
    }

    private void registerListener() {
        Log.d(TAG, "registerListener() - Inside");
        boolean result = mMyManager.registerListener(mMyListener);
        println("Result of registering : " + result);
    }

    private void unregisterListener() {
        Log.d(TAG, "unregisterListener() - Inside");
        boolean result = mMyManager.unregisterListener();
        println("Result of unregistering : " + result);
    }

    @Override
    public void print(String s) {
        runOnUiThread(() -> mTvStatus.append(nullSafe(s)));
    }

    @Override
    public void println(String s) {
        runOnUiThread(() -> mTvStatus.append(nullSafe(s) + System.lineSeparator()));
    }

    @Override
    public void clear() {
        runOnUiThread(() -> mTvStatus.setText(""));
    }

    @Override
    public void startProgress() {
        runOnUiThread(() -> mPbProgress.setVisibility(View.VISIBLE));
    }

    @Override
    public void stopProgress() {
        runOnUiThread(() -> mPbProgress.setVisibility(View.INVISIBLE));
    }

    private static String nullSafe(String s) {
        return s != null ? s : "";
    }
}