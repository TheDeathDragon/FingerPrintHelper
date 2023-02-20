package com.sunritel.fingerprinthelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sunritel.fingerprinthelper.FingerPrintHelperApplication;
import com.sunritel.fingerprinthelper.R;
import com.sunritel.fingerprinthelper.utils.Log;
import com.sunritel.fingerprinthelper.utils.PreferenceUtil;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("BootReceiver --> onReceive: " + intent.getAction());
            Context mAppContext = FingerPrintHelperApplication.getContext();
            PreferenceUtil mPreferenceUtil = new PreferenceUtil(mAppContext);
            if (mPreferenceUtil.getBoolean(R.string.main_key)) {
                Log.d("BootReceiver --> onReceive: " + intent.getAction());
            }
        }
    }
}