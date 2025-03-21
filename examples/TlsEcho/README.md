
# TLS Echo Client/Server

## Dev Environment
- OS : _Ubuntu 24.04.2 LTS_
- IDE : _Android Studio Meerkat | 2024.3.1_
- JDK : _OpenJDK 17.0.14_

## Note
When the application process moves to the background(entering an idle state), the client socket
may disconnect due to a battery optimization policy that is enabled by default on some Android devices,
so if you want to keep the connection alive, you need to request the package to be added to
the exception list or use the following adb commands:

```
// Check
$ adb shell dumpsys deviceidle whitelist

// Add
$ adb shell dumpsys deviceidle whitelist +truman.android.example.tls_echo.client

// Remove
$ adb shell dumpsys deviceidle whitelist -truman.android.example.tls_echo.client
```
