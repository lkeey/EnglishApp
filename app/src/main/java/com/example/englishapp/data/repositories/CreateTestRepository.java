package com.example.englishapp.data.repositories;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatSpinner;

import com.example.englishapp.R;
import com.example.englishapp.data.models.OptionModel;
import com.example.englishapp.data.models.QuestionModel;

import java.util.ArrayList;

public class CreateTestRepository {

    private static final String TAG = "TestRepository";
    private boolean resultOption;
    private final ArrayList<OptionModel> listOfOptions = new ArrayList<>();
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
    public static int CODE_NAME_SIZE = 8;

    public int readData(String testName, int timeDoing, LinearLayout layoutList) {

        listOfQuestions.clear();

        if (testName.isEmpty()) {
            return CODE_NAME;
        }

        if (timeDoing < 1) {
            return CODE_TIME;
        }

        if (testName.length() > 10) {
            return CODE_NAME_SIZE;
        }

        int status = checkLayoutQuestions(layoutList);

        if (status == CODE_SUCCESS) {

            if (listOfQuestions.size() == 0) {

                return CODE_NO_QUESTIONS;
            }

        } else {
            return status;
        }

        return CODE_SUCCESS;
    }

    private int checkLayoutQuestions(LinearLayout layoutList) {
        for(int i=0; i < layoutList.getChildCount(); i++) {

            View viewChild = layoutList.getChildAt(i);

            EditText editText = viewChild.findViewById(R.id.editText);
            LinearLayout listOption = viewChild.findViewById(R.id.layoutListOptions);

            QuestionModel questionModel = new QuestionModel();

            if (!editText.getText().toString().isEmpty()) {
                questionModel.setQuestion(editText.getText().toString());
            } else {
                return CODE_QUESTION;
            }

            listOfOptions.clear();

            int status = checkLayoutOptions(listOption);

            if (status == CODE_SUCCESS) {

                if (!resultOption) {

                    return CODE_LOT_CORRECT_OPTIONS;
                }

                if (listOfOptions.size() < 2) {

                    return CODE_AMOUNT_OPTIONS;
                }

                questionModel.setOptionsList(listOfOptions);


                listOfQuestions.add(questionModel);
            } else {
                return status;
            }
        }

        return CODE_SUCCESS;
    }

    private int checkLayoutOptions(LinearLayout listOption) {
        resultOption = false;

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

        return CODE_SUCCESS;
    }

}
