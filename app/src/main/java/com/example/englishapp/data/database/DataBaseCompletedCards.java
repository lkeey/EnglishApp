package com.example.englishapp.data.database;

import static com.example.englishapp.data.database.DataBasePersonalData.DATA_FIRESTORE;
import static com.example.englishapp.data.database.DataBasePersonalData.USER_MODEL;

import android.util.Log;

import com.example.englishapp.domain.interfaces.CompleteListener;

public class DataBaseCompletedCards {

    private static final String TAG = "CompletedCardsDao";
    public static boolean isCompleted;

    public void checkCompletedCards(String cardId, CompleteListener listener) {

        Log.i(TAG, "check completed cards - " + cardId);

        DATA_FIRESTORE.collection(Constants.KEY_COLLECTION_USERS)
            .document(USER_MODEL.getUid())
            .collection(Constants.KEY_COLLECTION_PERSONAL_DATA)
            .document(Constants.KEY_COMPLETED_CARDS)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.getBoolean(cardId) != null) {
                    Log.i(TAG, "find");

                    isCompleted = true;

                } else {
                    Log.i(TAG, "not found");

                    isCompleted = false;

                }

                listener.OnSuccess();

            }).addOnFailureListener(e -> {
                Log.i(TAG, "error - " + e.getMessage());

                isCompleted = false;

                listener.OnFailure();
            });
    }

}
