package com.example.englishapp.data.repositories;

import android.util.Log;

import com.example.englishapp.data.database.DataBaseBookmarks;
import com.example.englishapp.data.database.DataBasePersonalData;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.data.models.QuestionModel;

import java.util.Iterator;

public class BookmarkRepository {

    private static final String TAG = "RepositoryBookmark";
    public static int CODE_UNBOOKMARKED = 0;
    public static int CODE_BOOKMARKED = 1;

    public void saveBookmarks(CompleteListener listener) {

        Iterator<QuestionModel> questionModelIterator = DataBaseBookmarks.LIST_OF_BOOKMARKS.iterator();

        while(questionModelIterator.hasNext()) {

            QuestionModel nextQuestion = questionModelIterator.next();
            if (!nextQuestion.isBookmarked()) {
                questionModelIterator.remove();

                Log.i(TAG, "removed - " + nextQuestion.getQuestion());
            }
        }

        DataBasePersonalData.USER_MODEL.setBookmarksCount(DataBaseBookmarks.LIST_OF_BOOKMARKS.size());

        new DataBaseBookmarks().saveBookmarks(new CompleteListener() {
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

    public int addToBookmark(int numberOfQuestion) {

        QuestionModel questionModel = DataBaseBookmarks.LIST_OF_BOOKMARKS.get(numberOfQuestion);

        if (questionModel.isBookmarked()) {

            Log.i(TAG, "Already bookmark");

            DataBaseBookmarks.LIST_OF_BOOKMARKS.get(numberOfQuestion).setBookmarked(false);

            return CODE_UNBOOKMARKED;

        } else {

            Log.i(TAG, "New bookmark");

            DataBaseBookmarks.LIST_OF_BOOKMARKS.get(numberOfQuestion).setBookmarked(true);

            return CODE_BOOKMARKED;
        }
    }
}
