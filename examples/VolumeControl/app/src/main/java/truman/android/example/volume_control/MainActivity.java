package truman.android.example.volume_control;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private AudioManager mAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initViews();
    }

    private void init() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    private void initViews() {
        Button btnUp = findViewById(R.id.btnUp);
        Button btnDown = findViewById(R.id.btnDown);

        btnUp.setOnClickListener((v) -> raiseMediaVolume());
        btnDown.setOnClickListener((v) -> lowerMediaVolume());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setAutoSizeText(this, btnUp);
            setAutoSizeText(this, btnDown);
        }
    }

    private void raiseMediaVolume() {
        mAudioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    }

    private void lowerMediaVolume() {
        mAudioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
    }

    private void setAutoSizeText(Context context, TextView textView) {
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                textView,
                (int) context.getResources().getDimension(R.dimen.auto_size_min_text_size),
                (int) context.getResources().getDimension(R.dimen.auto_size_max_text_size),
                (int) context.getResources().getDimension(R.dimen.auto_size_step_granularity),
                TypedValue.COMPLEX_UNIT_PX
        );
    }
}
