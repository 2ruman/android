
# TLS Echo Client/Server

## Dev Environment
- OS : _Ubuntu 24.04.2 LTS_
- IDE : _Android Studio Meerkat | 2024.3.1_
- JDK : _OpenJDK 17.0.14_

## Note
The client socket could be disconnected while the application process goes into background
(falling into idle state), which is because of a battery optimization policy which is enforced
by default on some android devices. So, it's required to request the package to be included
to the except list, or some adb command is available as below.

```
// Check
$ adb shell dumpsys deviceidle whitelist

// Add
$ adb shell dumpsys deviceidle whitelist +truman.android.example.tls_echo.client

// Remove
$ adb shell dumpsys deviceidle whitelist -truman.android.example.tls_echo.client
```
