# Android Code Snippets

### Contents
+ [Date / Time](#date--time)
+ [File](#file)
+ [Process](#process)
+ [Screen / View](#screen--view)
+ [System](#system)
<br>

## Date / Time

### Format Time

```java
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

(...)

 /*
  * Format Examples:
  *     "yyyy-MM-dd HH:mm:ss.SSS             -->  2023-08-14 11:55:05.090
  *     "MM/dd/yy hh:mm:ss a"                -->  08/14/23 11:53:52 AM
  *     "yyyy년 MM월 dd일 E요일", Locale.KOREA  -->  2023년 08월 14일 월요일
  */
public static String getFormattedTime(long time) {
    Date date = new Date(time);
    Format dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
    return dateFormat.format(date);
}

String formattedTime = getFormattedTime(System.currentTimeMillis());
```

### Calculate Boot Time and Current Time using Uptime

```java
import android.os.SystemClock;

(...)

/*
 * Note that System.currentTimeMillis() and SystemClock.elapsedRealtime() use
 * different clock, so use this code in way that you don't change local system time manually.
 */
long bootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime();
long bootTimeNanos = System.currentTimeMillis() * 1000000 - SystemClock.elapsedRealtimeNanos();

long currentTime = bootTime + SystemClock.elapsedRealtime();
long currentTimeNanos = bootTimeNanos + SystemClock.elapsedRealtimeNanos();

/* getFormattedTime() function illustrated above [ Date/Time - Format Time ] */
String formattedBootTime1 = getFormattedTime(bootTime);
String formattedBootTime2 = getFormattedTime((bootTimeNanos/1000000));

String formattedCurrTime1 = getFormattedTime(currentTime);
String formattedCurrTime2 = getFormattedTime((currentTimeNanos/1000000));
```

## File

### Create and Get a Temporary Directory in Public Storage

```java
 public static File getTempDirectoryInPublic() {
     File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
             "tmp_" + System.currentTimeMillis());
     if (!dir.exists() && !dir.mkdirs()) {
         return null;
     }
     return dir;
 }
```

## Process

### Kill Myself

```java
public static void killMyself() {
    android.os.Process.killProcess(android.os.Process.myPid());
}
```

## Screen / View

### Fullscreen Mode

```java
import android.graphics.Color;
import android.os.Build;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

(...in Activity...)

@Override
protected void onResume() {
    super.onResume();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        getWindow().setDecorFitsSystemWindows(false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            controller.hide(WindowInsets.Type.navigationBars());
        }
    }
}
```

### Get Window Size / Get Screen Size

```java
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.WindowManager;

import androidx.annotation.NonNull;

(...)

public static Pair<Integer, Integer> getWindowSize(@NonNull Context context) {
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    int widthPx, heightPx;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        widthPx = wm.getCurrentWindowMetrics().getBounds().width();
        heightPx = wm.getCurrentWindowMetrics().getBounds().height();
    } else {
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        widthPx = metrics.widthPixels;
        heightPx = metrics.heightPixels;
    }
    return new Pair<>(Math.abs(widthPx), Math.abs(heightPx));
}

public static Pair<Integer, Integer> getScreenSize(@NonNull Context context) {
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    int widthPx, heightPx;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        widthPx = wm.getMaximumWindowMetrics().getBounds().width();
        heightPx = wm.getMaximumWindowMetrics().getBounds().height();
    } else {
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(metrics);
        widthPx = metrics.widthPixels;
        heightPx = metrics.heightPixels;
    }
    return new Pair<>(Math.abs(widthPx), Math.abs(heightPx));
}
```

## System

### Get Kernel Version

```java
String kernelVersion = Os.uname().release;
```
