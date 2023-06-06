package com.example.englishapp.database;

import static com.example.englishapp.database.Constants.KEY_ANSWER;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_QUESTIONS;
import static com.example.englishapp.database.Constants.KEY_NUMBER_OF_OPTIONS;
import static com.example.englishapp.database.Constants.KEY_OPTION;
import static com.example.englishapp.database.Constants.KEY_QUESTION_ID;
import static com.example.englishapp.database.Constants.KEY_TEST_ID;
import static com.example.englishapp.database.Constants.KEY_TEST_QUESTION;
import static com.example.englishapp.database.Constants.NOT_VISITED;
import static com.example.englishapp.database.DataBaseBookmarks.LIST_OF_BOOKMARK_IDS;
import static com.example.englishapp.database.DataBaseCategories.CHOSEN_CATEGORY_ID;
import static com.example.englishapp.database.DataBasePersonalData.DATA_FIRESTORE;
import static com.example.englishapp.database.DataBaseTests.CHOSEN_TEST_ID;

import android.util.ArrayMap;
import android.util.Log;

import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.models.OptionModel;
import com.example.englishapp.models.QuestionModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataBaseQuestions {

    private static final String TAG = "QuestionsDao";
    public static List<QuestionModel> LIST_OF_QUESTIONS = new ArrayList<>();

    public void createQuestionData(ArrayList<QuestionModel> listOfQuestions, String testID, CompleteListener listener) {

        WriteBatch batch = DATA_FIRESTORE.batch();

        for(int i=0; i < listOfQuestions.size(); i++) {

            Map<String, Object> questionData = new ArrayMap<>();

            QuestionModel questionModel = listOfQuestions.get(i);

            Log.i(TAG, "questionModel - " + questionModel.getQuestion());

            questionData.put(KEY_QUESTION_ID, CHOSEN_CATEGORY_ID + "_" + testID + "_" + i);
            questionData.put(KEY_TEST_QUESTION, questionModel.getQuestion());
            questionData.put(KEY_NUMBER_OF_OPTIONS, questionModel.getOptionsList().size());
            questionData.put(KEY_TEST_ID, testID);

            for (int n=0; n < questionModel.getOptionsList().size(); n++) {
                OptionModel optionModel = questionModel.getOptionsList().get(n);

                Log.i(TAG, "optionModel - " + optionModel.getOption());

                if (optionModel.isCorrect()) {
                    questionData.put(KEY_ANSWER, n);
                }

                questionData.put(KEY_OPTION + "_" + n, optionModel.getOption());

            }

            DocumentReference questionDocument = DATA_FIRESTORE
                    .collection(KEY_COLLECTION_QUESTIONS)
                    .document(CHOSEN_CATEGORY_ID + "_" + testID +"_" + i);

            batch.set(questionDocument, questionData, SetOptions.merge());

        }

        batch.commit().addOnSuccessListener(unused -> {
            Log.i(TAG, "Questions successfully added");
            listener.OnSuccess();
        }).addOnFailureListener(e -> {
            Log.i(TAG, "Fail to save test - " + e.getMessage());
            listener.OnFailure();
        });

    }

    public void loadQuestions(CompleteListener listener) {
        LIST_OF_QUESTIONS.clear();

        DATA_FIRESTORE.collection(KEY_COLLECTION_QUESTIONS)
            .whereEqualTo(KEY_TEST_ID, CHOSEN_TEST_ID)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {

                Log.i(TAG, "Begin loading questions");

                for (DocumentSnapshot document: queryDocumentSnapshots) {

                    Log.i(TAG, document.getString(KEY_TEST_QUESTION));

                    // load options
                    ArrayList<OptionModel> optionModels = new ArrayList<>();

                    for(int i = 0; i < (Objects.requireNonNull(document.getLong(KEY_NUMBER_OF_OPTIONS)).intValue()); i++) {
                        OptionModel optionModel = new OptionModel();

                        optionModel.setCorrect(i == Objects.requireNonNull(document.getLong(KEY_ANSWER)).intValue());

                        optionModel.setOption(document.getString(KEY_OPTION + "_" + i));

                        optionModels.add(optionModel);

                    }

                    // load question
                    QuestionModel questionModel = new QuestionModel();

                    questionModel.setQuestion(document.getString(KEY_TEST_QUESTION));
                    questionModel.setId(document.getString(KEY_QUESTION_ID));
                    questionModel.setCorrectAnswer(Objects.requireNonNull(document.getLong(KEY_ANSWER)).intValue());
                    questionModel.setOptionsList(optionModels);
                    questionModel.setStatus(NOT_VISITED);
                    questionModel.setSelectedOption(-1);

                    // if bookmarked
                    if (LIST_OF_BOOKMARK_IDS.contains(questionModel.getId())) {
                        questionModel.setBookmarked(true);

                        Log.i(TAG, "bookmarked - " + questionModel.getQuestion());
                    }

                    LIST_OF_QUESTIONS.add(questionModel);

                }

                Log.i(TAG, "Questions successfully loaded");

                listener.OnSuccess();

            })
            .addOnFailureListener(e -> {
                Log.i(TAG, "Error was occurred while loading questions - " + e.getMessage());
                listener.OnFailure();
            });
    }


}
