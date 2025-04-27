package truman.android.example.settings_activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName() + ".2ruman";

    private ActivityResultLauncher<Intent> settingsActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initVars();
        initActionBar();
    }

    private void initVars() {
        settingsActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == MainActivity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            onPreferenceChanged(data.getStringArrayListExtra("keys"));
                        }
                    }
        });
    }

    private void initActionBar() {
        setSupportActionBar(findViewById(R.id.main_toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setIcon(R.drawable.ic_android);
            actionBar.setTitle("   Main");
        }
    }

    private void onPreferenceChanged(ArrayList<String> keys) {
        if (keys == null) {
            return;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        for (String key : keys) {
            onPreferenceChanged(sp, key);
        }
    }

    private void onPreferenceChanged(SharedPreferences sp, String key) {
        Object valueObj = sp.getAll().get(key);
        String valueStr;
        if (valueObj instanceof String) {
            valueStr = String.valueOf(valueObj);
        } else if (valueObj instanceof Boolean) {
            valueStr = String.valueOf(valueObj);
        } else {
            valueStr = "Not Found";
        }
        Log.d(TAG, "onPreferenceChanged - Changed preference = " +
                "[" + key + ", " + valueStr + "]");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.menu_settings);
        Drawable icon = item.getIcon();
        if (icon != null) {
            icon.mutate();
            icon.setTintList(null);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_settings) {
            startSettingsActivity();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        settingsActivityLauncher.launch(intent);
    }
}