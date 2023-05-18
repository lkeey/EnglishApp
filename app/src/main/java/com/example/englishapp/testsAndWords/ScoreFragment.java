package com.example.englishapp.testsAndWords;

import static com.example.englishapp.messaging.Constants.KEY_CHOSEN_TEST;
import static com.example.englishapp.messaging.Constants.SHOW_FRAGMENT_DIALOG;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.englishapp.MVP.DataBase;
import com.example.englishapp.MVP.MainActivity;
import com.example.englishapp.R;
import com.example.englishapp.messaging.Constants;

import java.util.concurrent.TimeUnit;

public class ScoreFragment extends Fragment {

    private static final String TAG = "FragmentScore";
    private TextView totalScore, timeTaken, totalQuestions,
            amountCorrect, amountWrong, amountUnAttempted,
            dialogText;
    private Button btnCheckLeader, btnReAttempt, btnViewAnswers;
    private long timeLeft;
    private Dialog progressBar;
    private int finalScore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score, container, false);
        
        init(view);

        receiveData();

        setListeners();
        
        setData();
        
        return view;
    }

    private void receiveData() {
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            timeLeft = bundle.getLong(Constants.KEY_TEST_TIME, -1);
        }
    }

    private void setData() {
        int correctQuestions = 0;
        int wrongQuestions = 0;
        int unAttemptedQuestions = 0;

        int sizeOfQuestions = DataBase.LIST_OF_QUESTIONS.size();

        for (int i=0; i < sizeOfQuestions; i++) {

            QuestionModel questionModel = DataBase.LIST_OF_QUESTIONS.get(i);

            if (questionModel.getSelectedOption() == -1) {
                unAttemptedQuestions++;
            } else if (questionModel.getSelectedOption() == questionModel.getCorrectAnswer()) {
                correctQuestions++;
            } else {
                wrongQuestions++;
            }

        }

        // set amount questions
        amountCorrect.setText(String.valueOf(correctQuestions));
        amountWrong.setText(String.valueOf(wrongQuestions));
        amountUnAttempted.setText(String.valueOf(unAttemptedQuestions));

        totalQuestions.setText("" + sizeOfQuestions);

        // set score
        finalScore = correctQuestions * 100 / sizeOfQuestions;
        totalScore.setText(String.valueOf(finalScore));

        // set time
        String time = String.format(
                "%02d : %02d minutes",
                TimeUnit.MILLISECONDS.toMinutes(timeLeft),
                TimeUnit.MILLISECONDS.toSeconds(timeLeft) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeLeft))
        );

        timeTaken.setText(time);
    }

    private void setListeners() {
        btnCheckLeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Check Leader", Toast.LENGTH_SHORT).show();
            }
        });

        btnReAttempt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reAttempt();
            }
        });

        btnViewAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "View answers", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void init(View view) {
        totalScore = view.findViewById(R.id.totalScore);
        timeTaken = view.findViewById(R.id.timeTaken);
        totalQuestions = view.findViewById(R.id.totalQuestions);
        amountCorrect = view.findViewById(R.id.amountCorrect);
        amountWrong = view.findViewById(R.id.amountWrong);
        amountUnAttempted = view.findViewById(R.id.amountUnAttempted);
        btnCheckLeader = view.findViewById(R.id.btnCheckLeader);
        btnReAttempt = view.findViewById(R.id.btnReAttempt);
        btnViewAnswers = view.findViewById(R.id.btnViewAnswers);

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarOpening);

        ((MainActivity) getActivity()).setTitle(R.string.nameResult);
    }

    private void reAttempt() {
        try {
            if (DataBase.CHOSEN_TEST_ID != null) {
                TestInfoDialogFragment fragment = new TestInfoDialogFragment();

                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_CHOSEN_TEST, DataBase.findTestById(DataBase.CHOSEN_TEST_ID));
                fragment.setArguments(bundle);

                fragment.show(getParentFragmentManager(), SHOW_FRAGMENT_DIALOG);
            }
        } catch (Exception e) {
            Log.i(TAG, "error to open TestInfoDialogFragment - " + e.getMessage());

            Toast.makeText(getActivity(), "Can not re-attempt the test... Please, try later", Toast.LENGTH_SHORT).show();
        }
    }
}