package com.example.englishapp.domain.repositories;

import static com.example.englishapp.data.database.DataBaseExam.ANSWERED;
import static com.example.englishapp.data.database.DataBaseExam.REVIEW;
import static com.example.englishapp.data.database.DataBaseExam.UNANSWERED;

import android.util.Log;

import com.example.englishapp.data.database.DataBaseQuestions;
import com.example.englishapp.data.models.QuestionModel;

public class ExamRepository {

    private static final String TAG = "RepositoryExam";
    public static int CODE_UNBOOKMARKED = 0;
    public static int CODE_BOOKMARKED = 1;

    public int addToBookmark(int numberOfQuestion) {

        QuestionModel questionModel = DataBaseQuestions.LIST_OF_QUESTIONS.get(numberOfQuestion);

        if (questionModel.isBookmarked()) {

            Log.i(TAG, "Already bookmark");

            DataBaseQuestions.LIST_OF_QUESTIONS.get(numberOfQuestion).setBookmarked(false);

            if (questionModel.getSelectedOption() != -1) {
                Log.i(TAG, "New status - ANSWERED");

                DataBaseQuestions.LIST_OF_QUESTIONS.get(numberOfQuestion).setStatus(ANSWERED);

            } else {
                Log.i(TAG, "New status - UNANSWERED");

                DataBaseQuestions.LIST_OF_QUESTIONS.get(numberOfQuestion).setStatus(UNANSWERED);
            }

            return CODE_UNBOOKMARKED;

        } else {

            Log.i(TAG, "New bookmark");

            DataBaseQuestions.LIST_OF_QUESTIONS.get(numberOfQuestion).setBookmarked(true);

            DataBaseQuestions.LIST_OF_QUESTIONS.get(numberOfQuestion).setStatus(REVIEW);

            return CODE_BOOKMARKED;
        }
    }

}
