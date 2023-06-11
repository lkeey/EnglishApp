package com.example.englishapp.data.database;

import static com.example.englishapp.data.database.Constants.KEY_COLLECTION_PERSONAL_DATA;
import static com.example.englishapp.data.database.Constants.KEY_COLLECTION_USERS;
import static com.example.englishapp.data.database.Constants.KEY_USER_SCORES;
import static com.example.englishapp.data.database.DataBasePersonalData.DATA_FIRESTORE;
import static com.example.englishapp.data.database.DataBasePersonalData.USER_MODEL;

import android.util.Log;

import com.example.englishapp.domain.interfaces.CompleteListener;

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
                Log.i(TAG, "amount tests - " + DataBaseTests.LIST_OF_TESTS.size());

                for (int i = 0; i < DataBaseTests.LIST_OF_TESTS.size(); i++) {
                    int top = 0;

                    Log.i(TAG, "test id - " + DataBaseTests.LIST_OF_TESTS.get(i).getId() + " - " + DataBaseTests.LIST_OF_TESTS.get(i).getName());

                    if (documentSnapshot.getLong(DataBaseTests.LIST_OF_TESTS.get(i).getId()) != null) {
                        top = documentSnapshot.getLong(DataBaseTests.LIST_OF_TESTS.get(i).getId()).intValue();

                        Log.i(TAG, DataBaseTests.LIST_OF_TESTS.get(i).getName() + " - " + top);

                    }

                    Log.i(TAG, "top - " + top);

                    DataBaseTests.LIST_OF_TESTS.get(i).setTopScore(top);
                }
                listener.OnSuccess();
            })
            .addOnFailureListener(e -> {
                Log.i(TAG, "error while loading scores - " + e.getMessage());

                listener.OnFailure();
            });
    }

}
