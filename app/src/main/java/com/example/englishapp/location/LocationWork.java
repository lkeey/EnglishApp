package com.example.englishapp.location;

import static com.example.englishapp.database.Constants.KEY_LATITUDE;
import static com.example.englishapp.database.Constants.KEY_LOCATION;
import static com.example.englishapp.database.Constants.KEY_LONGITUDE;
import static com.example.englishapp.database.Constants.LOCAL_BROADCAST_ACTION;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.englishapp.R;
import com.example.englishapp.managers.LocationManager;

public class LocationWork extends Worker {

    private static final String TAG = "LocationWorker";
    private NotificationManager notificationManager;
    private Context context;
    private int NOTIFICATION_ID = 1;
    private LocationManager locationManager;
    private IntentFilter localBroadcastIntentFilter;

    public LocationWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        locationManager = LocationManager.getInstance(context);

        localBroadcastIntentFilter = new IntentFilter();
        localBroadcastIntentFilter.addAction(LOCAL_BROADCAST_ACTION);

        LocalBroadcastManager.getInstance(context).registerReceiver(localBroadCastReceiver, localBroadcastIntentFilter);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "doWork");

        setForegroundAsync(showNotification("loading", "loading"));

        locationManager.startLocationUpdates();

        while (true) {

            // it needs to never go out to return Result.success();
            Log.i(TAG, "go");
            if (false) {
                break;
            }

            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Log.i(TAG, "exited");

        return Result.success();
    }

    private ForegroundInfo showNotification(String latitude, String longitude) {
        Log.i(TAG, "showNotification");

        String title = "Foreground Work - Loading";

        return new ForegroundInfo(NOTIFICATION_ID, createNotification(latitude, longitude, title));
    }

    private Notification createNotification(String latitude, String longitude, String title) {
        Log.i(TAG, "createNotification");

        String CHANNEL_ID = "foreground_notifications";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(new NotificationChannel(
                    CHANNEL_ID,
                    title,
                    NotificationManager.IMPORTANCE_LOW
            ));
        }

        String contentText = "Latitude: " + latitude + " Longitude: " + longitude;

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setTicker(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_location)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .build();

        return notification;
    }

    private void updateNotification(String latitude, String longitude, String titleText) throws InterruptedException {

        Log.i(TAG, "updateNotification");

        Notification notification = createNotification(latitude, longitude, titleText);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);

    }

    BroadcastReceiver localBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "broadcasted");

            String latitude = intent.getStringExtra(KEY_LATITUDE);
            String longitude = intent.getStringExtra(KEY_LONGITUDE);
            String titleText = intent.getStringExtra(KEY_LOCATION);

            try {

                updateNotification(latitude, longitude, titleText);

            } catch (InterruptedException e) {

                throw new RuntimeException(e);

            }
        }
    };


    @Override
    public void onStopped() {
        Log.i(TAG, "onStopped");

        LocalBroadcastManager.getInstance(context).unregisterReceiver(localBroadCastReceiver);
        super.onStopped();
    }
}
