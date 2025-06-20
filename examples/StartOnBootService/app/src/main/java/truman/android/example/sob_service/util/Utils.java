package truman.android.example.sob_service.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import java.util.function.Consumer;

public class Utils {

    private static final String APP_PREFERENCES_NAME = "AppPreferences";

    public static boolean getAppPreference(Context context, String key, boolean defaultVal) {
        return getAppPreferences(context).getBoolean(key, defaultVal);
    }

    public static void putAppPreference(Context context, String key, boolean value) {
        getAppPreferences(context).edit().putBoolean(key, value).apply();
    }

    private static SharedPreferences getAppPreferences(Context context) {
        return context.getSharedPreferences(APP_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static boolean checkPermission(Context context, String permission) {
        return (ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED);
    }

    public static boolean isPermissionDenied(Context context, String permission) {
        return (ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_DENIED);
    }

    public static ActivityResultLauncher<String> createRequestPermissionResultLauncher(
            ComponentActivity activity, Consumer<Boolean> onResult) {
        return activity.registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                onResult::accept);
    }

    public static void requestPermission(ActivityResultLauncher<String> launcher, String permission) {
        launcher.launch(permission);
    }
}
