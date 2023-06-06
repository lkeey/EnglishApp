package com.example.englishapp.database;

import static com.example.englishapp.database.Constants.KEY_AMOUNT_DISCUSSIONS;
import static com.example.englishapp.database.Constants.KEY_AMOUNT_SENT_MESSAGES;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_CHAT;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_CONVERSATION;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_STATISTICS;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_USERS;
import static com.example.englishapp.database.Constants.KEY_FCM_TOKEN;
import static com.example.englishapp.database.Constants.KEY_LOCATION;
import static com.example.englishapp.database.DataBasePersonalData.DATA_FIRESTORE;
import static com.example.englishapp.database.DataBasePersonalData.USER_MODEL;
import static com.example.englishapp.database.DataBaseUsers.LIST_OF_USERS;

import android.util.Log;

import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.models.UserModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;

public class DataBase {
    private static final String TAG = "FirestoreDB";
    public static String CURRENT_CONVERSATION_ID = null;

    public void loadData(CompleteListener listener) {
        Log.i(TAG, "Load Data");

        DataBasePersonalData dataBasePersonalData = new DataBasePersonalData();

        DataBaseCategories dataBaseCategories = new DataBaseCategories();

        DataBaseUsers dataBaseUsers = new DataBaseUsers();

        dataBasePersonalData.getUserData(new CompleteListener() {
            @Override
            public void OnSuccess() {
                Log.i(TAG, "User data was loaded");
                dataBaseUsers.getListOfUsers(new CompleteListener() {
                    @Override
                    public void OnSuccess() {
                        Log.i(TAG, "Users were successfully loaded");
                        dataBaseCategories.getListOfCategories(new CompleteListener() {
                            @Override
                            public void OnSuccess() {
                                Log.i(TAG, "Categories were successfully loaded");

                                new DataBaseBookmarks().loadBookmarkIds(new CompleteListener() {
                                    @Override
                                    public void OnSuccess() {
                                        Log.i(TAG, "bookmarked successfully loaded");
                                        listener.OnSuccess();
                                    }

                                    @Override
                                    public void OnFailure() {
                                        Log.i(TAG, "can not load bookmark ids");
                                        listener.OnFailure();
                                    }
                                });
                            }
                            @Override
                            public void OnFailure() {
                                Log.i(TAG, "Can not load categories");
                                listener.OnFailure();
                            }
                        });
                    }

                    @Override
                    public void OnFailure() {
                        Log.i(TAG, "Exception: User Data can not be loaded");
                        listener.OnFailure();
                    }
                });

            }
            @Override
            public void OnFailure() {
                Log.i(TAG, "Can not load users");
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

    public static void sendMessage(HashMap<String, Object> mapMsg, CompleteListener listener) {

        DATA_FIRESTORE.collection(KEY_COLLECTION_CHAT).add(mapMsg)
                .addOnSuccessListener(documentReference -> {

                    WriteBatch batch = DATA_FIRESTORE.batch();

                    DocumentReference docReference = DATA_FIRESTORE
                            .collection(KEY_COLLECTION_STATISTICS)
                            .document(KEY_AMOUNT_SENT_MESSAGES);

                    // increment amount of messages
                    batch.update(docReference, KEY_AMOUNT_SENT_MESSAGES, FieldValue.increment(1));

                    batch.commit()
                        .addOnSuccessListener(unused -> listener.OnSuccess())
                        .addOnFailureListener(e -> {
                            listener.OnFailure();
                        });

                })
                .addOnFailureListener(e -> listener.OnFailure());
    }

    public static void addConversation(HashMap<String, Object> conversation, CompleteListener listener) {
        DATA_FIRESTORE.collection(KEY_COLLECTION_CONVERSATION)
                .add(conversation)
                .addOnSuccessListener(documentReference -> {

                    CURRENT_CONVERSATION_ID = documentReference.getId();

                    WriteBatch batch = DATA_FIRESTORE.batch();

                    DocumentReference docReference = DATA_FIRESTORE
                            .collection(KEY_COLLECTION_STATISTICS)
                            .document(KEY_AMOUNT_DISCUSSIONS);

                    // increment amount of discussions
                    batch.update(docReference, KEY_AMOUNT_DISCUSSIONS, FieldValue.increment(1));

                    batch.commit()
                        .addOnSuccessListener(unused -> listener.OnSuccess())
                        .addOnFailureListener(e -> listener.OnFailure());
                });
    }

    public static UserModel findUserById(String userUID) {

        Log.i(TAG, "Amount users - " + LIST_OF_USERS.size());

        return LIST_OF_USERS.stream().filter(user -> user.getUid().equals(userUID)).findAny()
                .orElseThrow(() -> new RuntimeException("not found"));
    }

    public static void updateUserGeoPosition(double latitude, double longitude, CompleteListener listener) {

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

            Log.i(TAG, "the same geo");

            listener.OnFailure();
        }
    }



}
