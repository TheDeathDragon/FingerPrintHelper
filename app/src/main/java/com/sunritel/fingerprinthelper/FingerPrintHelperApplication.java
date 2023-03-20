package com.sunritel.fingerprinthelper;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;

import com.sunritel.fingerprinthelper.receiver.FingerprintEventReceiver;


public class FingerPrintHelperApplication extends Application {

    private static Application mApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationContext = this;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.sunritel.fingerprinthelper.AUTHENTICATED");
        intentFilter.addAction("com.sunritel.fingerprinthelper.PRESSED");
        mApplicationContext.getApplicationContext().registerReceiver(new FingerprintEventReceiver()
                , intentFilter
                , "com.sunritel.fingerprinthelper.permission", null);
    }

    public static Context getContext() {
        return mApplicationContext.getApplicationContext();
    }
}
