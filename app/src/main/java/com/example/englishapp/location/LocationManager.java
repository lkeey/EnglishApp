package com.example.englishapp.location;

import static com.example.englishapp.messaging.Constants.KEY_LATITUDE;
import static com.example.englishapp.messaging.Constants.KEY_LONGITUDE;
import static com.example.englishapp.messaging.Constants.LOCAL_BROADCAST_ACTION;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

public class LocationManager {

    private static final String TAG = "ManagerLocation";
    private static final int REQUEST_CHECK = 10_000;
    private static LocationManager instance = null;
    private Context context;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

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
        Log.i(TAG, "init");

        this.context = context;

        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        Intent intent = new Intent(LOCAL_BROADCAST_ACTION);
//        StringBuilder stringBuilder = new StringBuilder();

        Log.i(TAG, "init 2");

        try {
            locationCallback = new LocationCallback() {

                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Log.i(TAG, "send intent");

                    super.onLocationResult(locationResult);

                    if (locationResult == null) {
                        Log.i(TAG, "null result");
                        return;
                    }

                    for (Location location : locationResult.getLocations()) {
                        Log.i(TAG, "Location - " + location.getLatitude() + " - " + location.getLongitude());

//                        stringBuilder.setLength(0);
//                        stringBuilder.append("Time: " + System.currentTimeMillis() + " Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());

                        intent.putExtra(KEY_LATITUDE, String.valueOf(location.getLatitude()));
                        intent.putExtra(KEY_LONGITUDE, String.valueOf(location.getLongitude()));

                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                }
            };

            Log.i(TAG, "init: 5");
        } catch (Exception e) {
            Log.i(TAG, "Err " + e.getMessage());
        }
    }

    public void createLocationRequest() {
        Log.i(TAG, "createLocationRequest");

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(context);

        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(locationSettingsResponse -> Log.i(TAG, "success task"));

        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    resolvableApiException.startResolutionForResult((Activity) context, REQUEST_CHECK);
                } catch (Exception ex) {
                    Log.i(TAG, "error - " + ex.getMessage());
                }
            }
        });
    }

    public void startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates");


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "null - permission");
            return;
        } else {
            Log.i(TAG, "okey");

            fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );
        }


    }

    public boolean isLocationEnabled() {
        Log.i(TAG, "isLocationEnabled");

        int locationMode = 0;
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
