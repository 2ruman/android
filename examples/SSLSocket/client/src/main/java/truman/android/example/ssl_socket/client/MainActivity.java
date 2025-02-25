package truman.android.example.ssl_socket.client;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.security.KeyStore;

public class MainActivity extends AppCompatActivity implements Ui.Out {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "MainActivity" +  TAG_SUFFIX;

    private TextView mTvStatus;
    private TlsClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.d(TAG, "onCreate()");

        Button runBtn = findViewById(R.id.btn_run);
        runBtn.setOnClickListener((v) -> run());
        Button resetBtn = findViewById(R.id.btn_reset);
        resetBtn.setOnClickListener((v) -> reset());

        mTvStatus = findViewById(R.id.tv_status);
        mTvStatus.setMovementMethod(new ScrollingMovementMethod());

        Ui.setOut(this);
        Ui.println("TLS Client");
    }

    private void connectToServer() {
        println("connectToServer()");
        if (client == null) {
            client = new TlsClient(getApplicationContext(), "192.168.123.104", 12345, this);
            client.setKsPassword("123456");
            client.connect();
        } else {
            println("Client already exists");
        }
    }

    private void run() {
        Log.d(TAG, "run() - Inside");

        println("Default type is " + KeyStore.getDefaultType());

        connectToServer();
    }

    private void reset() {
        Log.d(TAG, "reset() - Inside");
        if (client != null) {
            client.close();
            client = null;
        }
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