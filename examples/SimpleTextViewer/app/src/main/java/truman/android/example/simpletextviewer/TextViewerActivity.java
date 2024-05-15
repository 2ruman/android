package truman.android.example.simpletextviewer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

public class TextViewerActivity extends AppCompatActivity {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = MainActivity.class.getSimpleName() +  TAG_SUFFIX;

    public static final String EXTRA_CONTENT = "content";
    private static final String EXTRA_SCROLL_Y = "scroll_y";

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_viewer);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Log.d(TAG, "onCreate()");

        mTextView = findViewById(R.id.tv_text);
        mTextView.setMovementMethod(new ScrollingMovementMethod());

        String text = getIntent().getStringExtra(EXTRA_CONTENT);

        int scrollY = 0;
        if (savedInstanceState != null) {
            scrollY = savedInstanceState.getInt(EXTRA_SCROLL_Y);
        }
        updateView(text, scrollY);
    }

    private void updateView(String text, int scrollY) {
        runOnUiThread(() -> {
            mTextView.setText(text);
            mTextView.scrollTo(0, scrollY);
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState()");

        int scrollY = mTextView.getScrollY();
        outState.putInt(EXTRA_SCROLL_Y, scrollY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }
}