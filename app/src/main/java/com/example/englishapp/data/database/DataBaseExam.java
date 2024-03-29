package com.example.englishapp.data.database;

import static com.example.englishapp.data.database.Constants.KEY_BOOKMARKS;
import static com.example.englishapp.data.database.Constants.KEY_COLLECTION_PERSONAL_DATA;
import static com.example.englishapp.data.database.Constants.KEY_COLLECTION_USERS;
import static com.example.englishapp.data.database.Constants.KEY_COMPLETED_CARDS;
import static com.example.englishapp.data.database.Constants.KEY_SCORE;
import static com.example.englishapp.data.database.Constants.KEY_USER_SCORES;
import static com.example.englishapp.data.database.DataBaseBookmarks.LIST_OF_BOOKMARK_IDS;
import static com.example.englishapp.data.database.DataBasePersonalData.DATA_FIRESTORE;
import static com.example.englishapp.data.database.DataBasePersonalData.USER_MODEL;

import android.util.ArrayMap;
import android.util.Log;

import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.data.models.TestModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.Map;
import java.util.Objects;

public class DataBaseExam {

    private static final String TAG = "ExamDao";
    private Map<String, Object> userData;
    private DocumentReference userDocument;
    private WriteBatch batch;
    public static final int NOT_VISITED = 0;
    public static final int UNANSWERED = 1;
    public static final int ANSWERED = 2;
    public static final int REVIEW = 3;

    public void saveResult(boolean isWordExam, String cardId, int finalScore, CompleteListener listener) {
        batch = DATA_FIRESTORE.batch();

        userDocument = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS).document(USER_MODEL.getUid());

        if (!isWordExam) {
            Map<String, Object> bookmarksData = new ArrayMap<>();

            for (int i = 0; i < LIST_OF_BOOKMARK_IDS.size(); i++) {
                bookmarksData.put("BOOKMARK" + i + "_ID", LIST_OF_BOOKMARK_IDS.get(i));
                Log.i(TAG, "bookMark - " + LIST_OF_BOOKMARK_IDS.get(i));
            }

            DocumentReference bookmarkDocument = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS).document(USER_MODEL.getUid())
                    .collection(KEY_COLLECTION_PERSONAL_DATA)
                    .document(KEY_BOOKMARKS);

            batch.set(bookmarkDocument, bookmarksData);
        }

        userDocument.get()
            .addOnSuccessListener(documentSnapshot -> {
                userData = new ArrayMap<>();

                if (!isWordExam) {

                    updateScore(finalScore, Objects.requireNonNull(documentSnapshot.getLong(KEY_SCORE)).intValue());

                } else {

                    updateScoreWord(cardId, Objects.requireNonNull(documentSnapshot.getLong(KEY_SCORE)).intValue(), finalScore);
                }

                batch.update(userDocument, userData);

                batch.commit()
                    .addOnSuccessListener(unused -> listener.OnSuccess())
                    .addOnFailureListener(e -> listener.OnFailure());
            })
            .addOnFailureListener(e -> listener.OnFailure());
    }

    private void updateScore(int finalScore, int userExperience) {
        TestModel testModel = new DataBaseTests().findTestById(DataBaseTests.CHOSEN_TEST_ID);

        if (finalScore > testModel.getTopScore()) {

            int allScore = userExperience + finalScore - testModel.getTopScore();

            Log.i(TAG, "user - " + userExperience + " - all score - " + allScore + " - top - " + testModel.getTopScore());

            userData.put(KEY_SCORE, allScore);

            DocumentReference scoreDocument = userDocument
                    .collection(KEY_COLLECTION_PERSONAL_DATA)
                    .document(KEY_USER_SCORES);

            Map<String, Object> testData = new ArrayMap<>();

            testData.put(testModel.getId(), finalScore);
            batch.set(scoreDocument, testData, SetOptions.merge());

            USER_MODEL.setScore(allScore);

        }

        userData.put(KEY_BOOKMARKS, LIST_OF_BOOKMARK_IDS.size());
    }

    private void updateScoreWord(String cardId, int userExperience, int finalScore) {
        int allScore = userExperience + finalScore;

        userData.put(KEY_SCORE, allScore);

        DocumentReference scoreDocument = userDocument
                .collection(KEY_COLLECTION_PERSONAL_DATA)
                .document(KEY_COMPLETED_CARDS);

        Map<String, Object> cardData = new ArrayMap<>();

        cardData.put(cardId, true);
        batch.set(scoreDocument, cardData, SetOptions.merge());


        USER_MODEL.setScore(allScore);
    }
}
