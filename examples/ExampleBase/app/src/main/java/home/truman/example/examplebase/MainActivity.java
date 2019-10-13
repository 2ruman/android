package home.truman.example.examplebase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "MainActivity" +  TAG_SUFFIX;
    private static final int MSG_UPDATE_TV = 1;

    private Button mBtnRun;
    private Button mBtnReset;
    private TextView mTvStatus;

    private int mClickedCnt = 0;

    private final Handler mUIHandler = new UIHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate() - Inside");

        mBtnRun = (Button) findViewById(R.id.btn_run);
        mBtnRun.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                run();
            }
        });
        mBtnReset = (Button) findViewById(R.id.btn_reset);
        mBtnReset.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });
        mTvStatus = (TextView) findViewById(R.id.tv_status);
    }

    private void run() {
        Log.d(TAG, "run() - Inside");
        updateTv("Run button was clicked("+(++mClickedCnt)+")\n");
    }

    private void reset() {
        Log.d(TAG, "reset() - Inside");
        updateTv("Reset button was clicked("+(mClickedCnt=0)+")\n");
    }

    private void updateTv(String text) {
        mUIHandler.sendMessage(
                mUIHandler.obtainMessage(MSG_UPDATE_TV, text));
    }

    private void handleUpdateTv(String text) {
        if (text == null)
            return;
        mTvStatus.setText(text);
    }

    private static class UIHandler extends Handler {
        private final String TAG = "UIHandler" + TAG_SUFFIX;
        private final WeakReference<MainActivity> mActivity;

        UIHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity == null) {
                Log.e(TAG, "MainActivity is not available");
                return;
            }

            switch (msg.what) {
                case MSG_UPDATE_TV:
                    String text = msg.obj == null ?
                            "null" : msg.obj.toString();
                    activity.handleUpdateTv(text);
                    break;
                default:
                    Log.e(TAG, "Invalid message");
                    break;
            }
        }
    }



}
