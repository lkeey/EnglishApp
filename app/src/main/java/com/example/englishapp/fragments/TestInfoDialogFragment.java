package com.example.englishapp.fragments;

import static com.example.englishapp.database.Constants.KEY_CHOSEN_TEST;
import static com.example.englishapp.database.DataBaseExam.NOT_VISITED;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.englishapp.R;
import com.example.englishapp.presentation.activities.ExamActivity;
import com.example.englishapp.presentation.activities.MainActivity;
import com.example.englishapp.database.DataBaseQuestions;
import com.example.englishapp.models.TestModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TestInfoDialogFragment extends BottomSheetDialogFragment {
    private static final String TAG = "TestInfoDialogFragment";
    private TextView textClose, nameTest, amountQuestions, bestScore, amountTime;
    private Button btnDoTest;
    private TestModel receivedTest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_info_dialog, container, false);

        init(view);

        receiveData();

        return view;
    }

    private void receiveData() {
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            receivedTest = (TestModel) bundle.getSerializable(KEY_CHOSEN_TEST);

            nameTest.setText(receivedTest.getName());
            amountQuestions.setText("" + receivedTest.getAmountOfQuestion());
            bestScore.setText("" + receivedTest.getTopScore());
            amountTime.setText("" + receivedTest.getTime());

        }
    }

    private void init(View view) {

        textClose = view.findViewById(R.id.textClose);
        nameTest = view.findViewById(R.id.nameTest);
        amountQuestions = view.findViewById(R.id.amountQuestions);
        bestScore = view.findViewById(R.id.bestScore);
        amountTime = view.findViewById(R.id.amountTime);
        btnDoTest = view.findViewById(R.id.btnDoTest);

        textClose.setOnClickListener(v -> TestInfoDialogFragment.this.dismiss());

        btnDoTest.setOnClickListener(v -> {

            if (DataBaseQuestions.LIST_OF_QUESTIONS.size() >= 0) {
                Intent intent = new Intent((MainActivity) getActivity(), ExamActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(getActivity(), "Can not load questions... Try later", Toast.LENGTH_SHORT).show();
            }

            TestInfoDialogFragment.this.dismiss();

        });

        // set all answers with basic data
        for (int i = 0; i < DataBaseQuestions.LIST_OF_QUESTIONS.size(); i++) {
            DataBaseQuestions.LIST_OF_QUESTIONS.get(i).setSelectedOption(-1);
            DataBaseQuestions.LIST_OF_QUESTIONS.get(i).setStatus(NOT_VISITED);
        }

    }

    @Override
    public int getTheme() {
        // to set border radius

        return R.style.AppBottomSheetDialogTheme;
    }
}
