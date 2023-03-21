package com.sunritel.fingerprinthelper;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.sunritel.fingerprinthelper.utils.Log;


public class SettingsActivity extends AppCompatActivity {

    static {
        System.loadLibrary("fingerprinthelper");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isCertificateDevice()) {
            finish();
            return;
        }
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragment()).commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        Log.d("onCreate");
    }

    public native boolean isCertificateDevice();
}