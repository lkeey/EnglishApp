package com.example.englishapp.database;

import static com.example.englishapp.database.Constants.KEY_BOOKMARKS;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_STATISTICS;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_USERS;
import static com.example.englishapp.database.Constants.KEY_DEFAULT_IMAGE;
import static com.example.englishapp.database.Constants.KEY_DOB;
import static com.example.englishapp.database.Constants.KEY_EMAIL;
import static com.example.englishapp.database.Constants.KEY_FCM_TOKEN;
import static com.example.englishapp.database.Constants.KEY_GENDER;
import static com.example.englishapp.database.Constants.KEY_LANGUAGE_CODE;
import static com.example.englishapp.database.Constants.KEY_LOCATION;
import static com.example.englishapp.database.Constants.KEY_MOBILE;
import static com.example.englishapp.database.Constants.KEY_NAME;
import static com.example.englishapp.database.Constants.KEY_PROFILE_IMG;
import static com.example.englishapp.database.Constants.KEY_SCORE;
import static com.example.englishapp.database.Constants.KEY_TOTAL_USERS;
import static com.example.englishapp.database.Constants.KEY_USER_UID;

import android.os.Build;
import android.util.ArrayMap;
import android.util.Log;

import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.mlkit.nl.translate.TranslateLanguage;

import java.util.Map;
import java.util.Objects;

public class DataBasePersonalData {
    private static final String TAG = "PersonalDao";
    public static FirebaseFirestore DATA_FIRESTORE;
    public static UserModel USER_MODEL = new UserModel();
    public static FirebaseAuth DATA_AUTH;

    public void getUserData(CompleteListener listener) {
        DATA_FIRESTORE.collection(KEY_COLLECTION_USERS).document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                try {

                    USER_MODEL.setUid(documentSnapshot.getString(KEY_USER_UID));
                    USER_MODEL.setName(documentSnapshot.getString(KEY_NAME));
                    USER_MODEL.setEmail(documentSnapshot.getString(KEY_EMAIL));
                    USER_MODEL.setMobile(documentSnapshot.getString(KEY_MOBILE));
                    USER_MODEL.setFcmToken(documentSnapshot.getString(KEY_FCM_TOKEN));
                    USER_MODEL.setBookmarksCount(Objects.requireNonNull(documentSnapshot.getLong(KEY_BOOKMARKS)).intValue());
                    USER_MODEL.setScore(Objects.requireNonNull(documentSnapshot.getLong(KEY_SCORE)).intValue());
                    USER_MODEL.setDateOfBirth(documentSnapshot.getString(KEY_DOB));
                    USER_MODEL.setPathToImage(documentSnapshot.getString(KEY_PROFILE_IMG));
                    USER_MODEL.setGender(documentSnapshot.getString(KEY_GENDER));
                    USER_MODEL.setLanguageCode(documentSnapshot.getString(KEY_LANGUAGE_CODE));

                } catch (Exception e) {
                    Log.i(TAG, e.getMessage());
                }

                listener.OnSuccess();
            })
            .addOnFailureListener(e -> listener.OnFailure());
    }

    public void createUserData(String email, String name, String DOB, String gender, String mobile, String pathToImage, CompleteListener listener) {
        DATA_AUTH = FirebaseAuth.getInstance();

        Map<String, Object> userData = new ArrayMap<>();

        userData.put(KEY_USER_UID, Objects.requireNonNull(DATA_AUTH.getCurrentUser()).getUid());
        userData.put(KEY_EMAIL, email);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            userData.put(KEY_NAME, Objects.requireNonNullElseGet(name, () -> Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()));
        }

        userData.put(KEY_MOBILE, mobile);
        userData.put(KEY_GENDER, gender);
        userData.put(KEY_DOB, DOB);
        userData.put(KEY_PROFILE_IMG, pathToImage);
        userData.put(KEY_SCORE, 0);
        userData.put(KEY_BOOKMARKS, 0);

        // russia default
        userData.put(KEY_LANGUAGE_CODE, TranslateLanguage.RUSSIAN);

        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(s -> userData.put(KEY_FCM_TOKEN, s))
                .addOnFailureListener(e -> Log.i(TAG, e.getMessage()));

        userData.put(KEY_LOCATION, new GeoPoint(0, 0));

        // set default image
        userData.put(KEY_PROFILE_IMG, KEY_DEFAULT_IMAGE);

        DocumentReference userDoc = DATA_FIRESTORE
                .collection(KEY_COLLECTION_USERS)
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        WriteBatch batch = DATA_FIRESTORE.batch();

        batch.set(userDoc, userData, SetOptions.merge());

        DocumentReference docReference = DATA_FIRESTORE
                .collection(KEY_COLLECTION_STATISTICS)
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

    public void updateProfileData(Map profileMap, CompleteListener listener) {

        WriteBatch batch = DATA_FIRESTORE.batch();

        DocumentReference reference = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
                .document(USER_MODEL.getUid());

        batch.update(reference, profileMap);

        batch.commit()
                .addOnSuccessListener(unused -> listener.OnSuccess())
                .addOnFailureListener(e -> listener.OnFailure());
    }

    public void updateImage(String path, CompleteListener listener) {

        Log.i(TAG, "Path - " + path);
        try {
            DocumentReference reference = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
                    .document(USER_MODEL.getUid());

            reference.update(KEY_PROFILE_IMG, path)
                    .addOnSuccessListener(unused -> {
                        Log.i(TAG, "Image was changed");

                        USER_MODEL.setPathToImage(path);
                        listener.OnSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.i(TAG, "Can not load image - " + e.getMessage());

                        listener.OnFailure();
                    });
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public void updateToken(String token, CompleteListener listener) {

        DocumentReference reference = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
                .document(USER_MODEL.getUid());

        reference.update(KEY_FCM_TOKEN, token)
                .addOnSuccessListener(unused -> listener.OnSuccess())
                .addOnFailureListener(e -> listener.OnFailure());
    }

    public void updateUserGeoPosition(double latitude, double longitude, CompleteListener listener) {

        if (latitude != USER_MODEL.getLatitude() && longitude != USER_MODEL.getLongitude()) {

            Log.i(TAG, "new geo - " + USER_MODEL.getUid());

            DocumentReference reference = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
                    .document(USER_MODEL.getUid());

            USER_MODEL.setLongitude(longitude);
            USER_MODEL.setLatitude(latitude);

            reference.update(KEY_LOCATION, new GeoPoint(latitude, longitude))
                    .addOnSuccessListener(unused -> {

                        Log.i(TAG, "updated");

                        listener.OnSuccess();

                    }).addOnFailureListener(e -> {

                        Log.i(TAG, "can not update user geo position - " + e.getMessage());

                        listener.OnFailure();

                    });

        } else {

            Log.i(TAG, "the same geo - " + latitude + " - " + USER_MODEL.getLatitude());

            listener.OnFailure();
        }
    }

}
