package truman.android.example.simpletextviewer;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = MainActivity.class.getSimpleName() +  TAG_SUFFIX;

    private TextView mTvStatus;

    private String mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate()");

        Button runBtn = findViewById(R.id.btn_gen_text);
        runBtn.setOnClickListener((v) -> generateText());
        Button resetBtn = findViewById(R.id.btn_open_viewer);
        resetBtn.setOnClickListener((v) -> openViewer());

        mTvStatus = findViewById(R.id.tv_status);
        mTvStatus.setMovementMethod(new ScrollingMovementMethod());
    }

    private void generateText() {
        Log.d(TAG, "generateText() - Inside");
        String genId = Integer.toString(new Random().nextInt());
        StringBuilder sb = new StringBuilder("ID : " + genId + "\n");
        final int lineNum = 1000;
        for (int i = 0; i < lineNum; i++) {
            sb.append(String.format("%03d ABCDEFGHIJKLMNOPQRSTUVWXYZ\n", i));
        }
        mText = sb.toString();
        println("Text generated(" + genId + ")");
    }

    private void openViewer() {
        Log.d(TAG, "openViewer() - Inside");
        Intent intent = new Intent(this, TextViewerActivity.class);
        intent.putExtra(TextViewerActivity.EXTRA_CONTENT, mText);
        startActivity(intent);
    }

    public void println(String s) {
        runOnUiThread(() -> mTvStatus.append(s + System.lineSeparator()));
    }
}
