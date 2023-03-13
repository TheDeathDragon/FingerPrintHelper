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

    private PreferenceUtil mPreferenceUtil;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.sunritel.fingerprinthelper.AUTHENTICATED")) {
            Log.d("FingerprintEventReceiver --> onReceive: " + intent.getAction());
            Context mAppContext = FingerPrintHelperApplication.getContext();
            mPreferenceUtil = new PreferenceUtil(mAppContext);
            if (mPreferenceUtil.getBoolean(R.string.main_key)) {
                Log.d("FingerprintEventReceiver --> enabled");
                if (mPreferenceUtil.getString(R.string.action_key).equals(mAppContext.getString(R.string.action_home))) {
                    ActionUtil.showHomeScreen(mAppContext);
                } else if (mPreferenceUtil.getString(R.string.action_key).equals(mAppContext.getString(R.string.action_camera))) {
                    ActionUtil.lunchCamera(mAppContext);
                } else if (mPreferenceUtil.getString(R.string.action_key).equals(mAppContext.getString(R.string.action_lock))) {
                    ActionUtil.lockScreen(mAppContext);
                } else if (mPreferenceUtil.getString(R.string.action_key).equals(mAppContext.getString(R.string.action_flashlight))) {
                    ActionUtil.openFlashLight(mAppContext);
                } else if (mPreferenceUtil.getString(R.string.action_key).equals(mAppContext.getString(R.string.action_start_application))) {
                    String packageName = mPreferenceUtil.getString(R.string.action_start_app_key).split("#")[0];
                    String appActivityName = mPreferenceUtil.getString(R.string.action_start_app_key).split("#")[1];
                    Log.d("onPreferenceTreeClick --> packageName --> " + packageName);
                    Log.d("onPreferenceTreeClick --> activityName --> " + appActivityName);
                    ActionUtil.startApplication(mAppContext, packageName, appActivityName);
                }
            } else {
                Log.d("FingerprintEventReceiver --> disabled");
            }
        }
    }
}
