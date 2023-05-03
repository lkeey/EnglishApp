package com.example.englishapp.messaging;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.englishapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

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
            Log.i(TAG, "ID " + remoteMessage.getMessageId());
            Log.i(TAG, "ID S - " + remoteMessage.getSenderId());

            new Handler(Looper.getMainLooper()).post(() -> {
                Toast.makeText(MessageService.this, "My Awesome service toast...", Toast.LENGTH_SHORT).show();


            });

//        send notification
            int reqCode = 1;

            String CHANNEL_ID = "channel_name";// The id of the channel.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                Log.i(TAG, "1");

                CharSequence name = "Channel Name";// The user-visible name of the channel.
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MessageService.this);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MessageService.this, "channel_name")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Title")
                    .setContentText("Something text");

            Notification notification = notificationBuilder.build();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                Log.i(TAG, "11");

                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            notificationManagerCompat.notify(reqCode, notification);

            Log.i(TAG, "2");


            super.onMessageReceived(remoteMessage);


        } catch (Exception e) {
            Log.i(TAG, "ERROR - "  + e.getMessage());
        }
    }
}
