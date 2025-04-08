package truman.android.example.example_base;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import truman.android.example.example_base.databinding.ActivityMainBinding;

/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 */
public class MainActivity extends AppCompatActivity implements Ui.Out {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "MainActivity" +  TAG_SUFFIX;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d(TAG, "onCreate()");

        initViews();
        Ui.setOut(this);
    }

    private void initViews() {
        binding.btnStart.setOnClickListener((v) -> start());
        binding.btnStop.setOnClickListener((v) -> stop());
        binding.btnSetup.setOnClickListener((v) -> setup());
        binding.btnReset.setOnClickListener((v) -> reset());
        binding.btnRun.setOnClickListener((v) -> run());
        binding.tvStatus.setMovementMethod(new ScrollingMovementMethod());
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
        runOnUiThread(() -> binding.tvStatus.append(nullSafe(s)));
    }

    @Override
    public void println(String s) {
        runOnUiThread(() -> binding.tvStatus.append(nullSafe(s) + System.lineSeparator()));
    }

    @Override
    public void clear() {
        runOnUiThread(() -> binding.tvStatus.setText(""));
    }

    private static String nullSafe(String s) {
        return s != null ? s : "";
    }
}
