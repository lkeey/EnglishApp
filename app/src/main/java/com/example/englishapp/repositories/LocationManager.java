package com.example.englishapp.repositories;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

public class LocationManager {

    private static final String TAG = "ManagerLocation";
    private static LocationManager instance = null;
    private Context context;

    private LocationManager () {}

    public static LocationManager getInstance(Context context) {
        Log.i(TAG, "getInstance");

        if (instance == null) {
            instance = new LocationManager();
        }

        instance.init(context);

        return instance;
    }

    private void init(Context context) {
        this.context = context;
    }

    public boolean isLocationEnabled() {
        Log.i(TAG, "isLocationEnabled");

        int locationMode;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {

                locationMode = Settings.Secure.getInt(
                        context.getContentResolver(),
                        Settings.Secure.LOCATION_MODE
                );

            } catch (Settings.SettingNotFoundException e) {
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {

            locationProviders = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED
            );

            return !TextUtils.isEmpty(locationProviders);
        }
    }
}
