```markdown

adb root
adb shell remount
adb shell mkdir /system/priv-app/FingerPrintHelper/
adb push .\FingerPrintHelper.apk /system/priv-app/FingerPrintHelper/
adb reboot
```