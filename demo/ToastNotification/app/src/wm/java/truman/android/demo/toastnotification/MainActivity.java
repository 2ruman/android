package truman.android.demo.toastnotification;

import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import truman.android.demo.toastnotification.databinding.ActivityMainBinding;
import truman.android.demo.util.TN;

public class MainActivity extends AppCompatActivity {

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

        ensurePermissions();
        initViews();
    }

    private void ensurePermissions() {
        if (BuildConfig.FLAVOR != "wm") {
            return;
        }
        if (!TN.canDrawOverlays(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Required")
                    .setMessage("This app requires 'Display over other apps' permission to show notifications.")
                    .setPositiveButton("Settings", (dialog, which) -> TN.requestOverlayPermission(this))
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    private void initViews() {
        RadioGroup rgMode = binding.rgMode;
        rgMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.rbSlideUp.getId()) {
                TN.setMode(TN.Mode.SLIDE_UP);
            } else if (checkedId == binding.rbImmediate.getId()) {
                TN.setMode(TN.Mode.IMMEDIATE);
            }
        });

        binding.btnDebug.setOnClickListener(v -> TN.d(this, "This is a debug message"));
        binding.btnError.setOnClickListener(v -> TN.e(this, "An error has occurred!"));
        binding.btnWarning.setOnClickListener(v -> TN.w(this, "This is a warning message..."));
        binding.btnInfo.setOnClickListener(v -> TN.i(this, "Here is some Information"));
        binding.btnSuccess.setOnClickListener(v -> TN.s(this, "Operation successful!"));
        binding.btnCritical.setOnClickListener(v -> TN.c(this, "Critical failure!"));
    }
}
