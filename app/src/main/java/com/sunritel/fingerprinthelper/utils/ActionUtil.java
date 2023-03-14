package com.sunritel.fingerprinthelper.utils;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.PowerManager;

import com.sunritel.fingerprinthelper.R;

public class ActionUtil {

    public static void showHomeScreen(Context context) {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        try {
            context.startActivity(homeIntent);
            Log.d("ActionUtil --> showHomeScreen");
        } catch (Exception e) {
            Log.e("ActionUtil --> showHomeScreen: " + e.getMessage());
        }
    }
    public static void lockScreen(Context context) {
        try {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.class.getMethod("goToSleep", long.class).invoke(powerManager, System.currentTimeMillis());
            Log.d("ActionUtil --> lockScreen");
        } catch (Exception e) {
            Log.e("ActionUtil --> lockScreen: " + e.getMessage());
        }
    }

    public static void openFlashLight(Context context) {
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            if (cameraManager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                cameraManager.setTorchMode(cameraId, true);
                Log.d("ActionUtil --> openFlashLight: " + cameraId);
            } else {
                Log.d("ActionUtil --> openFlashLight: " + "Flash not available");
            }
        } catch (CameraAccessException e) {
            Log.e("ActionUtil --> openFlashLight: " + e.getMessage());
        }
    }

    public static void startApplication(Context context, String packageName, String appActivityName) {
        Intent intent = new Intent();
        intent.setClassName(packageName, appActivityName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        Log.d("ActionUtil --> startApplication: " + packageName);
        Log.d("ActionUtil --> startApplication --> " + appActivityName);
    }

}
