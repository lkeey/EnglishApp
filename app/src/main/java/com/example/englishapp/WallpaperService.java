package com.example.englishapp;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class WallpaperService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // сообщение о создании службы
        Toast.makeText(this, "Service created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // сообщение о запуске службы
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();

        WallManager wallpaper = new WallManager();
        Bitmap bitmapWallpaper = null;

        try {
            bitmapWallpaper = wallpaper.execute("football").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        Toast.makeText(this, "WALL" + bitmapWallpaper.toString(), Toast.LENGTH_SHORT).show();
        
        try {
            WallpaperManager.getInstance(this).setBitmap(bitmapWallpaper);
        } catch (IOException e) {
            e.printStackTrace();
        }

        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        //сообщение об остановке службы
        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
    }
}
