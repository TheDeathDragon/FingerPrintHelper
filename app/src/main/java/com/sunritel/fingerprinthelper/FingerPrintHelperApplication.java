package com.sunritel.fingerprinthelper;

import android.app.Application;
import android.content.Context;


public class FingerPrintHelperApplication extends Application {

    private static Application mApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationContext = this;
    }

    public static Context getContext() {
        return mApplicationContext.getApplicationContext();
    }
}
