package truman.android.example.dummyprocessgen.act;

import static androidx.constraintlayout.widget.ConstraintSet.PARENT_ID;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import truman.android.example.dummyprocessgen.R;
import truman.android.example.dummyprocessgen.common.ChainableDummy;
import truman.android.example.dummyprocessgen.common.Utils;

public class DummyActivity extends AppCompatActivity implements ChainableDummy {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(getTag(), "onCreate()");

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dummy);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dummy), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        chain();
    }

    private void initViews() {
        ViewGroup root = findViewById(R.id.dummy);
        if (root == null) {
            return;
        }
        TextView tv = new TextView(this);
        ConstraintLayout.LayoutParams tvParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvParams.startToStart = PARENT_ID;
        tvParams.endToEnd = PARENT_ID;
        tvParams.topToTop = PARENT_ID;
        tvParams.bottomToBottom = PARENT_ID;
        tv.setLayoutParams(tvParams);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setText(this.getClass().getSimpleName());

        root.addView(tv);
    }

    @Override
    public boolean isChaining() {
        return getIntent().getBooleanExtra(IS_CHAINING_KEY, false);
    }

    @Override
    public void onChain() {
        startNextChain(this, this::startActivity);
    }

    @Override
    public void postChain() {
        if (shouldKillAfterChain()) {
            if (DEBUG) {
                Log.d(getTag(), "postChain : Kill myself!");
            }
            finish();
            Utils.killMyself();
        }
    }

    @Override
    public boolean shouldKillAfterChain() {
        return getIntent().getBooleanExtra(KILL_AFTER_KEY, false);
    }
}