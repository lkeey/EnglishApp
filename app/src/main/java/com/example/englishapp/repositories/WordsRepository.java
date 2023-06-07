package com.example.englishapp.repositories;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.example.englishapp.R;
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
}
