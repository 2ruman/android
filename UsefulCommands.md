## Get Package Name

```
# of currently top-resumed activity
$ adb shell dumpsys activity activities | grep -A10 topResumedActivity | grep "packageName=" | awk -F'[[:space:]]+|packageName=' '{print $3}'
```


## Check Application ID (App's UID)

```
$ adb shell dumpsys package [PACKAGE_NAME] | grep appId

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
