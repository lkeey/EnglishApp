package com.example.englishapp.database;

import static com.example.englishapp.database.Constants.KEY_COLLECTION_PERSONAL_DATA;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_USERS;
import static com.example.englishapp.database.Constants.KEY_USER_SCORES;
import static com.example.englishapp.database.DataBasePersonalData.DATA_FIRESTORE;
import static com.example.englishapp.database.DataBasePersonalData.USER_MODEL;
import static com.example.englishapp.database.DataBaseTests.LIST_OF_TESTS;

import android.util.Log;

import com.example.englishapp.interfaces.CompleteListener;

public class DataBaseScores {

    private static final String TAG = "ScoresDao";

    public void loadMyScores(CompleteListener listener) {
        Log.i(TAG, "load scores");

        DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
            .document(USER_MODEL.getUid())
            .collection(KEY_COLLECTION_PERSONAL_DATA)
            .document(KEY_USER_SCORES)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                Log.i(TAG, "amount tests - " + LIST_OF_TESTS.size());

                for (int i=0; i < LIST_OF_TESTS.size(); i++) {
                    int top = 0;

                    Log.i(TAG, "test id - " + LIST_OF_TESTS.get(i).getId() + " - " + LIST_OF_TESTS.get(i).getName());

                    if (documentSnapshot.getLong(LIST_OF_TESTS.get(i).getId()) != null) {
                        top = documentSnapshot.getLong(LIST_OF_TESTS.get(i).getId()).intValue();

                        Log.i(TAG, LIST_OF_TESTS.get(i).getName() + " - " + top);

                    }

                    Log.i(TAG, "top - " + top);

                    LIST_OF_TESTS.get(i).setTopScore(top);
                }
                listener.OnSuccess();
            })
            .addOnFailureListener(e -> {
                Log.i(TAG, "error while loading scores - " + e.getMessage());

                listener.OnFailure();
            });
    }

}
