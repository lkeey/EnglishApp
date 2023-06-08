package com.example.englishapp.repositories;

import android.util.Log;

import com.example.englishapp.database.DataBaseBookmarks;
import com.example.englishapp.database.DataBaseExam;
import com.example.englishapp.database.DataBasePersonalData;
import com.example.englishapp.database.DataBaseQuestions;
import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.models.QuestionModel;

public class ScoreRepository {

    private static final String TAG = "RepositoryScore";

    public void updateData(boolean isWordExam, String cardId, int finalScore, CompleteListener listener) {
        if (!isWordExam) {
            // bookmarks
            Log.i(TAG, "was - " + DataBasePersonalData.USER_MODEL.getBookmarksCount() + " - " + DataBaseBookmarks.LIST_OF_BOOKMARKS.size());

            for (int i = 0; i < DataBaseQuestions.LIST_OF_QUESTIONS.size(); i++) {
                QuestionModel questionModel = DataBaseQuestions.LIST_OF_QUESTIONS.get(i);

                Log.i(TAG, "question - " + questionModel.isBookmarked() + " - " + questionModel.getQuestion() + " - " + DataBaseBookmarks.LIST_OF_BOOKMARK_IDS.contains(questionModel.getId()));

                if (questionModel.isBookmarked() && !DataBaseBookmarks.LIST_OF_BOOKMARK_IDS.contains(questionModel.getId())) {
                    DataBaseBookmarks.LIST_OF_BOOKMARK_IDS.add(questionModel.getId());

                    Log.i(TAG, "Added Bookmark - " + questionModel.getQuestion() + " - " + questionModel.getId());
                }

                if (!questionModel.isBookmarked() && DataBaseBookmarks.LIST_OF_BOOKMARK_IDS.contains(questionModel.getId())) {
                    DataBaseBookmarks.LIST_OF_BOOKMARK_IDS.remove(questionModel.getId());

                    Log.i(TAG, "Removed - " + questionModel.getQuestion());
                }
            }

            DataBasePersonalData.USER_MODEL.setBookmarksCount(DataBaseBookmarks.LIST_OF_BOOKMARK_IDS.size());

            Log.i(TAG, "become - " + DataBasePersonalData.USER_MODEL.getBookmarksCount());
        }

        // score
        new DataBaseExam().saveResult(isWordExam, cardId, finalScore, new CompleteListener() {
            @Override
            public void OnSuccess() {
                listener.OnSuccess();
            }

            @Override
            public void OnFailure() {
                listener.OnFailure();
            }
        });
    }

}
