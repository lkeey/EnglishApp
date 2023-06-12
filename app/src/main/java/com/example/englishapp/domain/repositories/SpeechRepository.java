package com.example.englishapp.domain.repositories;

import static com.example.englishapp.data.database.Constants.KEY_COLLECTION_USERS;
import static com.example.englishapp.data.database.DataBasePersonalData.USER_MODEL;
import static com.example.englishapp.data.database.DataBaseWords.LIST_OF_WORDS;

import android.util.Log;

import com.example.englishapp.data.database.Constants;
import com.example.englishapp.data.database.DataBasePersonalData;
import com.example.englishapp.data.database.DataBaseWords;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.Random;

public class SpeechRepository {

    private static final String TAG = "RepositorySpeech";
    public static String CHOSEN_WORD;

    public void addScore(CompleteListener listener) {
        USER_MODEL.setScore(USER_MODEL.getScore() + 25);

        DocumentReference reference = DataBasePersonalData.DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
                .document(DataBasePersonalData.USER_MODEL.getUid());

        reference.update(Constants.KEY_SCORE, USER_MODEL.getScore())
            .addOnSuccessListener(unused -> {

                Log.i(TAG, "updated score - " + USER_MODEL.getScore());

                listener.OnSuccess();
            })
            .addOnFailureListener(e -> {
                Log.i(TAG, "error - " + e.getMessage());
                listener.OnFailure();
            });
    }


    public void loadWords(CompleteListener listener) {

        DataBaseWords dataBaseWords = new DataBaseWords();

        dataBaseWords.loadWords(null, new CompleteListener() {
            @Override
            public void OnSuccess() {
                Log.i(TAG, "successfully load words");

                if (LIST_OF_WORDS.size() > 1) {

                    int number = new Random().nextInt(LIST_OF_WORDS.size() - 1);

                    if (CHOSEN_WORD != null) {
                        while (CHOSEN_WORD.equals(LIST_OF_WORDS.get(number).getTextEn())){
                            number = new Random().nextInt(LIST_OF_WORDS.size() - 1);
                        }
                    }

                    CHOSEN_WORD = LIST_OF_WORDS.get(number).getTextEn();

                    listener.OnSuccess();

                } else if (LIST_OF_WORDS.size() == 1) {

                    CHOSEN_WORD = LIST_OF_WORDS.get(0).getTextEn();

                    listener.OnSuccess();

                } else {
                    listener.OnFailure();
                }
            }

            @Override
            public void OnFailure() {
                listener.OnFailure();
            }
        });
    }


}
