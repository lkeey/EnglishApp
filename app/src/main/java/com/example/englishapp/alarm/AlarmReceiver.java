package com.example.englishapp.alarm;

import static com.example.englishapp.messaging.Constants.KEY_CHECK_LOCATION;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.BackoffPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.englishapp.MVP.FeedActivity;
import com.example.englishapp.R;
import com.example.englishapp.location.LocationManager;
import com.example.englishapp.location.LocationWork;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class AlarmReceiver extends BroadcastReceiver {
    private final static String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "Received");

        boolean isChecking = intent.getBooleanExtra(KEY_CHECK_LOCATION, false);

        if (isChecking) {
            Log.i(TAG, "received");
            //startCheckingPosition(context);

        } else {
            sendNotification(context);
        }
    }

    private void startCheckingPosition(Context context) {
        LocationManager locationManager = LocationManager.getInstance(context);

        if (locationManager.isLocationEnabled()) {
            Log.i(TAG, "Begin checking");

            locationManager.createLocationRequest();

            startLocationWork(context);
        }

        Log.i(TAG, "enable - " + locationManager.isLocationEnabled());
    }

    private void startLocationWork(Context context) {
        Log.i(TAG, "startLocationWork");

        OneTimeWorkRequest foregroundWorkRequest = new OneTimeWorkRequest.Builder(LocationWork.class)
                .addTag("LocationWork")
                .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.SECONDS
                ).build();

        WorkManager.getInstance(context).enqueue(foregroundWorkRequest);
    }


    private void sendNotification(Context context) {
        // must be random to always show on screen
        int NOTIFICATION_ID = new Random().nextInt();

        String CHANNEL_ID = "word_notifications"; // The id of the channel.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Log.i(TAG, "More O");

            CharSequence name = "Word Notification"; // The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(
                    CHANNEL_ID,
                    name,
                    importance
            );

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        // what to open after click on notification
        Intent notificationIntent = new Intent(context, FeedActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_send)
//                .setContentTitle(remoteMessage.getData().get(REMOTE_MSG_TITLE))
//                .setContentText(remoteMessage.getData().get(REMOTE_MSG_DATA))
                .setContentTitle("title")
                .setContentText("content")
                .setContentIntent(contentIntent);

        Notification notification = notificationBuilder.build();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        notificationManagerCompat.notify(NOTIFICATION_ID, notification);

    }
}
