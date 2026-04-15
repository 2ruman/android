package truman.android.demo.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class TN {

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

    public static void d(Context context, String message) { show(context, message, COLOR_DEBUG); }
    public static void e(Context context, String message) { show(context, message, COLOR_ERROR); }
    public static void w(Context context, String message) { show(context, message, COLOR_WARNING); }
    public static void i(Context context, String message) { show(context, message, COLOR_INFO); }
    public static void s(Context context, String message) { show(context, message, COLOR_SUCCESS); }
    public static void c(Context context, String message) { show(context, message, COLOR_CRITICAL); }

    private static void show(Context context, String message, int color) {
        if (!(context instanceof Activity)) return;
        final Activity activity = (Activity) context;
        final ViewGroup rootView = activity.findViewById(android.R.id.content);

        if (currentMode == Mode.IMMEDIATE && lastToastView != null) {
            rootView.removeView(lastToastView);
        }

        if (currentMode == Mode.SLIDE_UP && lastToastView != null) {
            final View prevView = lastToastView;
            prevView.animate()
                    .translationYBy(-dpToPx(context, SLIDE_UP_DP))
                    .alpha(0f)
                    .setDuration(ANIM_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            rootView.removeView(prevView);
                        }
                    });
        }

        final FrameLayout toastLayout = new FrameLayout(context);
        TextView textView = new TextView(context);
        textView.setText(message);
        textView.setTextColor(Color.WHITE);
        textView.setPadding(dpToPx(context, 20), dpToPx(context, 12), dpToPx(context, 20), dpToPx(context, 12));
        textView.setGravity(Gravity.CENTER);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(color);
        shape.setCornerRadius(dpToPx(context, 8));
        toastLayout.setBackground(shape);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER;
        toastLayout.setLayoutParams(params);
        toastLayout.addView(textView);

        toastLayout.setAlpha(0f);
        rootView.addView(toastLayout);
        lastToastView = toastLayout;

        toastLayout.animate()
                .alpha(1f)
                .setDuration(ANIM_DURATION)
                .setListener(null);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (toastLayout.getParent() != null) {
                toastLayout.animate()
                        .alpha(0f)
                        .setDuration(ANIM_DURATION)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                rootView.removeView(toastLayout);
                                if (lastToastView == toastLayout) lastToastView = null;
                            }
                        });
            }
        }, DISPLAY_DURATION);
    }

    private static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}