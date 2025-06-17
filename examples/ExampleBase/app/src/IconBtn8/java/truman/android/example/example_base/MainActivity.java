package truman.android.example.example_base;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.d(TAG, "onCreate()");

        initViews();
        Ui.setOut(this);
    }

    private void initViews() {
        binding.btnStart.setOnClickListener((v) -> start());
        binding.btnStop.setOnClickListener((v) -> stop());
        binding.btnReset.setOnClickListener((v) -> reset());
        binding.btnSettings.setOnClickListener((v) -> settings());

        binding.btnSync.setOnClickListener((v) -> sync());
        binding.btnUpload.setOnClickListener((v) -> upload());
        binding.btnRun.setOnClickListener((v) -> run());
        binding.btnTest.setOnClickListener((v) -> test());
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

    private void reset() {
        Log.d(TAG, "reset() - Inside");
        clear();
    }

    private void settings() {
        Log.d(TAG, "settings() - Inside");
        println("Open settings");
    }

    private void sync() {
        Log.d(TAG, "sync() - Inside");
        println("Sync");
    }

    private void upload() {
        Log.d(TAG, "upload() - Inside");
        println("Upload");
    }

    private void run() {
        Log.d(TAG, "run() - Inside");
        println("Run");
    }

    private void test() {
        Log.d(TAG, "test() - Inside");
        println("Test");
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
