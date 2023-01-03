package com.example.englishapp;

import android.app.Notification;
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
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ArrayActivity {
    private static Context context;
    public String[] enWords = {};
    public String[] ruWords = {};

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

    public void showNotification(int reqCode) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context.getApplicationContext(), "channel_name")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("HI")
                .setContentText("HI2")
                .setWhen(System.currentTimeMillis());

        Notification notification = notificationBuilder.build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context.getApplicationContext());
        notificationManagerCompat.notify(reqCode, notification);
    }

}

