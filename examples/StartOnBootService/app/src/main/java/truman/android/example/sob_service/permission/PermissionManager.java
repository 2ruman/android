package truman.android.example.sob_service.permission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;

import truman.android.example.sob_service.util.Utils;
import truman.android.example.sob_service.ui.Ui;

/**
 * The lifecycle of PermissionManager should follow the activity that owns its instance.
 */
public class PermissionManager {

   private static final boolean DEBUG = true;
   private static final String KEY_POST_NOTIFICATIONS_REQUESTED_ONCE = "post_notifications_requested_once";

   private final ComponentActivity activity;
   private final Context context;
   private final ActivityResultLauncher<String> requestPermissionLauncher;

   public PermissionManager(ComponentActivity activity) {
      this.activity = activity;
      this.context = (Context) activity;
      this.requestPermissionLauncher = Utils.createRequestPermissionResultLauncher(activity,
              (isGranted) -> {
                 Ui.println("Permission " + (isGranted ? "granted!" : "denied..."));

                 if (isPostNotificationsRequiredByVersion()) {
                    if (isGranted) {
                       Utils.putAppPreference(context, KEY_POST_NOTIFICATIONS_REQUESTED_ONCE, false);
                    } else if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                       Utils.putAppPreference(context, KEY_POST_NOTIFICATIONS_REQUESTED_ONCE, true);
                    }
                 }
                 dumpState();
              });
   }

   public boolean checkBatteryOptimizations() {
      return isIgnoringBatteryOptimizations();
   }

   public void enforceIgnoringBatteryOptimizations() {
      requestIgnoreBatteryOptimizations();
   }

   private boolean isIgnoringBatteryOptimizations() {
      PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
      String packageName = context.getPackageName();
      return pm.isIgnoringBatteryOptimizations(packageName);
   }

   @SuppressLint("BatteryLife")
   public void requestIgnoreBatteryOptimizations() {
      if (!isIgnoringBatteryOptimizations()) {
         Intent intent = new Intent();
         intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
         intent.setData(Uri.parse("package:" + context.getPackageName()));
         context.startActivity(intent);
      }
   }

   public boolean checkPostNotificationsPermission() {
      return isPostNotificationsRequiredByVersion()
              && Utils.checkPermission(context, Manifest.permission.POST_NOTIFICATIONS);
   }

   public void enforcePostNotificationsPermission() {
      if (!isPostNotificationsRequiredByVersion()) {
         return;
      }

      if (activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
         showPermissionRationaleDialog();
      } else {
         if (Utils.getAppPreference(context, KEY_POST_NOTIFICATIONS_REQUESTED_ONCE, false)) {
            showGoToSettingsDialog();
         } else {
            Utils.requestPermission(requestPermissionLauncher, Manifest.permission.POST_NOTIFICATIONS);
         }
      }
   }

   private boolean isPostNotificationsRequiredByVersion() {
      return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
              Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE);
   }

   private void showPermissionRationaleDialog() {
      if (!isPostNotificationsRequiredByVersion()) {
         return;
      }
      new AlertDialog.Builder(context)
              .setTitle("Notification Permission Required")
              .setMessage("To provide you with important notifications, this app needs " +
                      "notification permission. Please grant the permission requested.")
              .setPositiveButton("Confirm", (dialog, which) ->
                      requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS))
              .setNegativeButton("Cancel", (dialog, which) ->
                      Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show())
              .show();
   }

   private void showGoToSettingsDialog() {
      if (!isPostNotificationsRequiredByVersion()) {
         return;
      }
      new AlertDialog.Builder(context)
              .setTitle("Notification Permission Required")
              .setMessage("Notification permission is currently not allowed. Please go to your " +
                      "device settings to allow the permission to receive important notifications " +
                      "Do you want to go to settings?")
              .setPositiveButton("Go to Settings", (dialog, which) -> {
                 Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                 Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                 intent.setData(uri);
                 context.startActivity(intent);
              })
              .setNegativeButton("Cancel", (dialog, which) ->
                      Toast.makeText(context, "Notification permission denied. Some features " +
                              "may not work as intended", Toast.LENGTH_SHORT).show())
              .show();
   }

   private void dumpState() {
      if (DEBUG && isPostNotificationsRequiredByVersion()) {
         Ui.println("should?  : " + activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS));
         Ui.println("granted? : " + Utils.checkPermission(context, Manifest.permission.POST_NOTIFICATIONS));
         Ui.println("denied?  : " + Utils.isPermissionDenied(context, Manifest.permission.POST_NOTIFICATIONS));
         Ui.println("\n\n\n\n\n");
      }
   }
}
