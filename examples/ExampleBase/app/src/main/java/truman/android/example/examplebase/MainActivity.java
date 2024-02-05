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
 * Version : 1.1.0
 */
public class MainActivity extends AppCompatActivity implements Ui.Out {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "MainActivity" +  TAG_SUFFIX;

    private TextView mTvStatus;

    private volatile int mClickCount = 0; // Test val

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate()");

        Button runBtn = findViewById(R.id.btn_run);
        runBtn.setOnClickListener((v) -> run());
        Button resetBtn = findViewById(R.id.btn_reset);
        resetBtn.setOnClickListener((v) -> reset());

        mTvStatus = findViewById(R.id.tv_status);
        mTvStatus.setMovementMethod(new ScrollingMovementMethod());

        Ui.setOut(this);
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
