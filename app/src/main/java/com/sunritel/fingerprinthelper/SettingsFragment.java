package com.sunritel.fingerprinthelper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.sunritel.fingerprinthelper.utils.ActionUtil;
import com.sunritel.fingerprinthelper.utils.Log;
import com.sunritel.fingerprinthelper.utils.PreferenceUtil;

import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private Context mContext;

    private List<ResolveInfo> appList;

    private PreferenceUtil mPreferenceUtil;
    private SwitchPreferenceCompat enableSwitchPreference;
    private SwitchPreferenceCompat authenticationSwitchPreference;
    private ListPreference appListPreference;
    private ListPreference actionListPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        mContext = FingerPrintHelperApplication.getContext();
        mPreferenceUtil = new PreferenceUtil(mContext);

        enableSwitchPreference = findPreference(getString(R.string.main_key));
        authenticationSwitchPreference = findPreference(getString(R.string.authentication_key));
        actionListPreference = findPreference(getString(R.string.action_key));
        appListPreference = findPreference(getString(R.string.action_start_app_key));

        enableSwitchPreference.setOnPreferenceChangeListener(this);
        authenticationSwitchPreference.setOnPreferenceChangeListener(this);
        actionListPreference.setOnPreferenceChangeListener(this);
        appListPreference.setOnPreferenceChangeListener(this);

        setEnabled(mPreferenceUtil.getBoolean(R.string.main_key));

        appList = getInstalledAppInfo();
        String[] appLabels = new String[appList.size()];
        String[] appActivityNames = new String[appList.size()];
        if (appListPreference != null) {
            for (int i = 0; i < appList.size(); i++) {
                appLabels[i] = appList.get(i).loadLabel(mContext.getPackageManager()).toString();
                appActivityNames[i] = appList.get(i).activityInfo.packageName + "#" + appList.get(i).activityInfo.name;
                if (appActivityNames[i].equals(mPreferenceUtil.getString(R.string.action_start_app_key))) {
                    appListPreference.setSummary(appLabels[i]);
                    appListPreference.setValue(appActivityNames[i]);
                }
                Log.d("onCreatePreferences --> " + appLabels[i]);
                Log.d("onCreatePreferences --> " + appActivityNames[i]);
            }
            appListPreference.setEntries(appLabels);
            appListPreference.setEntryValues(appActivityNames);
        } else {
            Log.d("onCreatePreferences --> appListPreference is null, please check the permission of the app");
        }
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        if (preference.getKey().equals(getString(R.string.dev_key))) {
            Intent intent = new Intent();
            if (mPreferenceUtil.getString(R.string.action_start_app_key).equals("")) {
                Log.d("onPreferenceTreeClick --> mPreferenceUtil.getString(R.string.action_start_app_key) is null");
                return super.onPreferenceTreeClick(preference);
            }
            String packageName = mPreferenceUtil.getString(R.string.action_start_app_key).split("#")[0];
            String appActivityName = mPreferenceUtil.getString(R.string.action_start_app_key).split("#")[1];
            Log.d("onPreferenceTreeClick --> packageName --> " + packageName);
            Log.d("onPreferenceTreeClick --> activityName --> " + appActivityName);
            intent.setClassName(packageName, appActivityName);
            startActivity(intent);
        } else if (preference.getKey().equals(getString(R.string.dev_key_2))) {
            Log.d("onPreferenceTreeClick --> dev_key_2 --> " + mPreferenceUtil.getString(R.string.action_key));
            if (mPreferenceUtil.getString(R.string.action_key).equals(getString(R.string.action_home))) {
                ActionUtil.showHomeScreen(mContext);
            } else if (mPreferenceUtil.getString(R.string.action_key).equals(getString(R.string.action_camera))) {
                ActionUtil.lunchCamera(mContext);
            } else if (mPreferenceUtil.getString(R.string.action_key).equals(getString(R.string.action_lock))) {
                ActionUtil.lockScreen(mContext);
            } else if (mPreferenceUtil.getString(R.string.action_key).equals(getString(R.string.action_flashlight))) {
                ActionUtil.openFlashLight(mContext);
            } else if (mPreferenceUtil.getString(R.string.action_key).equals(getString(R.string.action_start_application))) {

            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        Log.d("onPreferenceChange --> " + preference.getKey() + " --> " + newValue);
        if (preference.equals(enableSwitchPreference)) {
            setEnabled((boolean) newValue);
        } else if (preference.equals(authenticationSwitchPreference)) {

        } else if (preference.equals(actionListPreference)) {
            appListPreference.setEnabled(newValue.equals(getString(R.string.action_start_application)));
            Log.d("onPreferenceChange --> " + newValue);
        } else if (preference.equals(appListPreference)) {
            String packageName = newValue.toString().split("#")[0];
            String appActivityName = newValue.toString().split("#")[1];
            Log.d("onPreferenceChange --> packageName --> " + packageName);
            Log.d("onPreferenceChange --> activityName --> " + appActivityName);
            for (ResolveInfo info : appList) {
                if (info.activityInfo.packageName.equals(packageName) && info.activityInfo.name.equals(appActivityName)) {
                    appListPreference.setSummary(info.loadLabel(mContext.getPackageManager()).toString());
                    break;
                }
            }
        }
        return true;
    }

    public List<ResolveInfo> getInstalledAppInfo() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> appList = mContext.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : appList) {
            Log.d("getInstalledAppInfo --> " + info.activityInfo.packageName);
        }
        return appList;
    }

    private void setEnabled(boolean enabled) {
        if (enabled) {
            enableSwitchPreference.setSummary(R.string.main_summary_on);
        } else {
            enableSwitchPreference.setSummary(R.string.main_summary_off);
        }
        authenticationSwitchPreference.setEnabled(enabled);
        actionListPreference.setEnabled(enabled);
        appListPreference.setEnabled(enabled);
    }

}