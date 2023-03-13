package com.sunritel.fingerprinthelper;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;

import com.sunritel.fingerprinthelper.receiver.FingerprintEventReceiver;
import com.sunritel.fingerprinthelper.utils.PreferenceUtil;


public class FingerPrintHelperApplication extends Application {

    private static Application mApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationContext = this;
        mApplicationContext.getApplicationContext().registerReceiver(new FingerprintEventReceiver(),
                new IntentFilter("com.sunritel.fingerprinthelper.AUTHENTICATED"));
    }

    public static Context getContext() {
        return mApplicationContext.getApplicationContext();
    }
}
