package com.example.englishapp.activities;

import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.englishapp.R;

public class ArrayActivity {
    private static Context context;
    public String[] enWords = {};
    public String[] ruWords = {};

    ArrayActivity(String[] enWords, String[] ruWords) {
        this.enWords = enWords;
        this.ruWords = ruWords;
    }

    public void createWallPaper() {

        int[] colors = new int[300 * 300];

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

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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
    }

}

