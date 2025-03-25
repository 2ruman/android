package truman.android.example.fg_service;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements Ui.Out {

    private TextView mTvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_run).setOnClickListener((v) -> run());
        findViewById(R.id.btn_reset).setOnClickListener((v) -> reset());

        mTvStatus = findViewById(R.id.tv_status);
        mTvStatus.setMovementMethod(new ScrollingMovementMethod());

        Ui.setOut(this);

        requestPermissionIfRequired();
    }

    private void requestPermissionIfRequired() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                !Utils.checkPermission(this, Manifest.permission.POST_NOTIFICATIONS)) {

            println("Request post-notification permission(SDK : " + Build.VERSION.SDK_INT + ")");

            Utils.requestPermission(this, Manifest.permission.POST_NOTIFICATIONS,
                    (isGranted) -> {
                        println("Permission " + (isGranted ? "granted!" : "denied..."));
                    });
        }
    }
    private void run() {
        println("Start service");
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        startService(intent);
    }

    private void reset() {
        println("Stop service");
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        stopService(intent);
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