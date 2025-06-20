package truman.android.example.sob_service.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.function.Consumer;

import truman.android.example.sob_service.R;
import truman.android.example.sob_service.databinding.ActivityMainBinding;
import truman.android.example.sob_service.permission.PermissionManager;
import truman.android.example.sob_service.service.MyService;

/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 */
public class MainActivity extends AppCompatActivity implements Ui.Out {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "MainActivity" +  TAG_SUFFIX;

    private ActivityMainBinding binding;
    private PermissionManager pm;

    private volatile int mClickCount = 0; // Test val

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

        initVars();
        initViews();

        Ui.setOut(this);
    }

    private void initVars() {
        pm = new PermissionManager(this);
    }

    private void initViews() {
        binding.btnRun.setOnClickListener((v) -> run());
        binding.btnReset.setOnClickListener((v) -> reset());
        binding.tvStatus.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        enforceAllRequirements();

        checkReadiness(isReady -> {
            if (isReady) {
                startMyService();
                Ui.println("Start service");
            } else {
                Ui.println("Unable to start service");
            }
        });
    }

    private void enforceAllRequirements() {
        if (!pm.checkBatteryOptimizations()) {
            pm.enforceIgnoringBatteryOptimizations();
        } else if (!pm.checkPostNotificationsPermission()) {
            pm.enforcePostNotificationsPermission();
        }
    }

    private void checkReadiness(Consumer<Boolean> readinessConsumer) {
        boolean isReady = pm.checkBatteryOptimizations() && pm.checkPostNotificationsPermission();
        readinessConsumer.accept(isReady);
    }

    private void startMyService() {
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        startService(intent);
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
