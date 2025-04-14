
## Check Application ID (App's UID)

```
$ adb shell dumpsys [PACKAGE_NAME] | grep appId

$ adb shell pm dump [PACKAGE_NAME] | grep appId
```
