# Android Code Snippets

### Contents
+ [Date / Time](#date--time)
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
  *     "yyyy-MM-dd HH:mm:ss.SSS  -->  2023-08-14 11:55:05.090
  *     "MM/dd/yy hh:mm:ss a"     -->  08/14/23 11:53:52 AM
  *     "yyyy년 MM월 dd일 E요일"     -->  2023년 08월 14일 Mon요일
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
