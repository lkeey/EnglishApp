package com.example.englishapp.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service implements LocationListener {

    private static final String TAG = "ServiceLocation";
    private static long notify_interval = 1000;
    private boolean isGPSEnable = false;
    private boolean isNetworkEnable = false;
    private double latitude, longitude;
    private LocationManager locationManager;
    private Location location;
    private Timer mTimer = null;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "Service created");



        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(), 5, notify_interval);

    }


    private void getLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {
            Log.i(TAG, "no connection and no gps");

        } else {

            if (isNetworkEnable) {
                Log.i(TAG, "no gps");

                location = null;

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);

                if (locationManager != null) {
                    Log.i(TAG, "no connection");

                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {

                        Log.e(TAG, "latitude 1 - " + location.getLatitude());
                        Log.e(TAG, "longitude 1 - " + location.getLongitude());

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                    }
                }

            }


            if (isGPSEnable) {

                location = null;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

                if (locationManager != null) {

                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (location != null) {
                        Log.e(TAG, "latitude 2 - " + location.getLatitude());
                        Log.e(TAG, "longitude 2 - " + location.getLongitude());

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                    }
                }
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            Log.i(TAG, "time!");

            getLocation();

        }
    }


}
