package com.example.englishapp.location;

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

public class LocationWork extends Worker {

    private static final String TAG = "LocationWorker";
    private NotificationManager notificationManager;
    private Context context;
    private String progress = "Starting work";
    private int NOTIFICATION_ID = 1;
    private LocationManager locationManager;
    private IntentFilter localBroadcastIntentFilter;

    public LocationWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        locationManager = LocationManager.getInstance(context);

        localBroadcastIntentFilter = new IntentFilter();
        localBroadcastIntentFilter.addAction("broadcast");

        LocalBroadcastManager.getInstance(context).registerReceiver(localBroadCastReceiver, localBroadcastIntentFilter);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "doWork");

        setForegroundAsync(showNotification(progress));

        while (true) {

            locationManager.startLocationUpdates();

            if (1 > 2) {
                break;
            }

            Log.i(TAG, "here");

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return Result.success();
    }

    private ForegroundInfo showNotification(String progress) {
        Log.i(TAG, "showNotification");

        return new ForegroundInfo(NOTIFICATION_ID, createNotification(progress));
    }

    private Notification createNotification(String progress) {
        Log.i(TAG, "createNotification");

        String CHANNEL_ID = "100";
        String title = "Foreground Work";
        String cancel = "Cancel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(new NotificationChannel(
                    CHANNEL_ID,
                    title,
                    NotificationManager.IMPORTANCE_HIGH
            ));
        }

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setTicker(title)
                .setContentText(progress)
                .setSmallIcon(R.drawable.ic_send)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .build();

        return notification;
    }

    private void updateNotification(String progress) {
        Log.i(TAG, "updateNotification");

        Notification notification = createNotification(progress);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }


    BroadcastReceiver localBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Broadcasted");
            progress = intent.getStringExtra("location");
            updateNotification(progress);
        }
    };


    @Override
    public void onStopped() {
        Log.i(TAG, "onStopped");

        LocalBroadcastManager.getInstance(context).unregisterReceiver(localBroadCastReceiver);
        super.onStopped();
    }
}
