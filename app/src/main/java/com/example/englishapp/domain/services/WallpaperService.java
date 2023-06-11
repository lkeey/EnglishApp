package com.example.englishapp.domain.services;

import static com.example.englishapp.data.database.Constants.KEY_WORD_IMG;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

public class WallpaperService extends Service {

    private static final String TAG = "ServiceWallPaper";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            String wordImg = intent.getStringExtra(KEY_WORD_IMG);

            Bitmap bmp = stringToBitMap(wordImg);

            WallpaperManager.getInstance(WallpaperService.this).setBitmap(bmp);

        } catch (Exception e) {
            Log.i(TAG, "error - " + e.getMessage());
        }

        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Wallpaper updated");
    }

    public Bitmap stringToBitMap(String encodedString){
        try {

            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        } catch(Exception e) {
            throw new RuntimeException("can not decode");
        }
    }
}
