package home.truman.demo.sdpdatabasedemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.ref.WeakReference;
/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 */
public class PermissionManager {

    private static final String TAG = "PermissionManager" + AppConstants.TAG_SUFFIX;

    private static final int REQUSET_CODE_WRITE_EXTERNAL_STORAGE = 1;

    private final WeakReference<AppCompatActivity> mActivity;

    public PermissionManager(AppCompatActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

    public boolean checkStoragePermission() {
        AppCompatActivity activity = mActivity.get();

        boolean hasReadPerm = false;
        boolean hasWritePerm = false;

        if (activity == null) {
            Log.e(TAG, "checkPermission() - Failed to get activity instance");
        } else {
            if (!(hasReadPerm = (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED))) {
                Log.d(TAG, "checkPermission() - App doesn't have read permission...");
            }

            if (!(hasWritePerm = (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED))) {
                Log.d(TAG, "checkPermission() - App doesn't have write permission...");
            }
        }
        return hasReadPerm && hasWritePerm;
    }

    public void requestStoragePermission() {
        AppCompatActivity activity = mActivity.get();

        if (activity == null) {
            Log.e(TAG, "requestPermission() - Failed to get activity instance");
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.d(TAG, "requestPermission() - Show request permission rationale");
                showRationale("You should allow storage permission!");
            } else {
                Log.d(TAG, "requestPermission() - No need to show the rationale");
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUSET_CODE_WRITE_EXTERNAL_STORAGE);
            }
        }
        return;
    }

    private void showRationale(String rationale) {
        AppCompatActivity activity = mActivity.get();

        if (activity == null) {
            Log.e(TAG, "showRationale() - Failed to get activity instance");
        } else {
            new AlertDialog.Builder(activity)
                    .setTitle("Notification")
                    .setMessage(rationale != null ? rationale : "null")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
        return;
    }

    public void onRequestPermissionsResult(int requestCode,
                                    String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUSET_CODE_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult() - Permission("
                            +  permissions[0] + ") granted!!!");
                } else {
                    Log.e(TAG, "Permission denied....");
                }
            }
        }
        return;
    }
}
