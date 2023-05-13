package com.example.englishapp.MVP;

import static com.example.englishapp.messaging.Constants.KEY_AMOUNT_CATEGORIES;
import static com.example.englishapp.messaging.Constants.KEY_AMOUNT_DISCUSSIONS;
import static com.example.englishapp.messaging.Constants.KEY_AMOUNT_SENT_MESSAGES;
import static com.example.englishapp.messaging.Constants.KEY_BOOKMARKS;
import static com.example.englishapp.messaging.Constants.KEY_CATEGORY_ID;
import static com.example.englishapp.messaging.Constants.KEY_CATEGORY_NAME;
import static com.example.englishapp.messaging.Constants.KEY_CATEGORY_NUMBER_OF_TESTS;
import static com.example.englishapp.messaging.Constants.KEY_COLLECTION_CATEGORIES;
import static com.example.englishapp.messaging.Constants.KEY_COLLECTION_CHAT;
import static com.example.englishapp.messaging.Constants.KEY_COLLECTION_CONVERSATION;
import static com.example.englishapp.messaging.Constants.KEY_COLLECTION_STATISTICS;
import static com.example.englishapp.messaging.Constants.KEY_COLLECTION_USERS;
import static com.example.englishapp.messaging.Constants.KEY_DOB;
import static com.example.englishapp.messaging.Constants.KEY_EMAIL;
import static com.example.englishapp.messaging.Constants.KEY_FCM_TOKEN;
import static com.example.englishapp.messaging.Constants.KEY_GENDER;
import static com.example.englishapp.messaging.Constants.KEY_LOCATION;
import static com.example.englishapp.messaging.Constants.KEY_MOBILE;
import static com.example.englishapp.messaging.Constants.KEY_NAME;
import static com.example.englishapp.messaging.Constants.KEY_PROFILE_IMG;
import static com.example.englishapp.messaging.Constants.KEY_SCORE;
import static com.example.englishapp.messaging.Constants.KEY_TOTAL_USERS;
import static com.example.englishapp.messaging.Constants.KEY_USER_UID;

import android.util.ArrayMap;
import android.util.Log;

import com.example.englishapp.Authentication.CategoryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.messaging.FirebaseMessaging;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBase {

    private static final String TAG = "FirestoreDB";
    public static String CURRENT_CONVERSATION_ID = null;
    public static FirebaseFirestore DATA_FIRESTORE;
    public static FirebaseAuth DATA_AUTH;
    public static FirebaseMessaging DATA_FIREBASE_MESSAGING;
    public static UserModel USER_MODEL = new UserModel(null,null, null, null, null, null, null,null, 0, 0, 1, 1);
    public static List<UserModel> LIST_OF_USERS = new ArrayList<>();
    public static List<CategoryModel> LIST_OF_CATEGORIES = new ArrayList<>();

    public static void createUserData(String email, String name, String DOB, String gender, String mobile, String pathToImage, CompleteListener listener) {
        DATA_AUTH = FirebaseAuth.getInstance();

        Map<String, Object> userData = new ArrayMap<>();

        userData.put(KEY_USER_UID, DATA_AUTH.getCurrentUser().getUid());
        userData.put(KEY_EMAIL, email);

        if (name != null) {
            userData.put(KEY_NAME, name);
        } else {
            userData.put(KEY_NAME, FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

        userData.put(KEY_MOBILE, mobile);
        userData.put(KEY_GENDER, gender);
        userData.put(KEY_DOB, DOB);
        userData.put(KEY_PROFILE_IMG, pathToImage);
        userData.put(KEY_SCORE, 0);
        userData.put(KEY_BOOKMARKS, 0);

        DATA_FIREBASE_MESSAGING.getInstance().getToken()
                .addOnSuccessListener(s -> userData.put(KEY_FCM_TOKEN, s))
                .addOnFailureListener(e -> Log.i(TAG, e.getMessage()));

        userData.put(KEY_LOCATION, new GeoPoint(0, 0));

        // set default image
        userData.put(KEY_PROFILE_IMG, "https://firebasestorage.googleapis.com/v0/b/englishapp-341d3.appspot.com/o/PROFILE_IMAGES%2Fno-image.jpg?alt=media&token=eaa4fa62-9cc9-4dbd-b300-96a61a3955a6");

        DocumentReference userDoc = DATA_FIRESTORE
                .collection(KEY_COLLECTION_USERS)
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        WriteBatch batch = DATA_FIRESTORE.batch();

        batch.set(userDoc, userData);

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

    public static void getUserData(CompleteListener listener) {
        DATA_FIRESTORE.collection(KEY_COLLECTION_USERS).document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    try {
                        USER_MODEL.setUid(documentSnapshot.getString(KEY_USER_UID));
                        USER_MODEL.setName(documentSnapshot.getString(KEY_NAME));
                        USER_MODEL.setEmail(documentSnapshot.getString(KEY_EMAIL));
                        USER_MODEL.setMobile(documentSnapshot.getString(KEY_MOBILE));
                        USER_MODEL.setFcmToken(documentSnapshot.getString(KEY_FCM_TOKEN));
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
        Log.i(TAG, "Load Data");

        getListOfUsers(new CompleteListener() {
            @Override
            public void OnSuccess() {
                Log.i(TAG, "Users loaded");
                getUserData(new CompleteListener() {
                    @Override
                    public void OnSuccess() {
                        Log.i(TAG, "User data loaded");
                        getListOfCategories(new CompleteListener() {
                            @Override
                            public void OnSuccess() {
                                Log.i(TAG, "Categories were successfully loaded");
                                listener.OnSuccess();
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


    public static void updateImage(String path, CompleteListener listener) {

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

                        UserModel userModel = new UserModel();

                        userModel.setUid(documentSnapshot.getString(KEY_USER_UID));
                        userModel.setName(documentSnapshot.getString(KEY_NAME));
                        userModel.setEmail(documentSnapshot.getString(KEY_EMAIL));
                        userModel.setGender(documentSnapshot.getString(KEY_GENDER));
                        userModel.setMobile(documentSnapshot.getString(KEY_MOBILE));
                        userModel.setPathToImage(documentSnapshot.getString(KEY_PROFILE_IMG));
                        userModel.setDateOfBirth(documentSnapshot.getString(KEY_DOB));
                        userModel.setFcmToken(documentSnapshot.getString(KEY_FCM_TOKEN));
                        userModel.setScore(documentSnapshot.getLong(KEY_SCORE).intValue());
                        userModel.setBookmarksCount(documentSnapshot.getLong(KEY_BOOKMARKS).intValue());
                        userModel.setLatitude(documentSnapshot.getGeoPoint(KEY_LOCATION).getLatitude());
                        userModel.setLongitude(documentSnapshot.getGeoPoint(KEY_LOCATION).getLongitude());
//                        LIST_OF_USERS.add(
//                                new UserModel(
//                                        documentSnapshot.getString(KEY_USER_UID),
//                                        documentSnapshot.getString(KEY_NAME),
//                                        documentSnapshot.getString(KEY_EMAIL),
//                                        documentSnapshot.getString(KEY_GENDER),
//                                        documentSnapshot.getString(KEY_MOBILE),
//                                        documentSnapshot.getString(KEY_PROFILE_IMG),
//                                        documentSnapshot.getString(KEY_DOB),
//                                        documentSnapshot.getString(KEY_FCM_TOKEN),
//                                        documentSnapshot.getLong(KEY_SCORE).intValue(),
//                                        documentSnapshot.getLong(KEY_BOOKMARKS).intValue(),
//                                        documentSnapshot.getLong(KEY_LATITUDE).doubleValue(),
//                                        documentSnapshot.getLong(KEY_LONGITUDE).doubleValue()
//                                ));
                        LIST_OF_USERS.add(userModel);

                        Log.i(TAG, "Created - " + userModel.getName() + " - " + userModel.getUid());

                    }
                } catch (Exception e) {
                    Log.i(TAG, e.getMessage());
                }

                Log.i(TAG, "All good");

                listener.OnSuccess();

            })
            .addOnFailureListener(e -> listener.OnFailure());
    }

    public static void getListOfCategories(CompleteListener listener) {
        LIST_OF_CATEGORIES.clear();

        Log.i(TAG, "Begin loading categories");

        DATA_FIRESTORE.collection(KEY_COLLECTION_CATEGORIES)
            .limit(20)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                Log.i(TAG, "Get category");

                try {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        CategoryModel categoryModel = new CategoryModel();

                        categoryModel.setId(documentSnapshot.getString(KEY_CATEGORY_ID));
                        categoryModel.setName(documentSnapshot.getString(KEY_CATEGORY_NAME));
                        categoryModel.setNumberOfTests(documentSnapshot.getLong(KEY_CATEGORY_NUMBER_OF_TESTS).intValue());

                        LIST_OF_CATEGORIES.add(categoryModel);

                        Log.i(TAG, "Created - " + categoryModel.getName() + " - " + categoryModel.getId());

                    }
                } catch (Exception e) {
                    Log.i(TAG, "Category error - " + e.getMessage());
                }

                Log.i(TAG, "All good");

                listener.OnSuccess();

            })
            .addOnFailureListener(e -> listener.OnFailure());
    }

    public static void createCategory(String name, CompleteListener listener) {
        Map<String, Object> categoryData = new ArrayMap<>();

//        String randomID = new Random()
//                .ints(0, 99)
//                .limit(20)
//                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
//                .toString();
        String randomID = null;

        while (true) {
            try {

                randomID = RandomStringUtils.random(20, true, true);

                Log.i(TAG, "random id - " + randomID);

                findCategoryById(randomID);

            } catch (Exception e) {
                Log.i(TAG, "not found category");

                break;
            }
        }

        categoryData.put(KEY_CATEGORY_ID, randomID);
        categoryData.put(KEY_CATEGORY_NAME, name);
        categoryData.put(KEY_CATEGORY_NUMBER_OF_TESTS, 0);

        WriteBatch batch = DATA_FIRESTORE.batch();

        DocumentReference categoryDocument = DATA_FIRESTORE
                .collection(KEY_COLLECTION_CATEGORIES)
                .document(randomID);


        batch.set(categoryDocument, categoryData);

        DocumentReference docReference = DATA_FIRESTORE
                .collection(KEY_COLLECTION_STATISTICS)
                .document(KEY_AMOUNT_CATEGORIES);

        batch.update(docReference, KEY_AMOUNT_CATEGORIES, FieldValue.increment(1));

        String randomId = randomID;
        batch.commit().addOnSuccessListener(unused -> {

            Log.i(TAG, "Category was successfully created");

            LIST_OF_CATEGORIES.add(new CategoryModel(
                    name,
                    randomId,
                    0
            ));

            listener.OnSuccess();

        }).addOnFailureListener(e -> {
            Log.i(TAG, "Can not create category - " + e.getMessage());

            listener.OnFailure();
        });
    }

    public static void updateProfileData(Map profileMap, CompleteListener listener) {

        WriteBatch batch = DATA_FIRESTORE.batch();

        DocumentReference reference = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
                .document(USER_MODEL.getUid());

        batch.update(reference, profileMap);

        batch.commit()
            .addOnSuccessListener(unused -> {
               listener.OnSuccess();
            })
            .addOnFailureListener(e -> {
                listener.OnFailure();
            });
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
                        .addOnSuccessListener(unused -> {
                            listener.OnSuccess();
                        })
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

    public static void updateUserGeoPosition(String latitude, String longitude, CompleteListener listener) {

        DocumentReference reference = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
                .document(USER_MODEL.getUid());

        reference.update(KEY_LOCATION, new GeoPoint(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                .addOnSuccessListener(unused -> listener.OnSuccess())
                .addOnFailureListener(e -> listener.OnFailure());
    }

    public static CategoryModel findCategoryById(String categoryId) {

        return LIST_OF_CATEGORIES.stream().filter(category -> category.getId().equals(categoryId)).findAny()
                .orElseThrow(() -> new RuntimeException("not found"));


    }

}
