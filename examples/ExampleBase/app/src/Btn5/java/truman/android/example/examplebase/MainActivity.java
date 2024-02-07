package truman.android.example.examplebase;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 */
public class MainActivity extends AppCompatActivity implements Ui.Out {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "MainActivity" +  TAG_SUFFIX;

    private TextView mTvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate()");

        Button startBtn = findViewById(R.id.btn_start);
        startBtn.setOnClickListener((v) -> start());
        Button stopBtn = findViewById(R.id.btn_stop);
        stopBtn.setOnClickListener((v) -> stop());
        Button setupBtn = findViewById(R.id.btn_setup);
        setupBtn.setOnClickListener((v) -> setup());
        Button resetBtn = findViewById(R.id.btn_reset);
        resetBtn.setOnClickListener((v) -> reset());
        Button runBtn = findViewById(R.id.btn_run);
        runBtn.setOnClickListener((v) -> run());

        mTvStatus = findViewById(R.id.tv_status);
        mTvStatus.setMovementMethod(new ScrollingMovementMethod());

        Ui.setOut(this);
    }

    private void start() {
        Log.d(TAG, "start() - Inside");
        println("Start service");
    }

    private void stop() {
        Log.d(TAG, "stop() - Inside");
        println("Stop service");
    }

    private void setup() {
        Log.d(TAG, "setup() - Inside");
        println("Set up tester");
    }

    private void reset() {
        Log.d(TAG, "reset() - Inside");
        clear();
    }

    private void run() {
        Log.d(TAG, "run() - Inside");
        println("Run tester");
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

    private static String nullSafe(String s) {
        return s != null ? s : "";
    }
}
