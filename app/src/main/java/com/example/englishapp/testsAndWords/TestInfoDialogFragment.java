package com.example.englishapp.testsAndWords;

import static com.example.englishapp.messaging.Constants.KEY_CHOSEN_TEST;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.englishapp.R;
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

        btnDoTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: send intent to start test fragment
            }
        });

    }

    @Override
    public int getTheme() {
        // to set border radius

        return R.style.AppBottomSheetDialogTheme;
    }
}
