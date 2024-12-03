package truman.android.example.avoid_crash_by_signal;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "MainActivity" +  TAG_SUFFIX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate()");

        Button invokeBtn = findViewById(R.id.btn_invoke);
        invokeBtn.setOnClickListener((v) -> invoke());

        Button invokeUnhandledBtn = findViewById(R.id.btn_invoke_unhandled);
        invokeUnhandledBtn.setOnClickListener((v) -> invokeUnhandled());
    }

    private void invoke() {
        int result = SystemCall.setUid(android.os.Process.myUid());
//        int result = SystemCall.setReuid(android.os.Process.myUid(),
//                                         android.os.Process.myUid());
//        int result = SystemCall.setResuid(android.os.Process.myUid(),
//                                          android.os.Process.myUid(),
//                                          android.os.Process.myUid());
//        int result = SystemCall.setResgid(android.os.Process.myUid(),
//                                          android.os.Process.myUid(),
//                                          android.os.Process.myUid());
        Log.d(TAG, "invoke() - result = " + result);
    }

    private void invokeUnhandled() {
        int result = SystemCall.setUidUnhandled(android.os.Process.myUid());
        Log.d(TAG, "invokeUnhandled() - result = " + result);
    }
}