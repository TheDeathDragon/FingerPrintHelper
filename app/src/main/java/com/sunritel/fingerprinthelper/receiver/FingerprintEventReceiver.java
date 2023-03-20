package com.sunritel.fingerprinthelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sunritel.fingerprinthelper.FingerPrintHelperApplication;
import com.sunritel.fingerprinthelper.R;
import com.sunritel.fingerprinthelper.utils.ActionUtil;
import com.sunritel.fingerprinthelper.utils.Log;
import com.sunritel.fingerprinthelper.utils.PreferenceUtil;

public class FingerprintEventReceiver extends BroadcastReceiver {

    private Context mAppContext;
    private PreferenceUtil mPreferenceUtil;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (mAppContext == null) {
            mAppContext = FingerPrintHelperApplication.getContext();
        }
        if (mPreferenceUtil == null) {
            mPreferenceUtil = new PreferenceUtil(mAppContext);
        }
        if (intent.getAction().equals("com.sunritel.fingerprinthelper.AUTHENTICATED")) {
            if (intent.getAction().equals("com.sunritel.fingerprinthelper.AUTHENTICATED")) {
                Log.d("FingerprintEventReceiver --> onReceive: " + intent.getAction());
                Log.d("FingerprintEventReceiver --> fpId: " + intent.getIntExtra("id", -1));
                Log.d("FingerprintEventReceiver --> authenticated: " + intent.getBooleanExtra("authenticated", false));
            }
            if (mPreferenceUtil.getBoolean(R.string.main_key)) {
                Log.d("FingerprintEventReceiver --> enabled");
                if (intent.getBooleanExtra("authenticated", false)) {
                    startApp(intent.getIntExtra("id", -1) + "");
                }
            } else {
                Log.d("FingerprintEventReceiver --> disabled");
            }
        }
    }

    public void startApp(String fingerprintId) {
        if (fingerprintId.equals("-1")) {
            Log.d("FingerprintEventReceiver --> fingerprintId is null");
            return;
        }
        String app = mPreferenceUtil.getString(fingerprintId + "_app");
        if (!app.equals("")) {
            String packageName = app.split("#")[0];
            String appActivityName = app.split("#")[1];
            Log.d("FingerprintEventReceiver --> packageName --> " + packageName);
            Log.d("FingerprintEventReceiver --> activityName --> " + appActivityName);
            try {
                ActionUtil.startApplication(mAppContext, packageName, appActivityName);
            } catch (Exception e) {
                Log.e("FingerprintEventReceiver --> startApp: " + e.getMessage());
                mPreferenceUtil.setString(fingerprintId + "_app", "");
            }
        }
    }
}
