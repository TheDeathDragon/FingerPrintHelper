package com.sunritel.fingerprinthelper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.sunritel.fingerprinthelper.utils.Log;
import com.sunritel.fingerprinthelper.utils.PreferenceUtil;

import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private Context mContext;

    private List<ResolveInfo> appList;

    private PreferenceUtil mPreferenceUtil;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        mContext = FingerPrintHelperApplication.getContext();
        mPreferenceUtil = new PreferenceUtil(mContext);
        appList = getInstalledAppInfo();
        String[] appLabels = new String[appList.size()];
        String[] appActivityNames = new String[appList.size()];
        ListPreference appListPreference = findPreference(getString(R.string.action_start_app_key));
        if (appListPreference != null) {
            for (int i = 0; i < appList.size(); i++) {
                appLabels[i] = appList.get(i).loadLabel(mContext.getPackageManager()).toString();
                appActivityNames[i] = appList.get(i).activityInfo.packageName + "#" + appList.get(i).activityInfo.name;
                Log.d("FingerPrintHelperApplication --> onCreatePreferences --> " + appLabels[i]);
                Log.d("FingerPrintHelperApplication --> onCreatePreferences --> " + appActivityNames[i]);
            }
            appListPreference.setEntries(appLabels);
            appListPreference.setEntryValues(appActivityNames);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        if (preference.getKey().equals(getString(R.string.dev_key))) {
            Intent intent = new Intent();
            String packageName = mPreferenceUtil.getString(R.string.action_start_app_key).split("#")[0];
            String appActivityName = mPreferenceUtil.getString(R.string.action_start_app_key).split("#")[1];
            Log.d("onPreferenceTreeClick --> packageName --> " + packageName);
            Log.d("onPreferenceTreeClick --> activityName --> " + appActivityName);
            intent.setClassName(packageName, appActivityName);
            startActivity(intent);
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        return true;
    }

    public List<ResolveInfo> getInstalledAppInfo() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> appList = mContext.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : appList) {
            Log.d("FingerPrintHelperApplication --> getInstalledAppInfo --> " + info.activityInfo.packageName);
        }
        return appList;
    }


}