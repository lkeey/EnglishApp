package com.example.englishapp.location;

import static com.example.englishapp.messaging.Constants.KEY_LATITUDE;
import static com.example.englishapp.messaging.Constants.KEY_LONGITUDE;
import static com.example.englishapp.messaging.Constants.LOCAL_BROADCAST_ACTION;

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

import com.example.englishapp.MVP.CompleteListener;
import com.example.englishapp.MVP.DataBase;
import com.example.englishapp.R;

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

        while (true) {

            locationManager.startLocationUpdates();

            // it needs to never go out to return Result.success();
            if (1 > 2) {
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

        return new ForegroundInfo(NOTIFICATION_ID, createNotification(latitude, longitude));
    }

    private Notification createNotification(String latitude, String longitude) {
        Log.i(TAG, "createNotification");

        String CHANNEL_ID = "foreground_notifications";
        String title = "Foreground Work";

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

    private void updateNotification(String latitude, String longitude) throws InterruptedException {
        Log.i(TAG, "updateNotification");

//        Thread.sleep(30_000); // every 30 seconds

        // update user position in firebase
        DataBase.updateUserGeoPosition(latitude, longitude, new CompleteListener() {
            @Override
            public void OnSuccess() {
                // show notification

                Notification notification = createNotification(latitude, longitude);
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(NOTIFICATION_ID, notification);

                Log.i(TAG, "Location was updated and notification was showed");
            }

            @Override
            public void OnFailure() {
                Log.i(TAG, "Can not update user geo position");
            }
        });

    }


    BroadcastReceiver localBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "broadcasted");

            String latitude = intent.getStringExtra(KEY_LATITUDE);
            String longitude = intent.getStringExtra(KEY_LONGITUDE);

            try {
                updateNotification(latitude, longitude);
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
