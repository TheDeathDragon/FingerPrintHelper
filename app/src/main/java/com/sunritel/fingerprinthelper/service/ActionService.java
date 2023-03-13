package com.sunritel.fingerprinthelper.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.sunritel.fingerprinthelper.R;
import com.sunritel.fingerprinthelper.utils.Log;
import com.sunritel.fingerprinthelper.utils.PreferenceUtil;

public class ActionService extends Service {

    private static boolean isRunning = false;
    private Context mAppContext;
    private PreferenceUtil mPreferenceUtil;

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("onStartCommand");
        isRunning = true;
        mAppContext = getApplicationContext();
        mPreferenceUtil = new PreferenceUtil(mAppContext);
        if (!mPreferenceUtil.getBoolean(R.string.main_key)) {
            return Service.START_STICKY;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public ComponentName startService(Intent service) {
        Log.d("startService");
        return super.startService(service);
    }

    @Override
    public boolean stopService(Intent name) {
        Log.d("stopService");
        return super.stopService(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        Log.d("onDestroy");
    }

    public static boolean isRunning() {
        return isRunning;
    }
}