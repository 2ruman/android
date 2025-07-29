package truman.android.example.example_base;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
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
        binding.btnRun.setOnClickListener((v) -> run());
        binding.btnReset.setOnClickListener((v) -> reset());
        binding.tvStatus.setMovementMethod(new ScrollingMovementMethod());
    }

    private void run() {
        Log.d(TAG, "run() - Inside");

        print("This is ");
        printColored("RED", Color.RED);
        println();

        print("This is ");
        printStyled("BOLD", Typeface.BOLD);
        println();

        print("This is ");
        printStyled("BOLD_ITALIC", Typeface.BOLD_ITALIC);
        println();

        print("This is ");
        printUnderlined("UNDERLINED");
        println();

        Ui.print("This is ");
        Ui.print("GREEN_ITALIC", Color.GREEN, Typeface.ITALIC, false);
        Ui.println();

        print("This is ");
        Ui.print("BLUE_BOLD", Color.BLUE, Typeface.BOLD, false);
        println();

        print("This is ");
        Ui.print("MAGENTA_BOLD_ITALIC_UNDERLINED", Color.MAGENTA, Typeface.BOLD_ITALIC, true);
        println();
    }

    private void reset() {
        Log.d(TAG, "reset() - Inside");
        clear();
    }

    private void printColored(String s, int color) {
        print(s, color, Typeface.NORMAL, false);
    }

    private void printStyled(String s, int style) {
        print(s, Color.BLACK, style, false);
    }

    private void printUnderlined(String s) {
        print(s, Color.BLACK, Typeface.NORMAL, true);
    }

    @Override
    public void print(String s, int color, int style, boolean underline) {
        if (s == null || s.isEmpty()) {
            return;
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder(s);
        ssb.setSpan(new StyleSpan(style), 0, s.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new ForegroundColorSpan(color), 0, s.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (underline) {
            ssb.setSpan(new UnderlineSpan(), 0, s.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        runOnUiThread(() -> binding.tvStatus.append(ssb));
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
