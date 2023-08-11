# Android Code Snippets

### Contents
+ Date/Time

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

String formattedTime = getFormattedTime(System.currentTimeMillis()));
```
