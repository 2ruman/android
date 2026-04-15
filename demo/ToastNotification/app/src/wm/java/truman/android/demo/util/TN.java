package truman.android.demo.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

public class TN {

    private static final String TAG = TN.class.getSimpleName() + ".2ruman";

    private static final int COLOR_DEBUG    = 0xAA607D8B;   // Blue Grey
    private static final int COLOR_ERROR    = 0xAAFF5252;   // Red
    private static final int COLOR_WARNING  = 0xAAFFAB40;   // Orange
    private static final int COLOR_INFO     = 0xAA2196F3;   // Blue
    private static final int COLOR_SUCCESS  = 0xAA4CAF50;   // Green
    private static final int COLOR_CRITICAL = 0xAA6200EA;   // Deep Purple

    private static final int SLIDE_UP_DP = 100;
    private static final long DISPLAY_DURATION = 2000;
    private static final long ANIM_DURATION = 400;

    public enum Mode {
        SLIDE_UP,
        IMMEDIATE
    }

    private static Mode currentMode = Mode.SLIDE_UP;
    private static View lastToastView = null;

    public static void setMode(Mode mode) {
        currentMode = mode;
    }

    public static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return Settings.canDrawOverlays(context);
    }

    public static void requestOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName())
            );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static void d(Context context, String message) { show(context, message, COLOR_DEBUG); }
    public static void e(Context context, String message) { show(context, message, COLOR_ERROR); }
    public static void w(Context context, String message) { show(context, message, COLOR_WARNING); }
    public static void i(Context context, String message) { show(context, message, COLOR_INFO); }
    public static void s(Context context, String message) { show(context, message, COLOR_SUCCESS); }
    public static void c(Context context, String message) { show(context, message, COLOR_CRITICAL); }

    private static void show(Context context, String message, int color) {
        if (!canDrawOverlays(context)) {
            Log.e(TAG, "Cannot show toast: overlay permission not granted");
            return;
        }

        final Context appContext = context.getApplicationContext();
        final WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) return;

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (lastToastView != null) {
                if (currentMode == Mode.IMMEDIATE) {
                    removeViewSafe(windowManager, lastToastView);
                    lastToastView = null;
                } else if (currentMode == Mode.SLIDE_UP) {
                    final View prevView = lastToastView;
                    lastToastView = null;
                    animateSlideUpAndRemove(windowManager, prevView);
                }
            }

            final FrameLayout toastLayout = new FrameLayout(appContext);
            TextView textView = new TextView(appContext);
            textView.setText(message);
            textView.setTextColor(Color.WHITE);
            textView.setPadding(dpToPx(appContext, 20), dpToPx(appContext, 12), dpToPx(appContext, 20), dpToPx(appContext, 12));
            textView.setGravity(Gravity.CENTER);

            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setColor(color);
            shape.setCornerRadius(dpToPx(appContext, 8));
            toastLayout.setBackground(shape);
            toastLayout.addView(textView);

            int layoutType = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O 
                    ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY 
                    : WindowManager.LayoutParams.TYPE_PHONE;

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    layoutType,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    PixelFormat.TRANSLUCENT
            );
            params.gravity = Gravity.CENTER;

            toastLayout.setAlpha(0f);
            try {
                windowManager.addView(toastLayout, params);
                lastToastView = toastLayout;
                toastLayout.animate().alpha(1f).setDuration(ANIM_DURATION).start();

                handler.postDelayed(() -> {
                    if (toastLayout.isAttachedToWindow()) {
                        toastLayout.animate()
                                .alpha(0f)
                                .setDuration(ANIM_DURATION)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        removeViewSafe(windowManager, toastLayout);
                                        if (lastToastView == toastLayout) lastToastView = null;
                                    }
                                }).start();
                    }
                }, DISPLAY_DURATION);
            } catch (Exception e) {
                Log.e(TAG, "Failed to show toast: " + e.getMessage());
            }
        });
    }

    private static void animateSlideUpAndRemove(WindowManager wm, View view) {
        view.animate()
                .translationYBy(-dpToPx(view.getContext(), SLIDE_UP_DP))
                .alpha(0f)
                .setDuration(ANIM_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        removeViewSafe(wm, view);
                    }
                }).start();
    }

    private static void removeViewSafe(WindowManager wm, View view) {
        try {
            if (view != null && view.isAttachedToWindow()) {
                wm.removeView(view);
            }
        } catch (Exception ignored) {}
    }

    private static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
