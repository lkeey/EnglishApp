package com.example.englishapp.repositories;

import static android.content.Context.MODE_PRIVATE;
import static com.example.englishapp.database.Constants.KEY_ALREADY_LEARNING;
import static com.example.englishapp.database.Constants.KEY_CARD_ID;
import static com.example.englishapp.database.Constants.KEY_LANGUAGE_CODE;
import static com.example.englishapp.database.Constants.MY_SHARED_PREFERENCES;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.example.englishapp.R;
import com.example.englishapp.database.DataBaseLearningWords;
import com.example.englishapp.database.DataBasePersonalData;
import com.example.englishapp.interfaces.CompleteListener;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class WordsRepository {

    private static final String TAG = "RepositoryWords";
    public static String translatedText;

    public void translateString(String currentWord, String languageCode, CompleteListener listener) {

        Log.i(TAG, "begin translating");

        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(languageCode)
                        .build();

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

    public Bitmap stringToBitMap(Context context, String encodedString){
        try {

            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        } catch(Exception e) {

            return BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.app_logo_large);

        }
    }

    public void endLearningWords(Context context) {

        new DataBaseLearningWords().deleteLearningWords(context, new CompleteListener() {
            @Override
            public void OnSuccess() {
                SharedPreferences sharedPreferences = context.getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();

                myEdit.putString(KEY_LANGUAGE_CODE, DataBasePersonalData.USER_MODEL.getLanguageCode());
                myEdit.putBoolean(KEY_ALREADY_LEARNING, false);
                myEdit.putString(KEY_CARD_ID, null);
                myEdit.apply();
            }

            @Override
            public void OnFailure() {
                Log.i(TAG, "can not delete words");
            }
        });
    }
}
