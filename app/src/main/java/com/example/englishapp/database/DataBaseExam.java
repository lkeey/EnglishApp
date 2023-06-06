package com.example.englishapp.database;

import static com.example.englishapp.database.Constants.KEY_BOOKMARKS;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_PERSONAL_DATA;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_USERS;
import static com.example.englishapp.database.Constants.KEY_SCORE;
import static com.example.englishapp.database.Constants.KEY_USER_SCORES;
import static com.example.englishapp.database.DataBaseBookmarks.LIST_OF_BOOKMARK_IDS;
import static com.example.englishapp.database.DataBasePersonalData.DATA_FIRESTORE;
import static com.example.englishapp.database.DataBasePersonalData.USER_MODEL;
import static com.example.englishapp.database.DataBaseTests.CHOSEN_TEST_ID;

import android.util.ArrayMap;
import android.util.Log;

import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.models.TestModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.Map;

public class DataBaseExam {

    private static final String TAG = "ExamDao";

    public void saveResult(int finalScore, CompleteListener listener) {
        WriteBatch batch = DATA_FIRESTORE.batch();

        DocumentReference userDocument = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS).document(USER_MODEL.getUid());

        Map<String, Object> bookmarksData = new ArrayMap<>();

        for (int i=0; i < LIST_OF_BOOKMARK_IDS.size(); i++) {
            bookmarksData.put("BOOKMARK" + i + "_ID", LIST_OF_BOOKMARK_IDS.get(i));
            Log.i(TAG, "bookMark - " + LIST_OF_BOOKMARK_IDS.get(i));
        }

        DocumentReference bookmarkDocument = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS).document(USER_MODEL.getUid())
                .collection(KEY_COLLECTION_PERSONAL_DATA)
                .document(KEY_BOOKMARKS);

        batch.set(bookmarkDocument, bookmarksData);

        userDocument.get()
            .addOnSuccessListener(documentSnapshot -> {
                Map<String, Object> userData = new ArrayMap<>();

                TestModel testModel = new DataBaseTests().findTestById(CHOSEN_TEST_ID);

                if (finalScore > testModel.getTopScore()) {

                    int userExperience = documentSnapshot.getLong(KEY_SCORE).intValue();
                    int allScore = userExperience + finalScore - testModel.getTopScore();

                    Log.i(TAG, "user - " + userExperience + " - all score - " + allScore + " - top - " + testModel.getTopScore());

                    userData.put(KEY_SCORE, allScore);

                    DocumentReference scoreDocument = userDocument.collection(KEY_COLLECTION_PERSONAL_DATA).document(KEY_USER_SCORES);

                    Map<String, Object> testData = new ArrayMap<>();

                    testData.put(testModel.getId(), finalScore);
                    batch.set(scoreDocument, testData, SetOptions.merge());

                    USER_MODEL.setScore(allScore);

                }

                userData.put(KEY_BOOKMARKS, LIST_OF_BOOKMARK_IDS.size());

                batch.update(userDocument, userData);

                batch.commit()
                        .addOnSuccessListener(unused -> {

                            listener.OnSuccess();
                        })
                        .addOnFailureListener(e -> listener.OnFailure());
            })
            .addOnFailureListener(e -> listener.OnFailure());
    }


}
