package com.example.englishapp.alarm;

import static android.content.Context.MODE_PRIVATE;
import static com.example.englishapp.messaging.Constants.KEY_SHOW_NOTIFICATION_WORD;
import static com.example.englishapp.messaging.Constants.MY_SHARED_PREFERENCES;
import static com.example.englishapp.messaging.Constants.WORD_COUNTER;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.englishapp.MVP.CompleteListener;
import com.example.englishapp.MVP.MainActivity;
import com.example.englishapp.MVP.WordModel;
import com.example.englishapp.R;
import com.example.englishapp.testsAndWords.RoomDataBase;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {
    private final static String TAG = "ReceiverAlarm";
    private List<WordModel> words = new ArrayList<>();
    private String translatedText, description, currentWord;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "Received");

        boolean isChecking = intent.getBooleanExtra(KEY_SHOW_NOTIFICATION_WORD, false);

        if (isChecking) {
            sendNotification(context);
        }
    }

    private void sendNotification(Context context) {

        // must be random to always show on screen
        int NOTIFICATION_ID = new Random().nextInt();

        String CHANNEL_ID = "word_notifications"; // The id of the channel.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Log.i(TAG, "More O");

            CharSequence name = "Word Notification"; // The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(
                    CHANNEL_ID,
                    name,
                    importance
            );

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        // what to open after click on notification
        Intent notificationIntent = new Intent(context, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        SharedPreferences sh = context.getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE);
        int counter = sh.getInt(WORD_COUNTER, -1);

        words = RoomDataBase.
                getDatabase(context)
                .roomDao()
                .getAllWords();

        currentWord = words.get(counter).getTextEn();
        description = words.get(counter).getDescription();

        int allWords = RoomDataBase.
                getDatabase(context)
                .roomDao()
                .getRowCount();

        SharedPreferences sharedPreferences = context.getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        if (counter == (allWords - 1)) {

            myEdit.putInt(WORD_COUNTER, 0);

        } else {

            myEdit.putInt(WORD_COUNTER, counter + 1);

        }

        myEdit.apply();

        translateString(currentWord, new CompleteListener() {
            @Override
            public void OnSuccess() {

                Log.i(TAG, "success");

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_bubbles)
                        .setContentTitle(currentWord + " - " + translatedText)
                        .setContentText(description)
                        .setLargeIcon(stringToBitMap(context, words.get(counter).getImage()))
                        .setContentIntent(contentIntent);

                Notification notification = notificationBuilder.build();

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                notificationManagerCompat.notify(NOTIFICATION_ID, notification);
            }

            @Override
            public void OnFailure() {

                Log.i(TAG, "fail");

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_bubbles)
                        .setContentTitle(currentWord)
                        .setContentText(description)
                        .setLargeIcon(stringToBitMap(context, words.get(counter).getImage()))
                        .setContentIntent(contentIntent);

                Notification notification = notificationBuilder.build();

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                notificationManagerCompat.notify(NOTIFICATION_ID, notification);
            }
        });




    }

    private Bitmap stringToBitMap(Context context, String encodedString){
        try {

            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;

        } catch(Exception e) {

            e.getMessage();
            return BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.app_logo_large);

        }
    }

    private void translateString(String currentWord, CompleteListener listener) {

        Log.i(TAG, "begin");

        // Create an English-German translator:
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    // TODO change to target
                    .setTargetLanguage(TranslateLanguage.RUSSIAN)
                    .build();

        Log.i(TAG, "begin 2");

        final Translator englishGermanTranslator =
                Translation.getClient(options);

        try {
            englishGermanTranslator.translate(currentWord).addOnSuccessListener(s -> {
                        Log.i(TAG, "translated - " + s);

                        translatedText = s;

                        listener.OnSuccess();

                    })
                    .addOnFailureListener(e -> {
                        Log.i(TAG, "can not translate - " + e.getMessage());

                        listener.OnFailure();

                    });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }
}
