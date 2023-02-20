package com.sunritel.fingerprinthelper.utils;

public class Log {

    public static final String TAG = "FingerPrintHelper";
    public static final String LOG_PREFIX = "FingerPrintHelperApplication --> ";

    public static final boolean DEBUG = true;

    public static void d(String message) {
        if (DEBUG) {
            android.util.Log.d(TAG, LOG_PREFIX + message);
        }
    }

    public static void v(String message) {
        if (DEBUG) {
            android.util.Log.d(TAG, LOG_PREFIX + message);
        }
    }

    public static void i(String message) {
        if (DEBUG) {
            android.util.Log.d(TAG, LOG_PREFIX + message);
        }
    }

    public static void w(String message) {
        if (DEBUG) {
            android.util.Log.d(TAG, LOG_PREFIX + message);
        }
    }

    public static void e(String message) {
        if (DEBUG) {
            android.util.Log.d(TAG, LOG_PREFIX + message);
        }
    }
}
