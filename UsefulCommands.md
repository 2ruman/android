
## Check Application ID (App's UID)

```
$ adb shell dumpsys package [PACKAGE_NAME] | grep appId

$ adb shell pm dump [PACKAGE_NAME] | grep appId
```

## Extract Application ID (App's UID)

```
$ adb shell

$ APP_UID=$(dumpsys package [PACKAGE_NAME] | grep appId= | cut -d'=' -f2); eho $APP_UID # Extend command

$ PKG_NAME=[PACKAGE_NAME]; APP_UID=$(dumpsys package $PKG_NAME | grep appId= | cut -d'=' -f2); echo "${PKG_NAME}(${APP_UID})" # Extend command
```
