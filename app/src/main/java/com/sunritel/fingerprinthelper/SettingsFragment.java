package com.sunritel.fingerprinthelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.UserHandle;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

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

    private ListPreference shortcutListPreference;

    private ListPreference actionListPreference;

    private ListPreference fingerprintListPreference;

    private static final int USER_ID = UserHandle.myUserId();
    private static final UserHandle USER_HANDLE = new UserHandle(USER_ID);
    private List<Fingerprint> fingerprints;
    private FingerprintManager fingerprintManager;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        mContext = FingerPrintHelperApplication.getContext();
        mPreferenceUtil = new PreferenceUtil(mContext);

        enableSwitchPreference = findPreference(getString(R.string.main_key));
        authenticationSwitchPreference = findPreference(getString(R.string.authentication_key));
        actionListPreference = findPreference(getString(R.string.action_key));
        appListPreference = findPreference(getString(R.string.action_start_app_key));
        shortcutListPreference = findPreference(getString(R.string.action_start_shortcut_key));
        fingerprintListPreference = findPreference(getString(R.string.fingerprint_key));

        enableSwitchPreference.setOnPreferenceChangeListener(this);
        authenticationSwitchPreference.setOnPreferenceChangeListener(this);
        actionListPreference.setOnPreferenceChangeListener(this);
        appListPreference.setOnPreferenceChangeListener(this);
        shortcutListPreference.setOnPreferenceChangeListener(this);
        fingerprintListPreference.setOnPreferenceChangeListener(this);

        setEnabled(mPreferenceUtil.getBoolean(R.string.main_key));

        appList = getInstalledAppInfo();
        String[] appLabels = new String[appList.size()];
        String[] appActivityNames = new String[appList.size()];
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

        try {
            fingerprintManager = getFingerprintManagerOrNull(mContext);
            if (fingerprintManager != null && fingerprintManager.hasEnrolledFingerprints()) {
                Log.d("onCreatePreferences --> hasEnrolledFingerprints --> true");
                fingerprints = fingerprintManager.getEnrolledFingerprints(USER_ID);
                String[] fingerprintLabels = new String[fingerprints.size()];
                String[] fingerprintNames = new String[fingerprints.size()];
                for (Fingerprint fingerprint : fingerprints) {
                    Log.d("onCreatePreferences --> fingerprint --> " + fingerprint.getName());
                    Log.d("onCreatePreferences --> fingerprint --> " + fingerprint.getBiometricId());
                    sp = mContext.getSharedPreferences("fingerprints", Context.MODE_PRIVATE);
                    editor = sp.edit();
                    editor.putString(fingerprint.getBiometricId() + "", (String) fingerprint.getName());
                    editor.apply();
                    fingerprintLabels[fingerprints.indexOf(fingerprint)] = (String) fingerprint.getName();
                    fingerprintNames[fingerprints.indexOf(fingerprint)] = fingerprint.getBiometricId() + "";
                }
                fingerprintListPreference.setEntries(fingerprintLabels);
                fingerprintListPreference.setEntryValues(fingerprintNames);
            } else {
                Log.d("onCreatePreferences --> hasEnrolledFingerprints --> false");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("onCreatePreferences --> Init FingerprintManager failed");
        }
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        Log.d("onPreferenceChange --> " + preference.getKey() + " --> " + newValue);
        if (preference.equals(enableSwitchPreference)) {
            setEnabled((boolean) newValue);
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
        } else if (preference.equals(fingerprintListPreference)) {
            Log.d("onPreferenceChange --> " + newValue);
            // TODO 每个指纹对应单独设置
        }
        return true;
    }

    public List<ResolveInfo> getInstalledAppInfo() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> appList = mContext.getPackageManager().queryIntentActivities(intent, 0);
        ResolveInfo mPackageInfo = null;
        for (ResolveInfo info : appList) {
            Log.d("getInstalledAppInfo --> " + info.activityInfo.packageName);
            if (info.activityInfo.packageName.equals(mContext.getPackageName())) {
                mPackageInfo = info;
            }
        }
        // exclude this app
        if (mPackageInfo != null) {
            appList.remove(mPackageInfo);
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
        appListPreference.setEnabled(enabled &&
                mPreferenceUtil.getString(R.string.action_key)
                        .equals(getString(R.string.action_start_application)));
        shortcutListPreference.setEnabled(enabled &&
                mPreferenceUtil.getString(R.string.action_key)
                        .equals(getString(R.string.action_start_shortcut)));
        fingerprintListPreference.setEnabled(enabled);
    }

    public static FingerprintManager getFingerprintManagerOrNull(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            return (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        } else {
            return null;
        }
    }

}