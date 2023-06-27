package com.example.englishapp.data.repositories;

import static com.example.englishapp.data.database.DataBasePersonalData.DATA_FIRESTORE;

import android.util.Log;

import com.example.englishapp.data.database.Constants;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

public class DeleteUserRepository {

    private static final String TAG = "DeleteUserRepository";
    private FirebaseUser user;

    public void deleteUser(CompleteListener listener) {
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.delete().addOnSuccessListener(unused -> {
                deleteUserData(listener);

                listener.OnSuccess();
            }).addOnFailureListener(e -> listener.OnFailure());
        }
    }

    public void deleteUserData(CompleteListener listener) {
        DocumentReference userDoc = DATA_FIRESTORE
                .collection(Constants.KEY_COLLECTION_USERS)
                .document(user.getUid());

        userDoc.delete()
            .addOnSuccessListener(unused -> listener.OnSuccess())
            .addOnFailureListener(e -> {
                Log.i(TAG, "error - " + e.getMessage());
                listener.OnFailure();
        });
    }

}
