package truman.android.demo.touch_blocker;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.service.quicksettings.TileService;
import android.util.Log;

public class TbTileService extends TileService {

    private static final String TAG = TbTileService.class.getSimpleName() + ".2ruman";

    public void onTileAdded() {
        Log.d(TAG, "onTileAdded()");
    }

    public void onTileRemoved() {
        Log.d(TAG, "onTileRemoved()");
    }

    public void onStartListening() {
        Log.d(TAG, "onStartListening()");
    }

    public void onStopListening() {
        Log.d(TAG, "onStopListening()");
    }

    @SuppressLint("StartActivityAndCollapseDeprecated")
    public void onClick() {
        Log.d(TAG, "onClick(5) - is secure? " + isSecure());
        if (isLocked()) {
            Log.d(TAG, "Service not operable while locked");
            return;
        }

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                startActivityAndCollapse(pendingIntent);
            } else {
                startActivityAndCollapse(intent);
            }
        } else {
            startService();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "on destroy");
    }

    private void startService() {
        Intent intent = new Intent(getApplicationContext(), TbService.class);
        startService(intent);
    }
}