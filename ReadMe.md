
## Install
```cmd
adb root
adb shell remount
adb shell mkdir /system/priv-app/FingerPrintHelper/
adb push .\FingerPrintHelper.apk /system/priv-app/FingerPrintHelper/
adb reboot
```

## Modified Framework

```diff
diff --git a/frameworks/base/services/core/java/com/android/server/biometrics/sensors/AuthenticationClient.java b/frameworks/base/services/core/java/com/android/server/biometrics/sensors/AuthenticationClient.java
old mode 100644
new mode 100755
index f20c08fdc8f..26afa490863
--- a/frameworks/base/services/core/java/com/android/server/biometrics/sensors/AuthenticationClient.java
+++ b/frameworks/base/services/core/java/com/android/server/biometrics/sensors/AuthenticationClient.java
@@ -42,6 +42,8 @@ import com.android.server.biometrics.Utils;
 import java.util.ArrayList;
 import java.util.List;
 
+import android.content.Intent;
+
 /**
  * A class to keep track of the authentication state for a given client.
  */
@@ -193,6 +195,18 @@ public abstract class AuthenticationClient<T> extends AcquisitionClient<T>
                 + ", user: " + getTargetUserId()
                 + ", clientMonitor: " + toString());
 
+        Intent intent = new Intent();
+        intent.setAction("com.sunritel.fingerprinthelper.AUTHENTICATED");
+        intent.setPackage("com.sunritel.fingerprinthelper");
+        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
+        intent.putExtra("authenticated", authenticated);
+        intent.putExtra("id", identifier.getBiometricId());
+        intent.putExtra("Owner", getOwnerString());
+        intent.putExtra("isBP", isBiometricPrompt());
+        intent.putExtra("requireConfirmation", mRequireConfirmation);
+        intent.putExtra("user", getTargetUserId());
+        getContext().sendBroadcast(intent, "com.sunritel.fingerprinthelper.permission");
+
         final PerformanceTracker pm = PerformanceTracker.getInstanceForSensorId(getSensorId());
         if (isCryptoOperation()) {
             pm.incrementCryptoAuthForUser(getTargetUserId(), authenticated);
diff --git a/frameworks/base/services/core/java/com/android/server/policy/PhoneWindowManager.java b/frameworks/base/services/core/java/com/android/server/policy/PhoneWindowManager.java
index 180ec6f8677..6461d700521 100755
--- a/frameworks/base/services/core/java/com/android/server/policy/PhoneWindowManager.java
+++ b/frameworks/base/services/core/java/com/android/server/policy/PhoneWindowManager.java
@@ -235,6 +235,7 @@ import java.io.PrintWriter;
 import java.util.HashSet;
 import android.content.ComponentName;
 import android.app.ActivityManagerNative;
+import android.hardware.input.InputManager;
 
 /**
  * WindowManagerPolicy implementation for the Android phone UI.  This
@@ -320,6 +321,10 @@ public class PhoneWindowManager implements WindowManagerPolicy {
 
     static final int PENDING_KEY_NULL = -1;
        private static final String KEY_TEST= "isKeyCodeTest";
+    private static final String FINGERPRINT_KEY_RETURN = "isFingerprintKeyReturn";
+    private static final String FINGERPRINT_KEY_HOME = "isFingerprintKeyHome";
+    private static final String FINGERPRINT_KEY_RECENT = "isFingerprintKeyRecent";
+    private long mKeyRemappingSendFakeKeyDownTime;
 
     static public final String SYSTEM_DIALOG_REASON_KEY = "reason";
     static public final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
@@ -2642,6 +2647,46 @@ public class PhoneWindowManager implements WindowManagerPolicy {
                     }
                 }
                 break;
+            case KeyEvent.KEYCODE_F9:
+                int isFingerprintKeyReturn = Settings.System.getIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_RETURN, 0, UserHandle.USER_CURRENT);
+                int isFingerprintKeyHome = Settings.System.getIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_HOME, 0, UserHandle.USER_CURRENT);
+                int isFingerprintKeyRecent = Settings.System.getIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_RECENT, 0, UserHandle.USER_CURRENT);
+                if (isFingerprintKeyReturn != 0 || isFingerprintKeyHome != 0 || isFingerprintKeyRecent != 0){
+                    android.util.Log.d("Rin","PhoneWindowManager KEYCODE_F9 isFingerprintKeyReturn:" + isFingerprintKeyReturn);
+                    android.util.Log.d("Rin","PhoneWindowManager KEYCODE_F9 isFingerprintKeyHome:" + isFingerprintKeyHome);
+                    android.util.Log.d("Rin","PhoneWindowManager KEYCODE_F9 isFingerprintKeyRecent:" + isFingerprintKeyRecent);
+                    android.util.Log.d("Rin", "interceptKeyTi keyCode=" + keyCode + " down=" + down + " repeatCount=" + repeatCount + " keyguardOn=" + keyguardOn + " canceled=" + canceled);
+                }
+
+                if (!keyguardOn && isFingerprintKeyReturn == 1) {
+                    if (down && repeatCount == 0) {
+                        keyRemappingSendFakeKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
+                        keyRemappingSendFakeKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK);
+                        android.util.Log.d("Rin","PhoneWindowManager keyRemappingSendFakeKeyEvent KEYCODE_BACK");
+                        return key_consumed;
+                    }
+                }
+
+                if (!keyguardOn && isFingerprintKeyRecent == 1) {
+                    if (down && repeatCount == 0) {
+                        android.util.Log.d("Rin","PhoneWindowManager preloadRecentApps");
+                        preloadRecentApps();
+                    } else if (!down) {
+                        android.util.Log.d("Rin","PhoneWindowManager toggleRecentApps");
+                        toggleRecentApps();
+                    }
+                    return key_consumed;
+                }
+
+                if (!keyguardOn && isFingerprintKeyHome == 1) {
+                    if (down && repeatCount == 0) {
+                        keyRemappingSendFakeKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HOME);
+                        keyRemappingSendFakeKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HOME);
+                        android.util.Log.d("Rin","PhoneWindowManager keyRemappingSendFakeKeyEvent KEYCODE_HOME");
+                        return key_consumed;
+                    }
+                }
+                break;
             case KeyEvent.KEYCODE_APP_SWITCH:
@@ -5645,4 +5690,14 @@ public class PhoneWindowManager implements WindowManagerPolicy {
         }
     }
 
+    private void keyRemappingSendFakeKeyEvent(int action, int keyCode) {
+        long eventTime = SystemClock.uptimeMillis();
+        if (action == KeyEvent.ACTION_DOWN) {
+            mKeyRemappingSendFakeKeyDownTime = eventTime;
+        }
+        KeyEvent keyEvent = new KeyEvent(mKeyRemappingSendFakeKeyDownTime, eventTime, action, keyCode, 0);
+        InputManager inputManager = (InputManager) mContext.getSystemService(Context.INPUT_SERVICE);
+        inputManager.injectInputEvent(keyEvent, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
+    }
+
 }

```


## Add Extra Settings
Location : `vendor/mediatek/proprietary/packages/apps/MtkSettings/res/xml/security_dashboard_settings.xml`
```xml
<Preference
    android:title="FingerprintHelper"
    android:summary="FingerprintHelper can provide some convenient functions">
    <intent
        android:action="android.settings.SETTINGS"
        android:targetClass="com.sunritel.fingerprinthelper.SettingsActivity"
        android:targetPackage="com.sunritel.fingerprinthelper" />
</Preference>
```
