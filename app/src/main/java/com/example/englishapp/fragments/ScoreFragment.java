package com.example.englishapp.fragments;

import static com.example.englishapp.database.Constants.KEY_CHOSEN_TEST;
import static com.example.englishapp.database.Constants.SHOW_FRAGMENT_DIALOG;

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

import com.example.englishapp.database.DataBasePersonalData;
import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.database.DataBase;
import com.example.englishapp.activities.MainActivity;
import com.example.englishapp.R;
import com.example.englishapp.database.Constants;
import com.example.englishapp.models.QuestionModel;

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

        updateBookmarksAndScore();
        
        return view;
    }

    private void updateBookmarksAndScore() {

        // bookmarks
        Log.i(TAG, "was - " + DataBasePersonalData.USER_MODEL.getBookmarksCount() + " - " + DataBase.LIST_OF_BOOKMARKS.size());

        for (int i=0; i < DataBase.LIST_OF_QUESTIONS.size(); i++) {
            QuestionModel questionModel = DataBase.LIST_OF_QUESTIONS.get(i);

            Log.i(TAG, "question - " + questionModel.isBookmarked() + " - " + questionModel.getQuestion() + " - " + DataBase.LIST_OF_BOOKMARK_IDS.contains(questionModel.getId()));

            if (questionModel.isBookmarked() && !DataBase.LIST_OF_BOOKMARK_IDS.contains(questionModel.getId())) {
                DataBase.LIST_OF_BOOKMARK_IDS.add(questionModel.getId());

                Log.i(TAG, "Added Bookmark - " + questionModel.getQuestion() + " - " + questionModel.getId());
            }

            if (!questionModel.isBookmarked() && DataBase.LIST_OF_BOOKMARK_IDS.contains(questionModel.getId())) {
                DataBase.LIST_OF_BOOKMARK_IDS.remove(questionModel.getId());

                Log.i(TAG, "Removed - " + questionModel.getQuestion());
            }
        }

        DataBasePersonalData.USER_MODEL.setBookmarksCount(DataBase.LIST_OF_BOOKMARK_IDS.size());

        Log.i(TAG, "become - " + DataBasePersonalData.USER_MODEL.getBookmarksCount());

        // score
        DataBase.saveResult(finalScore, new CompleteListener() {
            @Override
            public void OnSuccess() {
                progressBar.dismiss();
            }

            @Override
            public void OnFailure() {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                progressBar.dismiss();
            }
        });
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
        btnCheckLeader.setOnClickListener(v -> ((MainActivity) getActivity()).setFragment(new LeaderBordFragment()));

        btnReAttempt.setOnClickListener(v -> reAttempt());

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
            DataBase.loadMyScores(new CompleteListener() {
                @Override
                public void OnSuccess() {
                    if (DataBase.CHOSEN_TEST_ID != null) {
                        TestInfoDialogFragment fragment = new TestInfoDialogFragment();

                        Bundle bundle = new Bundle();
                        bundle.putSerializable(KEY_CHOSEN_TEST, DataBase.findTestById(DataBase.CHOSEN_TEST_ID));
                        fragment.setArguments(bundle);

                        fragment.show(getParentFragmentManager(), SHOW_FRAGMENT_DIALOG);
                    }
                }

                @Override
                public void OnFailure() {
                    Log.i(TAG, "can not load scores");
                }
            });

        } catch (Exception e) {
            Log.i(TAG, "error to open TestInfoDialogFragment - " + e.getMessage());

            Toast.makeText(getActivity(), "Can not re-attempt the test... Please, try later", Toast.LENGTH_SHORT).show();
        }
    }
}