package com.example.englishapp.location;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManager extends ActivityCompat {

    private static final String TAG = "PermissionManager";
    private static PermissionManager instance = null;

    private Context context;

    public PermissionManager() {

    }

    public static PermissionManager getInstance(Context context) {
        Log.i(TAG, "getInstance");

        if (instance == null) {

            instance = new PermissionManager();
        }

        instance.init(context);

        return instance;
    }

    private void init(Context context) {
        this.context = context;
    }

    public void askPermissions(Context context, String[] list, int resultCode) {
        Log.i(TAG, "askPermissions");

        ActivityCompat.requestPermissions((Activity) context, list, resultCode
        );
    }

    public boolean checkPermissions(String[] list) {
        Log.i(TAG, "checkPermissions");

        for (String permission: list) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, permission);

                return false;
            }
        }

        return true;
    }
}
