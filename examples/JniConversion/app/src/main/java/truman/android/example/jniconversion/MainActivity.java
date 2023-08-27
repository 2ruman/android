package truman.android.example.jniconversion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 * Version : 1.0.1
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "MainActivity" +  TAG_SUFFIX;

    private Button mBtnRun;
    private TextView mTvStatus;
    private MyImpl mMyImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate()");

        mBtnRun = findViewById(R.id.btn_run);
        mBtnRun.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                run();
            }
        });
        mTvStatus = findViewById(R.id.tv_status);
        mTvStatus.setMovementMethod(new ScrollingMovementMethod());

        mMyImpl = new MyImpl();
    }

    private void run() {
        Log.d(TAG, "run() - Inside");
        mMyImpl.test();
    }
}
