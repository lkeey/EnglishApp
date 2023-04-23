package com.example.englishapp.MVP;

import static com.example.englishapp.messaging.Constants.KEY_BOOKMARKS;
import static com.example.englishapp.messaging.Constants.KEY_COLLECTION_USERS;
import static com.example.englishapp.messaging.Constants.KEY_DOB;
import static com.example.englishapp.messaging.Constants.KEY_EMAIL;
import static com.example.englishapp.messaging.Constants.KEY_FCM_TOKEN;
import static com.example.englishapp.messaging.Constants.KEY_GENDER;
import static com.example.englishapp.messaging.Constants.KEY_MOBILE;
import static com.example.englishapp.messaging.Constants.KEY_NAME;
import static com.example.englishapp.messaging.Constants.KEY_PROFILE_IMG;
import static com.example.englishapp.messaging.Constants.KEY_SCORE;
import static com.example.englishapp.messaging.Constants.KEY_TOTAL_USERS;
import static com.example.englishapp.messaging.Constants.KEY_USER_UID;

import android.util.ArrayMap;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataBase {

    private static final String TAG = "FirestoreDB";
    public static FirebaseFirestore DATA_FIRESTORE;
    public static FirebaseAuth DATA_AUTH;
    public static FirebaseMessaging DATA_FIREBASE_MESSAGING;
    public static UserModel USER_MODEL = new UserModel("ID","NAME", "EMAIL", "DEFAULT", "PHONE", "PATH", "DATE","TOKEN", 0, 0);
    public static List<UserModel> LIST_OF_USERS = new ArrayList<>();
    public static void createUserData(String email, String name, String DOB, String gender, String mobile, String pathToImage, CompleteListener listener) {
        DATA_AUTH = FirebaseAuth.getInstance();

        Map<String, Object> userData = new ArrayMap<>();

        userData.put(KEY_USER_UID, DATA_AUTH.getCurrentUser().getUid());
        userData.put(KEY_EMAIL, email);
        userData.put(KEY_NAME, name);
        userData.put(KEY_MOBILE, mobile);
        userData.put(KEY_GENDER, gender);
        userData.put(KEY_DOB, DOB);
        userData.put(KEY_PROFILE_IMG, pathToImage);
        userData.put(KEY_SCORE, 0);
        userData.put(KEY_BOOKMARKS, 0);
        userData.put(KEY_FCM_TOKEN, DATA_FIREBASE_MESSAGING.getInstance().getToken());

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
        DATA_FIRESTORE.collection(KEY_COLLECTION_USERS).document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    try {
                        USER_MODEL.setUid(documentSnapshot.getString(KEY_USER_UID));
                        USER_MODEL.setName(documentSnapshot.getString(KEY_NAME));
                        USER_MODEL.setEmail(documentSnapshot.getString(KEY_EMAIL));
                        USER_MODEL.setMobile(documentSnapshot.getString(KEY_MOBILE));
                        USER_MODEL.setBookmarksCount(documentSnapshot.getLong(KEY_BOOKMARKS).intValue());
                        USER_MODEL.setScore(documentSnapshot.getLong(KEY_SCORE).intValue());
                        USER_MODEL.setDateOfBirth(documentSnapshot.getString(KEY_DOB));
                        USER_MODEL.setPathToImage(documentSnapshot.getString(KEY_PROFILE_IMG));
                        USER_MODEL.setGender(documentSnapshot.getString(KEY_GENDER));
                    } catch (Exception e) {
                        Log.i(TAG, e.getMessage());
                    }

                    listener.OnSuccess();
                })
                .addOnFailureListener(e -> listener.OnFailure());
    }


    public static void loadData(CompleteListener listener) {
        getUserData(new CompleteListener() {
            @Override
            public void OnSuccess() {
                Log.i(TAG, "User data loaded");
                Log.i(TAG, USER_MODEL.getUid());

                listener.OnSuccess();
            }

            @Override
            public void OnFailure() {
                Log.i(TAG, "Exception: User Data can not be loaded");

                listener.OnFailure();
            }
        });
    }

    public static void updateToken(String token, CompleteListener listener) {

        DocumentReference reference = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
                .document(USER_MODEL.getUid());

        reference.update(KEY_FCM_TOKEN, token)
                .addOnSuccessListener(unused -> listener.OnSuccess())
                .addOnFailureListener(e -> listener.OnFailure());
    }


    public static void updateImage(String path, CompleteListener listener) {

        Log.i(TAG, "Path - " + path);

        DocumentReference reference = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
            .document(DATA_AUTH.getCurrentUser().getUid());

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

    }

    public static void getListOfUsers(CompleteListener listener) {
        LIST_OF_USERS.clear();

        Log.i(TAG, "Begin loading");

        DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
                .limit(20)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.i(TAG, "Get data");
                    try {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            LIST_OF_USERS.add(
                                    new UserModel(
                                            documentSnapshot.getString(KEY_USER_UID),
                                            documentSnapshot.getString(KEY_NAME),
                                            documentSnapshot.getString(KEY_EMAIL),
                                            documentSnapshot.getString(KEY_GENDER),
                                            documentSnapshot.getString(KEY_MOBILE),
                                            documentSnapshot.getString(KEY_PROFILE_IMG),
                                            documentSnapshot.getString(KEY_DOB),
                                            documentSnapshot.getString(KEY_FCM_TOKEN),
                                            documentSnapshot.getLong(KEY_SCORE).intValue(),
                                            documentSnapshot.getLong(KEY_BOOKMARKS).intValue()
                                    ));

                            Log.i(TAG, "Created - " + documentSnapshot.getString(KEY_NAME) + " - " + documentSnapshot.getString(KEY_PROFILE_IMG));

                        }
                    } catch (Exception e) {
                        Log.i(TAG, e.getMessage());
                    }

                    Log.i(TAG, "All good");

                    listener.OnSuccess();

                })
                .addOnFailureListener(e -> listener.OnFailure());
    }
}
