package com.example.englishapp.receivers;

import static android.content.Context.MODE_PRIVATE;
import static com.example.englishapp.database.Constants.KEY_LANGUAGE_CODE;
import static com.example.englishapp.database.Constants.KEY_LOCATION;
import static com.example.englishapp.database.Constants.KEY_SHOW_NOTIFICATION_WORD;
import static com.example.englishapp.database.Constants.MY_SHARED_PREFERENCES;
import static com.example.englishapp.database.Constants.WORD_COUNTER;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.englishapp.R;
import com.example.englishapp.activities.SplashActivity;
import com.example.englishapp.database.DataBaseLearningWords;
import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.models.WordModel;
import com.example.englishapp.repositories.WordsRepository;
import com.example.englishapp.services.WallpaperService;

import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {
    private final static String TAG = "ReceiverAlarm";
    private final static String CHANNEL_ID = "word_notifications";
    private String description, currentWord, languageCode;
    private Bitmap bmp;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "Received");

        boolean isSending = intent.getBooleanExtra(KEY_SHOW_NOTIFICATION_WORD, false);

        if (isSending) {
            getData(context);
        }
    }

    private void beginWorking(Context context) {

        new WordsRepository().translateString(currentWord, languageCode, new CompleteListener() {
            @Override
            public void OnSuccess() {

                Log.i(TAG, "Successfully translated");

                String title = currentWord + " - " + WordsRepository.translatedText;

                sendNotification(title, context);

                // change wallpaper
                Intent intent = new Intent(context, WallpaperService.class);
                intent.putExtra("picture", currentWord);
                context.startService(intent);
            }

            @Override
            public void OnFailure() {

                Log.i(TAG, "fail");

                sendNotification(currentWord, context);

                // change wallpaper
                Intent intent = new Intent(context, WallpaperService.class);
                intent.putExtra("picture", currentWord);
                context.startService(intent);
            }
        });
    }

    private void sendNotification(String title, Context context) {
        // must be random to always show on screen
        int NOTIFICATION_ID = new Random().nextInt();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        Notification notification = createNotification(
                context,
                title,
                description,
                bmp
        );

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        notificationManagerCompat.notify(NOTIFICATION_ID, notification);

    }

    private void getData(Context context) {
        SharedPreferences sh = context.getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE);

        // number of word to show
        int counter = sh.getInt(WORD_COUNTER, -1);

        // translated language
        languageCode = sh.getString(KEY_LANGUAGE_CODE, "ru");

        new DataBaseLearningWords().loadLearningWords(context, new CompleteListener() {
            @Override
            public void OnSuccess() {

                Log.i(TAG, "Successfully read learning words");

                WordModel wordModel = DataBaseLearningWords.LIST_OF_LEARNING_WORDS.get(counter);

                currentWord = wordModel.getTextEn();

                description = wordModel.getDescription();

                bmp = new WordsRepository().stringToBitMap(context, wordModel.getImage());

                SharedPreferences sharedPreferences = context.getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();

                if (counter == (DataBaseLearningWords.LIST_OF_LEARNING_WORDS.size() - 1)) {
                    myEdit.putInt(WORD_COUNTER, 0);
                } else {
                    myEdit.putInt(WORD_COUNTER, counter + 1);
                }

                myEdit.apply();

                beginWorking(context);
            }

            @Override
            public void OnFailure() {
                Log.i(TAG, "Can not read learning words");
            }
        });
    }

    private Notification createNotification(Context context, String title, String description, Bitmap bmp) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.getSystemService(NotificationManager.class)
                .createNotificationChannel(
                    new NotificationChannel(
                            CHANNEL_ID,
                            title,
                            NotificationManager.IMPORTANCE_DEFAULT
                    ));
        }

        // what to open after click on notification
        Intent notificationIntent = new Intent(context, SplashActivity.class);

        notificationIntent.putExtra(KEY_LOCATION, true);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setTicker(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_bubbles)
                .setLargeIcon(bmp)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bmp)
                        .bigLargeIcon(null))
                .build();

        return notification;

    }
}
