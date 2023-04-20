package com.example.englishapp.MVP;

import static com.example.englishapp.messaging.Constants.KEY_BOOKMARKS;
import static com.example.englishapp.messaging.Constants.KEY_COLLECTION_USERS;
import static com.example.englishapp.messaging.Constants.KEY_DOB;
import static com.example.englishapp.messaging.Constants.KEY_EMAIL;
import static com.example.englishapp.messaging.Constants.KEY_GENDER;
import static com.example.englishapp.messaging.Constants.KEY_MOBILE;
import static com.example.englishapp.messaging.Constants.KEY_NAME;
import static com.example.englishapp.messaging.Constants.KEY_PROFILE_IMG;
import static com.example.englishapp.messaging.Constants.KEY_SCORE;
import static com.example.englishapp.messaging.Constants.KEY_TOTAL_USERS;

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
    public static UserModel USER_MODEL = new UserModel("NAME", "EMAIL", "DEFAULT", "PHONE", null, null,0, 0);

    public static void createUserData(String email, String name, String DOB, String gender, String mobile, String pathToImage, CompleteListener listener) {

        Map<String, Object> userData = new ArrayMap<>();

        userData.put(KEY_EMAIL, email);
        userData.put(KEY_NAME, name);
        userData.put(KEY_MOBILE, mobile);
        userData.put(KEY_GENDER, gender);
        userData.put(KEY_DOB, DOB);
        userData.put(KEY_PROFILE_IMG, pathToImage);
        userData.put(KEY_SCORE, 0);
        userData.put(KEY_BOOKMARKS, 0);

        DocumentReference userDoc = DATA_FIRESTORE
                .collection(KEY_COLLECTION_USERS)
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        WriteBatch batch = DATA_FIRESTORE.batch();

        batch.set(userDoc, userData);

        DocumentReference docReference = DATA_FIRESTORE
                .collection(KEY_COLLECTION_USERS)
                .document(KEY_TOTAL_USERS);

        batch.update(docReference, KEY_TOTAL_USERS, FieldValue.increment(1));

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

    public static void getUserData(CompleteListener listener) {
        DATA_FIRESTORE.collection(KEY_COLLECTION_USERS).document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    USER_MODEL.setName(documentSnapshot.getString(KEY_NAME));
                    USER_MODEL.setEmail(documentSnapshot.getString(KEY_EMAIL));
                    USER_MODEL.setMobile(documentSnapshot.getString(KEY_MOBILE));
                    USER_MODEL.setBookmarksCount(documentSnapshot.getLong(KEY_BOOKMARKS).intValue());
                    USER_MODEL.setScore(documentSnapshot.getLong(KEY_SCORE).intValue());
                    USER_MODEL.setDateOfBirth(documentSnapshot.getString(KEY_DOB));
                    USER_MODEL.setPathToImage(documentSnapshot.getString(KEY_PROFILE_IMG));
                    USER_MODEL.setGender(documentSnapshot.getString(KEY_GENDER));

                    listener.OnSuccess();
                })
                .addOnFailureListener(e -> listener.OnFailure());
    }


    public static void loadData(CompleteListener listener) {
        getUserData(new CompleteListener() {
            @Override
            public void OnSuccess() {
                listener.OnSuccess();
            }

            @Override
            public void OnFailure() {
                Log.i(TAG, "Exception: User Data can not be loaded");

                listener.OnFailure();
            }
        });
    }


}
