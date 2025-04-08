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

    private volatile int mClickCount = 0; // Test val

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
        binding.btnRun.setOnClickListener((v) -> run());
        binding.btnReset.setOnClickListener((v) -> reset());
        binding.tvStatus.setMovementMethod(new ScrollingMovementMethod());
    }

    private void run() {
        Log.d(TAG, "run() - Inside");
        mClickCount++;
        println("Click count("+(mClickCount)+")");
    }

    private void reset() {
        Log.d(TAG, "reset() - Inside");
        mClickCount = 0;
        clear();
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
