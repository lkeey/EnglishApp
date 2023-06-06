package com.example.englishapp.repositories;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatSpinner;

import com.example.englishapp.R;
import com.example.englishapp.models.OptionModel;
import com.example.englishapp.models.QuestionModel;

import java.util.ArrayList;

public class CreateTestRepository {

    private static final String TAG = "TestRepository";
    public static ArrayList<QuestionModel> listOfQuestions = new ArrayList<>();
    public static int CODE_SUCCESS = 0;
    public static int CODE_NAME = 1;
    public static int CODE_TIME = 2;
    public static int CODE_QUESTION = 3;
    public static int CODE_OPTION = 4;
    public static int CODE_NO_CORRECT_OPTION = 5;
    public static int CODE_LOT_CORRECT_OPTIONS = 5;
    public static int CODE_AMOUNT_OPTIONS = 6;
    public static int CODE_NO_QUESTIONS = 7;

    public int readData(String testName, int timeDoing, LinearLayout layoutList) {

        boolean resultOption;

        listOfQuestions.clear();

        if (testName.isEmpty()) {

            return CODE_NAME;
        }

        if (timeDoing < 1) {

            return CODE_TIME;
        }

        for(int i=0; i < layoutList.getChildCount(); i++) {
            resultOption = false;

            View viewChild = layoutList.getChildAt(i);

            EditText editText = viewChild.findViewById(R.id.editText);
            LinearLayout listOption = viewChild.findViewById(R.id.layoutListOptions);

            QuestionModel questionModel = new QuestionModel();

            if (!editText.getText().toString().isEmpty()) {
                questionModel.setQuestion(editText.getText().toString());
            } else {
                return CODE_QUESTION;
            }

            ArrayList<OptionModel> listOfOptions = new ArrayList<>();

            for(int n=0; n < listOption.getChildCount(); n++) {

                View viewOption = listOption.getChildAt(n);

                EditText editTextOption = viewOption.findViewById(R.id.editTextOption);
                AppCompatSpinner spinnerOption = viewOption.findViewById(R.id.spinnerOptions);

                OptionModel optionModel = new OptionModel();

                if (!editTextOption.getText().toString().isEmpty()) {
                    optionModel.setOption(editTextOption.getText().toString());
                } else {

                    return CODE_OPTION;
                }

                if (spinnerOption.getSelectedItemPosition() == 1) {

                    if (resultOption) {
                        // if there are more than one correct answer

                        return CODE_NO_CORRECT_OPTION;
                    }

                    resultOption = true;

                    optionModel.setCorrect(true);

                } else {
                    optionModel.setCorrect(false);
                }


                Log.i(TAG, "Option - " + optionModel.getOption() + " - " + optionModel.isCorrect());

                listOfOptions.add(optionModel);
            }

            if (!resultOption) {

                return CODE_LOT_CORRECT_OPTIONS;
            }

            if (listOfOptions.size() < 2) {

                return CODE_AMOUNT_OPTIONS;
            }

            questionModel.setOptionsList(listOfOptions);


            listOfQuestions.add(questionModel);
        }

        if(listOfQuestions.size() == 0) {

            return CODE_NO_QUESTIONS;

        }

        return CODE_SUCCESS;
    }


}
