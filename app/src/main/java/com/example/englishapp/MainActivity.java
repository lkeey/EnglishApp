package com.example.englishapp;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {

    private long backPressedTime;
    public NotificationManager notificationManager;
    public NotificationManagerCompat notificationManagerCompat;
    public Notification notification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

//        send notification
        int reqCode = 1;
        Intent intentNew = new Intent(getApplicationContext(), MainActivity.class);

        String CHANNEL_ID = "channel_name";// The id of the channel.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManagerCompat = NotificationManagerCompat.from(this);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_name")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Title")
                .setContentText("Something text")
                .setWhen(System.currentTimeMillis() + 3000);

        notification = notificationBuilder.build();
        notificationManagerCompat.notify(reqCode, notification);

        Toast.makeText(this, "Good", Toast.LENGTH_SHORT).show();


        //   BEGIN IMAGE SEARCH

//        Intent intent = new Intent(this, WallpaperService.class);
//        startService(intent);

        Toast.makeText(this, "OKEY - BEGIN", Toast.LENGTH_SHORT).show();

        // END GOOGLE IMAGE SEARCH

        // go to account
        Button btnStart = findViewById(R.id.btnLogging);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(MainActivity.this, GreetingActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {

                }
            }
        });

        //begin Learning
        Button btnLearning = findViewById(R.id.btnStart);
        btnLearning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(MainActivity.this, LoggingActivity.class);
                    startActivity(intent);
                    finish();

                    ArrayActivity ac = new ArrayActivity(
                            new String[]{"awd"}, new String[]{"AWd"}
                    );

                    Toast.makeText(MainActivity.this, "Good2", Toast.LENGTH_SHORT).show();

//                    ac.showNotification(reqCode);

//                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), "channel_name")
//                            .setSmallIcon(R.drawable.ic_launcher_background)
//                            .setContentTitle("HI")
//                            .setContentText("HI2")
//                            .setWhen(System.currentTimeMillis());
//
//                    notification = notificationBuilder.build();
//                    notificationManagerCompat.notify(reqCode, notification);

                    Toast.makeText(MainActivity.this, "Good4", Toast.LENGTH_SHORT).show();

                } catch ( Exception e) {

                }
            }
        });

        //Pass the Exam
        Button btnCheck = findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(MainActivity.this, CheckingActivity.class);
                    startActivity(intent);
                    finish();
                } catch ( Exception e) {

                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, WallpaperService.class);
        stopService(intent);
    }

    @Override
    public void onBackPressed() {
        if(backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Click again to exit", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();

    }

    public Bitmap drawTextToBitmap(Context mContext, int resourceId, String mText) {
        try {
            Resources resources = mContext.getResources();
            float scale = resources.getDisplayMetrics().density;
            Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId);
            android.graphics.Bitmap.Config bitmapConfig =   bitmap.getConfig();

            // set default bitmap config if none
            if(bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }

            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            // new antialised Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            paint.setColor(Color.rgb(110,110, 110));
            // text size in pixels
            paint.setTextSize((int) (12 * scale));
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width())/6;
            int y = (bitmap.getHeight() + bounds.height())/5;

            canvas.drawText(mText, x * scale, y * scale, paint);
            canvas.drawText(mText, x * scale, (y+bounds.height()) * scale, paint);

            return bitmap;

        } catch (Exception e) {
            return null;
        }
    }
}