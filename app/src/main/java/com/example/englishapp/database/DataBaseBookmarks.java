package com.example.englishapp.database;

import static com.example.englishapp.database.Constants.KEY_ANSWER;
import static com.example.englishapp.database.Constants.KEY_BOOKMARKS;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_PERSONAL_DATA;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_QUESTIONS;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_USERS;
import static com.example.englishapp.database.Constants.KEY_NUMBER_OF_OPTIONS;
import static com.example.englishapp.database.Constants.KEY_OPTION;
import static com.example.englishapp.database.Constants.KEY_QUESTION_ID;
import static com.example.englishapp.database.Constants.KEY_TEST_QUESTION;
import static com.example.englishapp.database.DataBaseExam.NOT_VISITED;
import static com.example.englishapp.database.DataBasePersonalData.DATA_FIRESTORE;
import static com.example.englishapp.database.DataBasePersonalData.USER_MODEL;

import android.util.ArrayMap;
import android.util.Log;

import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.models.OptionModel;
import com.example.englishapp.models.QuestionModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataBaseBookmarks {

    private static final String TAG = "BookmarksDao";
    public static List<String> LIST_OF_BOOKMARK_IDS = new ArrayList<>();
    public static List<QuestionModel> LIST_OF_BOOKMARKS = new ArrayList<>();

    public void loadBookmarkIds(CompleteListener listener) {
        Log.i(TAG, "begin load bookmarks ids");

        LIST_OF_BOOKMARK_IDS.clear();

        DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
            .document(USER_MODEL.getUid())
            .collection(KEY_COLLECTION_PERSONAL_DATA)
            .document(KEY_BOOKMARKS)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                int count = USER_MODEL.getBookmarksCount();

                Log.i(TAG, "amount bookmarks - " + count);

                for(int i=0; i < count; i++) {

                    String questionId = documentSnapshot.getString("BOOKMARK" + i + "_ID");

                    LIST_OF_BOOKMARK_IDS.add(questionId);

                }

                Log.i(TAG, "found bookmarks - " + LIST_OF_BOOKMARKS.size());

                listener.OnSuccess();

            })
            .addOnFailureListener(e -> {
                listener.OnFailure();
                Log.i(TAG, "error while loading bookmarks - " + e.getMessage());
            });
    }

    public void loadBookmarks(CompleteListener listener) {
        Log.i(TAG, "begin load bookmarks");

        LIST_OF_BOOKMARKS.clear();

        for (int i=0; i < LIST_OF_BOOKMARK_IDS.size(); i++) {

            String questionId = LIST_OF_BOOKMARK_IDS.get(i);

            //load bookmarks from questions list
            int number = i;
            DATA_FIRESTORE.collection(KEY_COLLECTION_QUESTIONS)
                .document(questionId)
                .get()
                .addOnSuccessListener(document -> {

                    ArrayList<OptionModel> optionModels = new ArrayList<>();

                    // find options for question
                    for (int n = 0; n < (Objects.requireNonNull(document.getLong(KEY_NUMBER_OF_OPTIONS)).intValue()); n++) {
                        OptionModel optionModel = new OptionModel();

                        optionModel.setCorrect(n == Objects.requireNonNull(document.getLong(KEY_ANSWER)).intValue());

                        optionModel.setOption(document.getString(KEY_OPTION + "_" + n));

                        optionModels.add(optionModel);

                    }

                    QuestionModel questionModel = new QuestionModel();

                    questionModel.setQuestion(document.getString(KEY_TEST_QUESTION));
                    questionModel.setId(document.getString(KEY_QUESTION_ID));
                    questionModel.setCorrectAnswer(Objects.requireNonNull(document.getLong(KEY_ANSWER)).intValue());
                    questionModel.setOptionsList(optionModels);
                    questionModel.setBookmarked(true);
                    questionModel.setStatus(NOT_VISITED);
                    questionModel.setSelectedOption(-1);

                    Log.i(TAG, "found - " + questionModel.getQuestion());

                    LIST_OF_BOOKMARKS.add(questionModel);

                    Log.i(TAG, "added - " + questionModel.getQuestion() + " - all - " + LIST_OF_BOOKMARKS.size());

                    if (number == LIST_OF_BOOKMARK_IDS.size() - 1) {
                        Log.i(TAG, "amount bookmarks - " + LIST_OF_BOOKMARKS.size());
                        listener.OnSuccess();
                    }

                })
                .addOnFailureListener(e -> {
                    Log.i(TAG, "can not find bookmark - " + e.getMessage());
                    listener.OnFailure();
                });
        }

    }

    public void saveBookmarks(CompleteListener listener) {

        WriteBatch batch = DATA_FIRESTORE.batch();

        DocumentReference userDocument = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
                .document(USER_MODEL.getUid());

        Log.i(TAG, "amount saved bookmarks - " + LIST_OF_BOOKMARKS.size());

        Map<String, Object> bookmarksData = new ArrayMap<>();

        for (int i=0; i < LIST_OF_BOOKMARKS.size(); i++) {
            bookmarksData.put("BOOKMARK" + i + "_ID", LIST_OF_BOOKMARKS.get(i).getId());

            Log.i(TAG, "bookMark - " + LIST_OF_BOOKMARKS.get(i).getId() + " - " + LIST_OF_BOOKMARKS.get(i).getQuestion());
        }

        DocumentReference bookmarkDocument = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
                .document(USER_MODEL.getUid())
                .collection(KEY_COLLECTION_PERSONAL_DATA)
                .document(KEY_BOOKMARKS);

        batch.set(bookmarkDocument, bookmarksData);

        userDocument.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> userData = new ArrayMap<>();

            userData.put(KEY_BOOKMARKS, LIST_OF_BOOKMARKS.size());

            batch.update(userDocument, userData);

            batch.commit()
                    .addOnSuccessListener(unused -> listener.OnSuccess())
                    .addOnFailureListener(e -> listener.OnFailure());
        }).addOnFailureListener(e -> listener.OnFailure());
    }


}
