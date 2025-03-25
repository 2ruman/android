package truman.android.example.fg_service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import java.util.function.Consumer;

public class Utils {

    public static boolean checkPermission(Context context, String permission) {
        return (ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED);
    }

    public static void requestPermission(ComponentActivity activity, String permission,
                                            Consumer<Boolean> onResult) {
        ActivityResultLauncher<String> requestPermissionLauncher =
                activity.registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                        onResult::accept);
        requestPermissionLauncher.launch(permission);
    }

    public static int getProcessImportance(Context context) {
        return getProcessImportance(context, android.os.Process.myPid());
    }

    public static int getProcessImportance(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : am.getRunningAppProcesses()) {
                if (processInfo.pid == pid) {
                    return processInfo.importance;
                }
            }
        }
        return 0;
    }
}
