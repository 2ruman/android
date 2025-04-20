package truman.android.demo.touch_blocker;

import static android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

public class TbService extends Service {

    private static final String TAG = TbService.class.getSimpleName() + ".2ruman";
    private static final String SERVICE_NOTI_TITLE = "Touch Blocker";
    private static final String SERVICE_NOTI_TEXT = "Touch Blocker is running";

    private TbServiceNotification tbServiceNotification;
    private View vOverlay;
    private int tapCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        tbServiceNotification = new TbServiceNotification(getApplicationContext(), TbService.class);
        tbServiceNotification.setLinkedActivityClass(MainActivity.class);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {

        vOverlay = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null);
        vOverlay.setOnTouchListener((v, event) -> {
            Log.d(TAG, "Touch blocked!");
            return true;
        });

        initUpperView(vOverlay);
        initLowerView(vOverlay);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);

        getWindowManager().addView(vOverlay, params);
    }

    private void initUpperView(View parent) {
        SeekBar sbAlpha = parent.findViewById(R.id.sb_alpha);
        int initProgress = sbAlpha.getProgress();

        View vUpper = parent.findViewById(R.id.v_blocker_upper);
        vUpper.setAlpha(initProgress / 100f);

        sbAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vUpper.setAlpha(progress / 100f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initLowerView(View parent) {
        GestureDetector gestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        Log.d(TAG, "Touch blocked!");
                        return true;
                    }
                    @Override
                    public boolean onDoubleTap(@NonNull MotionEvent e) {
                        if (Utils.isEven(++tapCount)) {
                            stopSelf();
                        }
                        return true;
                    }
                });
        View vLower = parent.findViewById(R.id.v_blocker_lower);
        vLower.setClickable(true);
        vLower.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (vOverlay != null) {
            getWindowManager().removeView(vOverlay);
        }
    }

    private WindowManager getWindowManager() {
        return (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");

        if (Settings.canDrawOverlays(this)) {
            becomeForeground();
            initViews();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTimeout(int startId) {
        super.onTimeout(startId);

        Log.d(TAG, "onTimeout() - Importance = " + Utils.getProcessImportance(this));

        if (Utils.getProcessImportance(this) >=
                ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND_SERVICE) {
            stopSelf();
        } else {
            becomeForeground();
        }
    }

    private void becomeForeground() {
        Log.d(TAG, "becomeForeground() - SDK :" + Build.VERSION.SDK_INT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(tbServiceNotification.getNotificationId(),
                    tbServiceNotification.getNotification(SERVICE_NOTI_TITLE, SERVICE_NOTI_TEXT),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE);
        } else {
            startForeground(tbServiceNotification.getNotificationId(),
                    tbServiceNotification.getNotification(SERVICE_NOTI_TITLE, SERVICE_NOTI_TEXT));
        }
    }
}