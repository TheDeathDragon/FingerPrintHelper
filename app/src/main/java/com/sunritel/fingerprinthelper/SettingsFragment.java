package com.sunritel.fingerprinthelper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintManager;
import android.provider.Settings;
import android.os.Bundle;
import android.os.UserHandle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.sunritel.fingerprinthelper.utils.Log;
import com.sunritel.fingerprinthelper.utils.PreferenceUtil;

import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private Context mContext;
    private List<ResolveInfo> mAppList;
    private PreferenceUtil mPreferenceUtil;
    private SwitchPreferenceCompat mEnableSwitchPreference;
    private SwitchPreferenceCompat mReturnSwitchPreference;
    private SwitchPreferenceCompat mHomeSwitchPreference;
    private SwitchPreferenceCompat mRecentSwitchPreference;
    private ListPreference mAppListPreference;
    private ListPreference mFingerprintListPreference;
    private PreferenceCategory mFingerprintPressPreferenceCategory;

    private Preference mTipsPreference;
    private static final String FINGERPRINT_KEY_RETURN = "isFingerprintKeyReturn";
    private static final String FINGERPRINT_KEY_HOME = "isFingerprintKeyHome";
    private static final String FINGERPRINT_KEY_RECENT = "isFingerprintKeyRecent";
    private static final int USER_ID = UserHandle.myUserId();
    private static final UserHandle USER_HANDLE = new UserHandle(USER_ID);
    private List<Fingerprint> mFingerprints;
    private FingerprintManager mFingerprintManager;
    private int mTipsClickCount;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        mContext = FingerPrintHelperApplication.getContext();
        mPreferenceUtil = new PreferenceUtil(mContext);

        mEnableSwitchPreference = findPreference(getString(R.string.main_key));
        mReturnSwitchPreference = findPreference(getString(R.string.fingerprint_function_return_key));
        mHomeSwitchPreference = findPreference(getString(R.string.fingerprint_function_home_key));
        mRecentSwitchPreference = findPreference(getString(R.string.fingerprint_function_recent_key));
        mAppListPreference = findPreference(getString(R.string.fingerprint_start_app_key));
        mFingerprintListPreference = findPreference(getString(R.string.fingerprint_key));
        mTipsPreference = findPreference(getString(R.string.tips_key));
        mFingerprintPressPreferenceCategory = findPreference(getString(R.string.fingerprint_press_key));

        mEnableSwitchPreference.setOnPreferenceChangeListener(this);
        mReturnSwitchPreference.setOnPreferenceChangeListener(this);
        mHomeSwitchPreference.setOnPreferenceChangeListener(this);
        mRecentSwitchPreference.setOnPreferenceChangeListener(this);
        mAppListPreference.setOnPreferenceChangeListener(this);
        mFingerprintListPreference.setOnPreferenceChangeListener(this);

        mFingerprintPressPreferenceCategory.setVisible(mPreferenceUtil.getBoolean(R.string.fingerprint_press_key));

        mTipsClickCount = 0;
        mTipsPreference.setOnPreferenceClickListener(preference -> {
            mTipsClickCount = mTipsClickCount + 1;
            if (mTipsClickCount == 7) {
                Toast.makeText(mContext, getString(R.string.toast_msg), Toast.LENGTH_LONG).show();
                mFingerprintPressPreferenceCategory.setVisible(true);
                if (mPreferenceUtil.getBoolean(R.string.fingerprint_press_key)) {
                    mPreferenceUtil.setBoolean(R.string.fingerprint_press_key, false);
                    mFingerprintPressPreferenceCategory.setVisible(false);
                } else {
                    mPreferenceUtil.setBoolean(R.string.fingerprint_press_key, true);
                    mFingerprintPressPreferenceCategory.setVisible(true);
                }
                mTipsClickCount = 0;
            }
            return true;
        });

        // Set the initial state of the switch
        setEnabled(mPreferenceUtil.getBoolean(R.string.main_key));
        initFingerprintManager();
        initAppList();
        if (mFingerprintManager != null && mFingerprints == null) {
            Toast.makeText(mContext, mContext.getString(R.string.no_fingerprint_enrolled), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResume");
        setEnabled(mPreferenceUtil.getBoolean(R.string.main_key));
        initFingerprintManager();
        initAppList();
    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        Log.d("onPreferenceChange --> " + preference.getKey() + " --> " + newValue);
        if (preference.equals(mEnableSwitchPreference)) {
            setEnabled((boolean) newValue);
        }
        if (preference.equals(mFingerprintListPreference)) {
            String fingerprintId = newValue.toString();
            String app = mPreferenceUtil.getString(fingerprintId + "_app");
            String packageName;
            String appActivityName;
            if (app.equals("")) {
                Log.d("onPreferenceChange --> app --> app is empty");
                mAppListPreference.setValue("");
                mAppListPreference.setSummary(getString(R.string.fingerprint_start_app_summary));
                mAppListPreference.setIcon(null);
                mAppListPreference.setIconSpaceReserved(false);
            } else {
                Log.d("onPreferenceChange --> app --> " + app);
                packageName = app.split("#")[0];
                appActivityName = app.split("#")[1];
                // Check if the app is installed eg. the app is uninstalled by the user
                boolean isAppInstalled = false;
                for (ResolveInfo info : mAppList) {
                    if (info.activityInfo.packageName.equals(packageName) && info.activityInfo.name.equals(appActivityName)) {
                        mAppListPreference.setSummary(info.loadLabel(mContext.getPackageManager()).toString());
                        mAppListPreference.setValue(app);
                        mAppListPreference.setIcon(info.loadIcon(mContext.getPackageManager()));
                        isAppInstalled = true;
                        break;
                    }
                }
                if (!isAppInstalled) {
                    mAppListPreference.setValue("");
                    mAppListPreference.setSummary(getString(R.string.fingerprint_start_app_summary));
                    mAppListPreference.setIcon(null);
                    mAppListPreference.setIconSpaceReserved(false);
                }
            }
        }
        if (preference.equals(mAppListPreference)) {
            if (newValue.toString().equals("")) {
                mAppListPreference.setSummary(getString(R.string.fingerprint_start_app_summary));
                mAppListPreference.setIcon(null);
                mAppListPreference.setIconSpaceReserved(false);
                mPreferenceUtil.setString(mPreferenceUtil.getString(R.string.fingerprint_key) + "_app", "");
                return true;
            }
            String packageName = newValue.toString().split("#")[0];
            String appActivityName = newValue.toString().split("#")[1];
            Log.d("onPreferenceChange --> packageName --> " + packageName);
            Log.d("onPreferenceChange --> activityName --> " + appActivityName);
            mPreferenceUtil.setString(mPreferenceUtil.getString(R.string.fingerprint_key) + "_app", newValue.toString());
            for (ResolveInfo info : mAppList) {
                if (info.activityInfo.packageName.equals(packageName) && info.activityInfo.name.equals(appActivityName)) {
                    mAppListPreference.setSummary(info.loadLabel(mContext.getPackageManager()).toString());
                    mAppListPreference.setValue(newValue.toString());
                    mAppListPreference.setIcon(info.loadIcon(mContext.getPackageManager()));
                    break;
                }
            }
        }
        if (preference.equals(mReturnSwitchPreference)) {
            if ((boolean) newValue) {
                Settings.System.putIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_RETURN, 1, UserHandle.USER_CURRENT);
                if (mPreferenceUtil.getBoolean(R.string.fingerprint_function_home_key)) {
                    mPreferenceUtil.setBoolean(R.string.fingerprint_function_home_key, false);
                    mHomeSwitchPreference.setChecked(false);
                    Settings.System.putIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_HOME, 0, UserHandle.USER_CURRENT);
                }
                if (mPreferenceUtil.getBoolean(R.string.fingerprint_function_recent_key)) {
                    mPreferenceUtil.setBoolean(R.string.fingerprint_function_recent_key, false);
                    mRecentSwitchPreference.setChecked(false);
                    Settings.System.putIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_RECENT, 0, UserHandle.USER_CURRENT);
                }
            } else {
                Settings.System.putIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_RETURN, 0, UserHandle.USER_CURRENT);
            }
        }
        if (preference.equals(mHomeSwitchPreference)) {
            if ((boolean) newValue) {
                Settings.System.putIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_HOME, 1, UserHandle.USER_CURRENT);
                if (mPreferenceUtil.getBoolean(R.string.fingerprint_function_recent_key)) {
                    mPreferenceUtil.setBoolean(R.string.fingerprint_function_recent_key, false);
                    mRecentSwitchPreference.setChecked(false);
                    Settings.System.putIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_RECENT, 0, UserHandle.USER_CURRENT);
                }
                if (mPreferenceUtil.getBoolean(R.string.fingerprint_function_return_key)) {
                    mPreferenceUtil.setBoolean(R.string.fingerprint_function_return_key, false);
                    mReturnSwitchPreference.setChecked(false);
                    Settings.System.putIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_RETURN, 0, UserHandle.USER_CURRENT);
                }
            } else {
                Settings.System.putIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_HOME, 0, UserHandle.USER_CURRENT);
            }
        }
        if (preference.equals(mRecentSwitchPreference)) {
            if ((boolean) newValue) {
                Settings.System.putIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_RECENT, 1, UserHandle.USER_CURRENT);
                if (mPreferenceUtil.getBoolean(R.string.fingerprint_function_home_key)) {
                    mPreferenceUtil.setBoolean(R.string.fingerprint_function_home_key, false);
                    mHomeSwitchPreference.setChecked(false);
                    Settings.System.putIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_HOME, 0, UserHandle.USER_CURRENT);
                }
                if (mPreferenceUtil.getBoolean(R.string.fingerprint_function_return_key)) {
                    mPreferenceUtil.setBoolean(R.string.fingerprint_function_return_key, false);
                    mReturnSwitchPreference.setChecked(false);
                    Settings.System.putIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_RETURN, 0, UserHandle.USER_CURRENT);
                }
            } else {
                Settings.System.putIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_RECENT, 0, UserHandle.USER_CURRENT);
            }
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
            mEnableSwitchPreference.setSummary(R.string.main_summary_on);
            if (mPreferenceUtil.getBoolean(R.string.fingerprint_function_return_key)) {
                Settings.System.putIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_RETURN, 1, UserHandle.USER_CURRENT);
            }
            if (mPreferenceUtil.getBoolean(R.string.fingerprint_function_home_key)) {
                Settings.System.putIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_HOME, 1, UserHandle.USER_CURRENT);
            }
            if (mPreferenceUtil.getBoolean(R.string.fingerprint_function_recent_key)) {
                Settings.System.putIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_RECENT, 1, UserHandle.USER_CURRENT);
            }
        } else {
            mEnableSwitchPreference.setSummary(R.string.main_summary_off);
            Settings.System.putIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_RETURN, 0, UserHandle.USER_CURRENT);
            Settings.System.putIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_HOME, 0, UserHandle.USER_CURRENT);
            Settings.System.putIntForUser(mContext.getContentResolver(), FINGERPRINT_KEY_RECENT, 0, UserHandle.USER_CURRENT);
        }
        mFingerprintListPreference.setEnabled(enabled);
        mAppListPreference.setEnabled(enabled);
        mReturnSwitchPreference.setEnabled(enabled);
        mHomeSwitchPreference.setEnabled(enabled);
        mRecentSwitchPreference.setEnabled(enabled);
    }


    public void initFingerprintManager() {
        try {
            mFingerprintManager = getFingerprintManagerOrNull(mContext);
            if (mFingerprintManager != null && mFingerprintManager.hasEnrolledFingerprints()) {
                Log.d("initFingerprintManager --> hasEnrolledFingerprints --> true");
                mFingerprints = mFingerprintManager.getEnrolledFingerprints(USER_ID);
                String[] fingerprintLabels = new String[mFingerprints.size()];
                String[] fingerprintNames = new String[mFingerprints.size()];
                for (Fingerprint fingerprint : mFingerprints) {
                    Log.d("initFingerprintManager --> fingerprint --> " + fingerprint.getName());
                    Log.d("initFingerprintManager --> fingerprint --> " + fingerprint.getBiometricId());
                    fingerprintLabels[mFingerprints.indexOf(fingerprint)] = (String) fingerprint.getName();
                    fingerprintNames[mFingerprints.indexOf(fingerprint)] = fingerprint.getBiometricId() + "";
                    mPreferenceUtil.setString(fingerprint.getBiometricId() + "", (String) fingerprint.getName());
                }
                mFingerprintListPreference.setEntries(fingerprintLabels);
                mFingerprintListPreference.setEntryValues(fingerprintNames);
            } else {
                Log.d("initFingerprintManager --> hasEnrolledFingerprints --> false");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("initFingerprintManager --> Init FingerprintManager failed");
        } finally {
            if (mFingerprintManager != null && mFingerprints == null) {
                mFingerprintListPreference.setEnabled(false);
                mAppListPreference.setEnabled(false);
            }
        }
    }

    public void initAppList() {
        // 读取已安装的app信息
        mAppList = getInstalledAppInfo();
        String[] appLabels = new String[mAppList.size() + 1];
        String[] appActivityNames = new String[mAppList.size() + 1];

        // 读取保存的app信息
        String app = mPreferenceUtil.getString(mPreferenceUtil.getString(R.string.fingerprint_key) + "_app");
        boolean isAppExist = false;
        for (int i = 0; i < mAppList.size(); i++) {
            appLabels[i] = mAppList.get(i).loadLabel(mContext.getPackageManager()).toString();
            appActivityNames[i] = mAppList.get(i).activityInfo.packageName + "#" + mAppList.get(i).activityInfo.name;
            if (!app.equals("") && appActivityNames[i].equals(app)) {
                mAppListPreference.setSummary(appLabels[i]);
                mAppListPreference.setValue(appActivityNames[i]);
                mAppListPreference.setIcon(mAppList.get(i).loadIcon(mContext.getPackageManager()));
                isAppExist = true;
            } else if (!isAppExist) {
                mAppListPreference.setSummary(getString(R.string.fingerprint_start_app_summary));
                mAppListPreference.setValue("");
                mAppListPreference.setIcon(null);
                mAppListPreference.setIconSpaceReserved(false);
            }
            Log.d("initAppList --> appLabels --> " + appLabels[i]);
            Log.d("initAppList --> appActivityNames --> " + appActivityNames[i]);
        }
        appLabels[mAppList.size()] = getString(R.string.fingerprint_start_app_summary);
        appActivityNames[mAppList.size()] = "";
        mAppListPreference.setEntries(appLabels);
        mAppListPreference.setEntryValues(appActivityNames);
    }

    public static FingerprintManager getFingerprintManagerOrNull(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            return (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        } else {
            return null;
        }
    }

}