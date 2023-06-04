package com.example.englishapp.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.englishapp.R;
import com.example.englishapp.database.DataBase;
import com.example.englishapp.interfaces.CompleteListener;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

public class ForegroundLocationService extends Service {

    private static final String TAG = "ServiceLocation";
    private static final String CHANNEL_ID = "foreground_location_service";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private int NOTIFICATION_ID = 666;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(() -> {

            while (true) {

                Log.i(TAG, "location checking - is running");

                try {

                    if (isLocationEnabled()) {
                        Log.i(TAG, "location enabled");

                        startLocationUpdates();
                    } else {
                        Log.i(TAG, "turn on location");
                    }

                    Thread.sleep(20_000);

                } catch (InterruptedException e) {
                    Log.i(TAG, e.getMessage());
                }

            }
        }).start();

        startForeground(NOTIFICATION_ID, createNotification("Foreground Service Location - Loading", "Latitude - Loading", "Longitude - Loading"));

        return super.onStartCommand(intent, flags, startId);
    }

    private Notification createNotification(String title, String latitude, String longitude) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager.class)
                    .createNotificationChannel(
                            new NotificationChannel(
                                    CHANNEL_ID,
                                    title,
                                    NotificationManager.IMPORTANCE_LOW
                            ));
        }

        String contentText = "Latitude: " + latitude + " Longitude: " + longitude;

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setTicker(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_location)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .build();

        return notification;

    }

    public void startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "null - permission");

        } else {
            Log.i(TAG, "okey");

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            createLocationCallback();

            createLocationRequest();

            fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );
        }
    }

    private void createLocationCallback() {
        Log.i(TAG, "create location callback");

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult == null) {
                    Log.i(TAG, "null result");
                    return;
                }

                Log.i(TAG, "send intent to broadcast - " + locationResult.getLastLocation().getLatitude() + " - " + locationResult.getLastLocation().getLongitude());

                // update user position in firebase
                DataBase.updateUserGeoPosition(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude(), new CompleteListener() {
                    @Override
                    public void OnSuccess() {
                        // send intent to show notification

                        updateNotification(
                                "Foreground Service Location - Updated",
                                String.valueOf(locationResult.getLastLocation().getLatitude()),
                                String.valueOf(locationResult.getLastLocation().getLongitude())
                        );

                        Log.i(TAG, "Location was updated");
                    }

                    @Override
                    public void OnFailure() {
                        // can not update user's location in firebase

                        updateNotification(
                                "Foreground Service Location - Not Updated",
                                String.valueOf(locationResult.getLastLocation().getLatitude()),
                                String.valueOf(locationResult.getLastLocation().getLongitude())
                        );

                        Log.i(TAG, "Can not update user geo position");
                    }
                });
            }
        };
    }

    public void createLocationRequest() {
        Log.i(TAG, "createLocationRequest");

        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 60*1000)
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(60*1000)
                .build();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(locationSettingsResponse -> Log.i(TAG, "success task"));

        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {

                Log.i(TAG, "error - " + e.getMessage());

            }
        });
    }

    public boolean isLocationEnabled() {
        Log.i(TAG, "isLocationEnabled");

        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {

                locationMode = Settings.Secure.getInt(
                        this.getContentResolver(),
                        Settings.Secure.LOCATION_MODE
                );

            } catch (Settings.SettingNotFoundException e) {
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {

            locationProviders = Settings.Secure.getString(
                    this.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED
            );

            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private void updateNotification(String latitude, String longitude, String titleText) {

        Log.i(TAG, "updateNotification");

        Notification notification = createNotification(latitude, longitude, titleText);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}