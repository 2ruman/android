package truman.android.example.httpparser.ui;

import static truman.android.example.httpparser.http.HttpConstants.SERVER_PORT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import truman.android.example.httpparser.R;
import truman.android.example.httpparser.databinding.ActivityMainBinding;
import truman.android.example.httpparser.http.HttpServerCallback;
import truman.android.example.httpparser.http.Httpd;

/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 */
public class MainActivity extends AppCompatActivity implements Ui.Out {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "MainActivity" +  TAG_SUFFIX;

    private ActivityMainBinding binding;

    private Httpd httpd;

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
        binding.tvStatus.setMovementMethod(new ScrollingMovementMethod());
    }

    private void start() {
        if (httpd == null) {
            httpd = Httpd.create(SERVER_PORT, httpServerCallback);
            httpd.start();

            println("Server started: " + SERVER_PORT);
        }
    }

    private void stop() {
        if (httpd != null) {
            httpd.stop();
            httpd = null;

            println("Server stopped");
        }
    }

    private final HttpServerCallback httpServerCallback = new HttpServerCallback() {
        @Override
        public void onGet(Bundle request) {
            Log.d(TAG, "onGet() - request: " + request);
        }

        @Override
        public void onPost(Bundle request, byte[] buff) {
            Log.d(TAG, "onPost() - request: " + request + ", buff len: " + buff.length);
        }
    };

    @Override
    protected void onPostResume() {
        super.onPostResume();

        enforceAllRequirements();
    }

    private void enforceAllRequirements() {
        if (!isIgnoringBatteryOptimizations()) {
            requestIgnoreBatteryOpt();
        }
    }

    private boolean isIgnoringBatteryOptimizations() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        String packageName = getPackageName();
        return pm.isIgnoringBatteryOptimizations(packageName);
    }

    @SuppressLint("BatteryLife")
    public void requestIgnoreBatteryOpt() {
        if (!isIgnoringBatteryOptimizations()) {
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (httpd != null) {
            httpd.stop();
            httpd = null;
        }
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