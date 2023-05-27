package com.example.englishapp;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

// http://android-er.blogspot.com/2015/03/draw-text-on-bitmap.html

// 2023-03-10 19:50:15.708 11208-11208/com.example.englishapp I/WallPaperService: Scale 2.625
//2023-03-10 19:50:15.718 11208-11208/com.example.englishapp I/WallPaperService: Bounds Height 31
//2023-03-10 19:50:15.718 11208-11208/com.example.englishapp I/WallPaperService: Bounds Width 211
//2023-03-10 19:50:15.718 11208-11208/com.example.englishapp I/WallPaperService: Bitmap Width 1280
//2023-03-10 19:50:15.718 11208-11208/com.example.englishapp I/WallPaperService: Bitmap Height 720
//
//
//
//
// h

public class WallpaperService extends Service {

    private static final String TAG = "WallPaperService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // сообщение о создании службы
        Log.i(TAG, "started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // сообщение о запуске службы

        WallManager wallpaper = new WallManager();
        Bitmap bitmapWallpaper = null;

        String str = intent.getStringExtra("picture");

        try {

            bitmapWallpaper = wallpaper.execute(str).get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
//            String str = "Hello Android1\nHello Java2\nHello Java3\nHello Java4\nHello Java5\nHello Java6\nHello Java7\nHello Java8\nHello Java9\nHello Java10";
            String str1 = "Hello everyone!";
            String str2 = "HEY1";
            String str3 = "HEY2";

            String strText[] = new String [] {
                    str2, str3, "HEY3", "HEY4", "HEY5", "hEY6", str, str, str, "HEY10"
            };

//            Bitmap bmpWithText = drawTextToBitmap(getApplicationContext(), bitmapWallpaper, str, strText);

//            Toast.makeText(this, "WALL" + bmpWithText.toString(), Toast.LENGTH_SHORT).show();

            WallpaperManager.getInstance(this).setBitmap(bitmapWallpaper);

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "EXCEPTION", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        //сообщение об остановке службы
        Toast.makeText(this, "Wallpaper Updated", Toast.LENGTH_SHORT).show();
    }

    public Bitmap drawTextToBitmap(Context mContext, Bitmap bitmap, String mText, String[] strText) {
        try {
            Resources resources = mContext.getResources();
            float scale = resources.getDisplayMetrics().density;
            Log.i(TAG, "Scale " + String.valueOf(scale));

//            Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId);

            android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();

            // set default bitmap config if none
            if(bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }

            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            // new antialiased Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            paint.setColor(Color.rgb(110,110, 110));
            // text size in pixels
            paint.setTextSize(10);
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);

            int x = (bitmap.getWidth() - bounds.width())/6;
            int y = (bitmap.getHeight() + bounds.height())/5;

            Log.i(TAG, "Bounds Height " + String.valueOf(bounds.height()));
            Log.i(TAG, "Bounds Width " + String.valueOf(bounds.width()));
            Log.i(TAG, "Bitmap Width " + String.valueOf(bitmap.getWidth()));
            Log.i(TAG, "Bitmap Height " + String.valueOf(bitmap.getHeight()));


            for(int i = 0; i < 9; i++) {
//                canvas.drawText(mText, x * scale, y * scale, paint);
//                String str = "Hello JAVA " + String.valueOf(i);

//                canvas.drawText(str, x * scale, (y + bounds.height()*(i)) * scale, paint);
//                Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
//                Log.i(TAG, str);

//                String str = String.valueOf(i);

                canvas.drawText(strText[i], x * scale, (y + bounds.height()*i) * scale, paint);

            }

            canvas.drawText(mText, x * scale, (y + 1) * scale, paint);

            return bitmap;

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            Toast.makeText(mContext, "EXCEPTION", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
