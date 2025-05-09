## Get Installed APK Path

```
# By the package name
$ adb shell dumpsys package [PACKAGE_NAME] | grep "path: " | head -n 1 | cut -d"path: " -F2
```

## Get Activity Component Name

```
# Of currently top-resumed activity
$ adb shell dumpsys activity activities | grep topResumedActivity= | awk '{print $3}'
```

## Get Package Name

```
# Of currently top-resumed activity
$ adb shell dumpsys activity activities | grep -A10 topResumedActivity | grep "packageName=" | awk -F'[[:space:]]+|packageName=' '{print $3}'
```


## Get Process Name
```
# Of currently top-resumed activity
$ adb shell dumpsys activity activities | grep -A10 topResumedActivity | grep "processName=" | cut -d"processName=" -F2
```

## Get Application ID (App's UID)

```
# By the package name: Method #1
$ adb shell dumpsys package [PACKAGE_NAME] | grep appId

# By the package name: Method #2
$ adb shell pm dump [PACKAGE_NAME] | grep appId
```

## Extract Application ID (App's UID)

```
$ adb shell

$ APP_UID=$(dumpsys package [PACKAGE_NAME] | grep appId= | cut -d'=' -f2); echo $APP_UID # Extend command

$ PKG_NAME=[PACKAGE_NAME]; APP_UID=$(dumpsys package $PKG_NAME | grep appId= | cut -d'=' -f2); echo "${PKG_NAME}(${APP_UID})" # Extend command
```

## Extract Process ID (PID)

```
$ adb shell

$ PID=$(ps -A | grep [PACKAGE_NAME_HINT] | head -n 1 | awk '{print $2}'); echo $PID # Extend command
```
