# Android Code Snippets

### Contents
+ Date/Time
<br>

## Date/Time

### Format Time

```java
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

(...)

public static String getFormattedTime(long time) {
    Date date = new Date(time);
    Format dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
    return dateFormat.format(date);
}

String formattedTime = getFormattedTime(System.currentTimeMillis());
```

### Calculate Boot Time

```java
import android.os.SystemClock;

(...)

// [!] Note that System.currentTimeMillis() and SystemClock.elapsedRealtime() use
// different clock, so use this code in way that you don't change local system time manually.
long bootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime();
long bootTimeNanos = System.currentTimeMillis() * 1000000 - SystemClock.elapsedRealtimeNanos();

// getFormattedTime() function illustrated above [ Date/Time - Format Time ]
String formattedBootTime1 = getFormattedTime(bootTime);
String formattedBootTime2 = getFormattedTime((bootTimeNanos/1000000));
```
