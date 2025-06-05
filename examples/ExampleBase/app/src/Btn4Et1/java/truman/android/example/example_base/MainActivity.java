package truman.android.example.example_base;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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
        binding.btnSetup.setOnClickListener((v) -> setup());
        binding.btnFunc.setOnClickListener((v) -> func());
        binding.btnRun.setOnClickListener((v) -> run());
        binding.btnReset.setOnClickListener((v) -> reset());
        binding.tvStatus.setMovementMethod(new ScrollingMovementMethod());
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
        String text = binding.etInput.getText().toString();
        println("You input {" + System.lineSeparator() + "\t\t" + text + System.lineSeparator() + "}");
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
        runOnUiThread(() -> {
            binding.etInput.setText("");
            binding.etInput.clearFocus();
            hideKeyboard(binding.etInput);
            binding.tvStatus.setText("");
        });
    }

    private static String nullSafe(String s) {
        return s != null ? s : "";
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
