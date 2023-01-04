package com.example.englishapp;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.acl.NotOwnerException;

public class MainActivity extends AppCompatActivity {

    private long backPressedTime;

    public NotificationManager notificationManager;
    public NotificationManagerCompat notificationManagerCompat;
    public Notification notification;

    public Bitmap drawTextToBitmap(Context mContext,  int resourceId,  String mText) {
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

//        change wallpaper
        int[] colors = new int[300*300];

        Bitmap bitmapAlpha1 = Bitmap.createBitmap(colors, 300, 300, Bitmap.Config.ARGB_8888);
        Bitmap bitmapAlpha2 = BitmapFactory.decodeResource(getResources(), R.drawable.main_img_user);

        Bitmap bmpNEW = drawTextToBitmap(this, R.drawable.main_img_user,"Hello Android\nHello Java");

//        String gText = "HI";
//
//        Canvas canvas = new Canvas(bitmapAlpha2); //Создаем Canvas на его основе
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);// Создаем кисть со сглаживанием
//
//        paint.setColor(Color.rgb(0, 0, 0)); //Ставим цвет
//        paint.setTextSize(24); //Ставим размер текста в пикселях
//
//        //делаем некоторые действия чтобы выяснить куда рисовать
//        Rect bounds = new Rect();//объект, в который положим размеры, которые займет наш текст
//
//        int x = (bitmapAlpha2.getWidth() - bounds.width())/2; //зная ширину и высоту текста, вычисляем координаты верхнего левого угла надписи в координатах вашего фото, поменяйте на то что вам нужно
//        int y = (bitmapAlpha2.getHeight() + bounds.height())/2;
//
//        try {
//            paint.getTextBounds(gText, 0, gText.length(), bounds); //вычисляем размеры текста, нарисованного этой кистью
//
//            canvas.drawText(gText, x, y, paint);//рисуем ваш текст в ваших координатах вашей кистью
//            FileOutputStream fos = null; //создаем поток для записи в итоговый файл
//
//            fos = new FileOutputStream("new_file");
//            bitmapAlpha2.compress(Bitmap.CompressFormat.JPEG, 90, fos);//пишем в этот поток конвертированное в JPEG ил PNG изображение
//            fos.close();//закрываем поток
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Toast.makeText(this, "Good", Toast.LENGTH_SHORT).show();

        try {

            WallpaperManager.getInstance(this).setBitmap(bmpNEW);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // go to account
        Button btnStart = findViewById(R.id.btnLogging);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(MainActivity.this, LoggingActivity.class);
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
    public void onBackPressed() {
        if(backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Click again to exit", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();

    }
}