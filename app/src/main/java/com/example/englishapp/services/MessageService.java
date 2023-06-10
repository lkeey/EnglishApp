package com.example.englishapp.services;

import static com.example.englishapp.database.Constants.REMOTE_MSG_DATA;
import static com.example.englishapp.database.Constants.REMOTE_MSG_TITLE;
import static com.example.englishapp.database.Constants.REMOTE_MSG_USER_SENDER;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.englishapp.activities.SplashActivity;
import com.example.englishapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

public class MessageService extends FirebaseMessagingService {

    private static final String TAG = "ServiceMessage";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d(TAG, "New Token - " + token);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        try {
            Log.i(TAG, "Message " + remoteMessage);

            Map<String, String> params = remoteMessage.getData();
            JSONObject object = new JSONObject(params);
            Log.i(TAG, object.toString());

//          show toast in ui thread
//            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(MessageService.this, "My Awesome service toast...", Toast.LENGTH_SHORT).show());

//        send notification

//            must be random to always show on screen
            int NOTIFICATION_ID = new Random().nextInt();

            String CHANNEL_ID = "message_notifications";// The id of the channel.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                Log.i(TAG, "More O");

                CharSequence name = "Message Notification";// The user-visible name of the channel.
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel mChannel = new NotificationChannel(
                        CHANNEL_ID,
                        name,
                        importance
                );

                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MessageService.this);

            // what to open after click on notification
            Intent notificationIntent = new Intent(getApplicationContext(), SplashActivity.class);

            notificationIntent.putExtra(REMOTE_MSG_USER_SENDER, remoteMessage.getData().get(REMOTE_MSG_USER_SENDER));

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent contentIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), notificationIntent, PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MessageService.this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_send)
                    // to close after click
                    .setAutoCancel(true)
                    .setContentTitle(remoteMessage.getData().get(REMOTE_MSG_TITLE))
                    .setContentText(remoteMessage.getData().get(REMOTE_MSG_DATA))
                    .setContentIntent(contentIntent);

            Notification notification = notificationBuilder.build();

            Log.i(TAG, "1");

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                Log.i(TAG, "tue");

                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            Log.i(TAG, "2");


            notificationManagerCompat.notify(NOTIFICATION_ID, notification);

            Log.i(TAG, "3");


            super.onMessageReceived(remoteMessage);


        } catch (Exception e) {
            Log.i(TAG, "ERROR - "  + e.getMessage());
        }
    }
}
