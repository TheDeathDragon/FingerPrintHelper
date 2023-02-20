package com.sunritel.fingerprinthelper.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ActionService extends Service {

    private static boolean isRunning = false;

    public ActionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}