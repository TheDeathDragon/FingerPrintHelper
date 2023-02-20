package com.sunritel.fingerprinthelper.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class PreferenceUtil {

    private final SharedPreferences sharedPrefs;
    private final Context mContext;

    public PreferenceUtil(Context context) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context;
    }

    public boolean getBoolean(int resId) {
        return sharedPrefs.getBoolean(mContext.getString(resId), false);
    }

    public void setBoolean(int resId, boolean value) {
        SharedPreferences.Editor e = sharedPrefs.edit();
        e.putBoolean(mContext.getString(resId), value);
        e.apply();
    }

    public String getString(int resId) {
        return sharedPrefs.getString(mContext.getString(resId), "");
    }

    public void setString(int resId, String value) {
        SharedPreferences.Editor e = sharedPrefs.edit();
        e.putString(mContext.getString(resId), value);
        e.apply();
    }

    public int getInt(int resId) {
        return sharedPrefs.getInt(mContext.getString(resId), 0);
    }

    public void setInt(int resId, int value) {
        SharedPreferences.Editor e = sharedPrefs.edit();
        e.putInt(mContext.getString(resId), value);
        e.apply();
    }

}
