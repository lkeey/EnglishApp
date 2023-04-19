package com.example.englishapp.MVP;

import android.util.ArrayMap;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.Map;

public class DataBase {

    private static final String TAG = "FirestoreDB";
    public static FirebaseFirestore DATA_FIRESTORE;
    public static final String USER_COLLECTION = "USERS";
    public static final String TOTAL_OF_USERS = "TOTAL_USERS";

    public static void createUserData(String email, String name, String DOB, String gender, String mobile, CompleteListener listener) {
        Map<String, Object> userData = new ArrayMap<>();
        userData.put("EMAIL_ID", email);
        userData.put("NAME", name);
        userData.put("MOBILE", mobile);
        userData.put("GENDER", gender);
        userData.put("DOB", DOB);

        userData.put("TOTAL_SCORE", 0);
        userData.put("BOOKMARKS", 0);

        DocumentReference userDoc = DATA_FIRESTORE
                .collection(USER_COLLECTION)
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        WriteBatch batch = DATA_FIRESTORE.batch();

        batch.set(userDoc, userData);

        DocumentReference docReference = DATA_FIRESTORE
                .collection(USER_COLLECTION)
                .document(TOTAL_OF_USERS);

        batch.update(docReference, "COUNT", FieldValue.increment(1));

        batch.commit()
                .addOnSuccessListener(unused -> {
                    Log.i(TAG, "User Created");

                    listener.OnSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.i(TAG, "Fail To Create User " + e.getMessage());

                    listener.OnFailure();
                });
    }

    public static void loadData(CompleteListener listener) {
        listener.OnSuccess();
    }
}
