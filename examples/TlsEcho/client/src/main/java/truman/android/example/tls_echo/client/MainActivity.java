package truman.android.example.tls_echo.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.net.Socket;

import truman.android.example.tls_echo.common.Callback;
import truman.android.example.tls_echo.common.Message;

public class MainActivity extends AppCompatActivity implements Ui.Out {

    private static final String TAG ="MainActivity.2ruman";
    private static final boolean MUTE_ERROR = true;
    private EditText etInput;
    private TextView tvStatus;
    private Settings settings;

    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate()");

        settings = Settings.getInstance(this);

        initViews();
        initClient();

        requestIgnoreBatteryOpt();
    }

    private void initViews() {
        findViewById(R.id.btn_send).setOnClickListener((v) -> send());
        findViewById(R.id.btn_conn).setOnClickListener((v) -> connect());
        findViewById(R.id.btn_close).setOnClickListener((v) -> close());

        etInput = findViewById(R.id.et_input);

        tvStatus = findViewById(R.id.tv_status);
        tvStatus.setMovementMethod(new ScrollingMovementMethod());

        Ui.setOut(this);
    }

    private void initClient() {
        client = new Client(this,
                settings.getServerIp(), settings.getServerPort(), settings.getKsPassword(), new Callback() {
            @Override
            public void onConnected(Socket socket) {
                println("Connected to server: " + socket.getRemoteSocketAddress());
            }

            @Override
            public void onError(Exception e) {
                if (!MUTE_ERROR) {
                    println("Error detected: " + e);
                }
            }

            @Override
            public void onMessage(Message m) {
                println("Message echoed: " + m);
            }
        });
    }

    private void send() {
        String message = etInput.getText().toString();
        ensureConnection();
        client.send(message);
    }

    private void connect() {
        client.open();
    }

    private void close() {
        client.close();
    }

    private void ensureConnection() {
        client.open().await();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.close();
    }

    @Override
    public void print(String s) {
        runOnUiThread(() -> tvStatus.append(nullSafe(s)));
    }

    @Override
    public void println(String s) {
        runOnUiThread(() -> tvStatus.append(nullSafe(s) + System.lineSeparator()));
    }

    @Override
    public void clear() {
    }

    private static String nullSafe(String s) {
        return s != null ? s : "";
    }

    @SuppressLint("BatteryLife")
    public void requestIgnoreBatteryOpt() {
        PowerManager pm= (PowerManager) getSystemService(Context.POWER_SERVICE);
        String packageName = getPackageName();
        if (pm.isIgnoringBatteryOptimizations(packageName)) {
            println("Already ignoring battery optimizations: " + packageName);
        } else {
            println("Request for ignoring battery optimizations: " + packageName);
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }

    }
}