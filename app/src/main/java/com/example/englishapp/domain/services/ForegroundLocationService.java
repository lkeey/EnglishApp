package com.example.englishapp.domain.services;

import static com.example.englishapp.data.database.Constants.KEY_LOCATION;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.englishapp.R;
import com.example.englishapp.presentation.activities.SplashActivity;
import com.example.englishapp.data.database.DataBasePersonalData;
import com.example.englishapp.domain.interfaces.CompleteListener;
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

import java.util.Objects;

public class ForegroundLocationService extends Service {

    private static final String TAG = "ServiceLocation";
    private static final String CHANNEL_ID = "foreground_location_service";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private final int NOTIFICATION_ID = 666;
    private Notification notification;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "started location checking");

        notification = createNotification(
                getApplication(),
                "Latitude - Loading",
                "Longitude - Loading"
        );


        createLocationRequest();

        createLocationCallback();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startLocationUpdates();

        startForeground(NOTIFICATION_ID, notification);

        return START_STICKY;
    }

    private void startLocationUpdates() {
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

    private void createLocationRequest() {
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

    private void updateNotification(String longitude, String titleText) {

        Log.i(TAG, "updateNotification");

        Notification notification = createNotification(getApplication(), longitude, titleText);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);

    }

    private void createLocationCallback() {
        Log.i(TAG, "create location callback");

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Log.i(TAG, "send intent to broadcast - " + Objects.requireNonNull(locationResult.getLastLocation()).getLatitude() + " - " + locationResult.getLastLocation().getLongitude());

                // update user position in firebase
                new DataBasePersonalData().updateUserGeoPosition(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude(), new CompleteListener() {
                    @Override
                    public void OnSuccess() {
                        // successfully updated

                        updateNotification(
                                String.valueOf(locationResult.getLastLocation().getLatitude()),
                                String.valueOf(locationResult.getLastLocation().getLongitude())
                        );

                        Log.i(TAG, "Location was updated");
                    }

                    @Override
                    public void OnFailure() {
                        // can not update user's location in firebase

                        updateNotification(
                                String.valueOf(locationResult.getLastLocation().getLatitude()),
                                String.valueOf(locationResult.getLastLocation().getLongitude())
                        );

                        Log.i(TAG, "Can not update user geo position");
                    }
                });
            }
        };
    }

    private Notification createNotification(Context context, String latitude, String longitude) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager.class)
                    .createNotificationChannel(
                            new NotificationChannel(
                                    CHANNEL_ID,
                                    getString(R.string.foreground_service_location),
                                    NotificationManager.IMPORTANCE_LOW
                            ));
        }

        String contentText = "Latitude: " + latitude + " Longitude: " + longitude;

        // what to open after click on notification
        Intent notificationIntent = new Intent(context, SplashActivity.class);

        notificationIntent.putExtra(KEY_LOCATION, true);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.foreground_service_location))
                .setTicker(getString(R.string.foreground_service_location))
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_location)
                .setOngoing(true)
                .setContentIntent(contentIntent)
                .setOnlyAlertOnce(true)
                .build();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        fusedLocationProviderClient.removeLocationUpdates(
                locationCallback
        );
    }
}