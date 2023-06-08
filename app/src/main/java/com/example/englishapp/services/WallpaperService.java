package com.example.englishapp.services;

import static com.example.englishapp.database.Constants.GOOGLE_API_KEY;
import static com.example.englishapp.database.Constants.GOOGLE_CX;
import static com.example.englishapp.database.Constants.KEY_WORD_TEXT_EN;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.englishapp.interfaces.GoogleService;
import com.example.englishapp.models.GoogleResults;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WallpaperService extends Service {

    private static final String TAG = "WallPaperService";

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

        String wordText = intent.getStringExtra(KEY_WORD_TEXT_EN);

        GoogleService serviceGoogle = GoogleApi.getInstance().create(GoogleService.class);

        Call<GoogleResults> call = serviceGoogle.find(
                GOOGLE_API_KEY,
                GOOGLE_CX,
                wordText,
                "json",
                "image"
        );

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<GoogleResults> call, @NonNull Response<GoogleResults> response) {
                GoogleResults res = response.body();

                if (response.isSuccessful()) {
                    Log.i(TAG, "url - " + Objects.requireNonNull(res).getItems().get(0).getImage().getUrl());
                    try {
                        URL url = new URL(Objects.requireNonNull(res).getItems().get(0).getImage().getUrl());

                        Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                        Log.i(TAG, "image - " + image.getWidth() + " - " + image.getHeight());

                        WallpaperManager.getInstance(WallpaperService.this).setBitmap(image);

                        Log.i(TAG, "bitmap successfully set");

                    } catch (IOException e) {
                        Log.i(TAG, e.getMessage());
                    }
                } else {
                    Log.i(TAG, "Response is not successful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GoogleResults> call, @NonNull Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });
        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Wallpaper updated");
    }
}
