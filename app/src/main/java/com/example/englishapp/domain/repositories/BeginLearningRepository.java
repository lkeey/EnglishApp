package com.example.englishapp.domain.repositories;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.example.englishapp.data.database.Constants.KEY_ALREADY_LEARNING;
import static com.example.englishapp.data.database.Constants.KEY_CARD_ID;
import static com.example.englishapp.data.database.Constants.KEY_LANGUAGE_CODE;
import static com.example.englishapp.data.database.Constants.KEY_SHOW_NOTIFICATION_WORD;
import static com.example.englishapp.data.database.Constants.MY_SHARED_PREFERENCES;
import static com.example.englishapp.data.database.Constants.WORD_COUNTER;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.englishapp.data.database.DataBaseCompletedCards;
import com.example.englishapp.data.database.DataBaseLearningWords;
import com.example.englishapp.data.database.DataBasePersonalData;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.domain.interfaces.LearningWordListener;
import com.example.englishapp.domain.receivers.AlarmReceiver;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class BeginLearningRepository {

    private static final String TAG = "RepositoryLearningWords";

    public void loadModel(Context context, String cardId, LearningWordListener listener) {
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(DataBasePersonalData.USER_MODEL.getLanguageCode())
                        .build();

        final Translator translator =
                Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions.Builder()
                .build();

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(unused -> {

            Log.i(TAG, "loaded");

            SharedPreferences sharedPreferences = context.getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE);

            boolean isLearning = sharedPreferences.getBoolean(KEY_ALREADY_LEARNING, false);

            new DataBaseCompletedCards().checkCompletedCards(cardId, new CompleteListener() {
                @Override
                public void OnSuccess() {
                    Log.i(TAG, "isLearning - " + isLearning + " - " + DataBaseCompletedCards.isCompleted);

                    if (!isLearning && !DataBaseCompletedCards.isCompleted) {
                        learnWords(context, cardId, listener);
                    } else if (DataBaseCompletedCards.isCompleted) {
                        listener.cancelLearning();
                    } else {
                       listener.otherLearning();
                    }
                }

                @Override
                public void OnFailure() {
                    listener.onFail();
                }
            });

        }).addOnFailureListener(e -> listener.onFail());
    }

    public void learnWords(Context context, String cardId, LearningWordListener listener) {

        Log.i(TAG, "learnWords - " + cardId);

        DataBaseLearningWords dataBaseLearningWords = new DataBaseLearningWords();
        dataBaseLearningWords.uploadLearningWords(context, cardId, new CompleteListener() {
            @Override
            public void OnSuccess() {
                try {

                    SharedPreferences sharedPreferences = context.getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();

                    try {
                        Log.i(TAG, "amount loaded words - " + DataBaseLearningWords.LIST_OF_LEARNING_WORDS.size());

                        Log.i(TAG, "Successfully set");

                        myEdit.putInt(WORD_COUNTER, 0);
                        myEdit.putString(KEY_LANGUAGE_CODE, DataBasePersonalData.USER_MODEL.getLanguageCode());
                        myEdit.putBoolean(KEY_ALREADY_LEARNING, true);
                        myEdit.putString(KEY_CARD_ID, cardId);
                        myEdit.apply();

                        // create periodic task
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

                        Intent intent = new Intent(context, AlarmReceiver.class);
                        intent.putExtra(KEY_SHOW_NOTIFICATION_WORD, true);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_MUTABLE);

                        // cancel previous
                        alarmManager.cancel(pendingIntent);

                        // every minute
                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60 * 1000, pendingIntent);

                        listener.beginLearning();

                    } catch (Exception e) {
                        listener.onFail();
                    }


                } catch (Exception e) {
                    listener.onFail();
                }
            }

            @Override
            public void OnFailure() {
                Log.i(TAG, "can not load learning words");
            }
        });

    }

}
