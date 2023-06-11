package com.example.englishapp.domain.repositories;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionRepository {

    private static final String TAG = "RepositoryPermission";
    private final Context context;
    private final List<String> listPermissionsNeeded = new ArrayList<>();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 100;

    public PermissionRepository(Context context) {
        this.context = context;
    }

    public List<String> checkPermissions() {

        int wallpaper = ContextCompat.checkSelfPermission(context, Manifest.permission.SET_WALLPAPER);
        int internet = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET);
        int coarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        int fineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int storage;
        int notifications = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifications = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            storage = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES);

            if (storage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES);

                Log.i(TAG, "storage");
            }

        } else {

            storage = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);

            if (storage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);

                Log.i(TAG, "storage");
            }

        }

        if (wallpaper != PackageManager.PERMISSION_GRANTED) {

            listPermissionsNeeded.add(Manifest.permission.SET_WALLPAPER);

            Log.i(TAG, "wallpaper");

        }

        if (internet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);

            Log.i(TAG, "internet");

        }

        if (notifications != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                listPermissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);
            }

            Log.i(TAG, "notifications");
        }

        if (coarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);

            Log.i(TAG, "coarse");
        }

        if (fineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);

            Log.i(TAG, "fine");
        }

        return listPermissionsNeeded;

    }

}
