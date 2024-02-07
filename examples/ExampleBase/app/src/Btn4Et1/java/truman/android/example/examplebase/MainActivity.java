package truman.android.example.examplebase;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 */
public class MainActivity extends AppCompatActivity implements Ui.Out {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "MainActivity" +  TAG_SUFFIX;

    private EditText mEtInput;
    private TextView mTvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate()");

        Button setupBtn = findViewById(R.id.btn_setup);
        setupBtn.setOnClickListener((v) -> setup());
        Button funcBtn = findViewById(R.id.btn_func);
        funcBtn.setOnClickListener((v) -> func());
        Button runBtn = findViewById(R.id.btn_run);
        runBtn.setOnClickListener((v) -> run());
        Button resetBtn = findViewById(R.id.btn_reset);
        resetBtn.setOnClickListener((v) -> reset());

        mEtInput = findViewById(R.id.et_input);

        mTvStatus = findViewById(R.id.tv_status);
        mTvStatus.setMovementMethod(new ScrollingMovementMethod());

        Ui.setOut(this);
    }

    private void setup() {
        Log.d(TAG, "setup() - Inside");
        println("Set up tester");
    }

    private void reset() {
        Log.d(TAG, "reset() - Inside");
        clear();
    }

    private void func() {
        Log.d(TAG, "func() - Inside");
        String text = mEtInput.getText().toString();
        println("You input {" + System.lineSeparator() + "\t\t" + text + System.lineSeparator() + "}");
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
        runOnUiThread(() -> {
            mEtInput.setText("");
            mTvStatus.setText("");
        });
    }

    private static String nullSafe(String s) {
        return s != null ? s : "";
    }
}
