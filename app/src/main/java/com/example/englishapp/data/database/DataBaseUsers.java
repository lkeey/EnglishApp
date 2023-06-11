package com.example.englishapp.data.database;

import static com.example.englishapp.data.database.Constants.KEY_BOOKMARKS;
import static com.example.englishapp.data.database.Constants.KEY_COLLECTION_USERS;
import static com.example.englishapp.data.database.Constants.KEY_DOB;
import static com.example.englishapp.data.database.Constants.KEY_EMAIL;
import static com.example.englishapp.data.database.Constants.KEY_FCM_TOKEN;
import static com.example.englishapp.data.database.Constants.KEY_GENDER;
import static com.example.englishapp.data.database.Constants.KEY_LOCATION;
import static com.example.englishapp.data.database.Constants.KEY_MOBILE;
import static com.example.englishapp.data.database.Constants.KEY_NAME;
import static com.example.englishapp.data.database.Constants.KEY_PROFILE_IMG;
import static com.example.englishapp.data.database.Constants.KEY_SCORE;
import static com.example.englishapp.data.database.Constants.KEY_USER_UID;
import static com.example.englishapp.data.database.DataBasePersonalData.DATA_FIRESTORE;
import static com.example.englishapp.data.database.DataBasePersonalData.USER_MODEL;

import android.util.Log;

import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.data.models.UserModel;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataBaseUsers {
    private static final String TAG = "DataBaseUsers";
    public static List<UserModel> LIST_OF_USERS = new ArrayList<>();

    public void getListOfUsers(CompleteListener listener) {
        LIST_OF_USERS.clear();

        Log.i(TAG, "Begin loading");

        DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
            .orderBy(KEY_SCORE, Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                Log.i(TAG, "Get data");
                try {
                    int place = 1;

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
                        userModel.setScore(Objects.requireNonNull(documentSnapshot.getLong(KEY_SCORE)).intValue());
                        userModel.setBookmarksCount(Objects.requireNonNull(documentSnapshot.getLong(KEY_BOOKMARKS)).intValue());
                        userModel.setLatitude(Objects.requireNonNull(documentSnapshot.getGeoPoint(KEY_LOCATION)).getLatitude());
                        userModel.setLongitude(Objects.requireNonNull(documentSnapshot.getGeoPoint(KEY_LOCATION)).getLongitude());
                        userModel.setPlace(place);

                        LIST_OF_USERS.add(userModel);

                        // set place for current user

                        if (userModel.getUid().equals(USER_MODEL.getUid())) {
                            USER_MODEL.setPlace(place);
                        }

                        place++;

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
    public UserModel findUserById(String userUID) {

        Log.i(TAG, "Amount users - " + LIST_OF_USERS.size());

        return LIST_OF_USERS.stream().filter(user -> user.getUid().equals(userUID)).findAny()
                .orElseThrow(() -> new RuntimeException("not found"));
    }


}
