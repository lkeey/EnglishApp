package com.example.englishapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ArrayActivity {
    private static Context context;
    public String[] enWords = {};
    public String[] ruWords = {};

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    ArrayActivity(String[] enWords, String[] ruWords) {
        this.enWords = enWords;
        this.ruWords = ruWords;
    }

    public void createWallPaper() {

        int[] colors = new int[300*300];

        Bitmap bitmapAlpha = Bitmap.createBitmap(colors, 300, 300, Bitmap.Config.ARGB_8888);

        Toast.makeText(context, "Good", Toast.LENGTH_SHORT).show();

//        try {
//            WallpaperManager.getInstance(getApplicationContext()).setBitmap(bitmapAlpha);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Toast.makeText(context, "Good2", Toast.LENGTH_SHORT).show();
    }

    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);
        String CHANNEL_ID = "channel_name";// The id of the channel.

        notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id

        Log.d("showNotification", "showNotification: " + reqCode);
    }

//    public int setStream (InputStream bitmapData,
//                          Rect visibleCropHint,
//                          boolean allowBackup)

//    private Context getApplicationContext() {
//        return ArrayActivity.context;
//    }
}

