
# Foreground Service

## Note
- You don't need 'POST_NOTIFICATIONS' permission if your SDK version is equal or greater than 34.

- For a quick test, use the command like below:
```
    $ adb shell device_config put activity_manager short_fgs_timeout_duration 5000
```

_Reference : https://developer.android.com/develop/background-work/services/fgs_