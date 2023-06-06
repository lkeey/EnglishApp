package com.example.englishapp.fragments;

import static com.example.englishapp.repositories.CreateTestRepository.CODE_AMOUNT_OPTIONS;
import static com.example.englishapp.repositories.CreateTestRepository.CODE_LOT_CORRECT_OPTIONS;
import static com.example.englishapp.repositories.CreateTestRepository.CODE_NAME;
import static com.example.englishapp.repositories.CreateTestRepository.CODE_NO_CORRECT_OPTION;
import static com.example.englishapp.repositories.CreateTestRepository.CODE_NO_QUESTIONS;
import static com.example.englishapp.repositories.CreateTestRepository.CODE_OPTION;
import static com.example.englishapp.repositories.CreateTestRepository.CODE_QUESTION;
import static com.example.englishapp.repositories.CreateTestRepository.CODE_TIME;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import com.example.englishapp.R;
import com.example.englishapp.activities.MainActivity;
import com.example.englishapp.database.DataBaseTests;
import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.repositories.CreateTestRepository;

import java.util.ArrayList;
import java.util.List;

public class CreateTestFragment extends Fragment {
    private static final String TAG = "CreateTestFragment";
    private LinearLayout layoutList;
    private Button btnAdd, btnSubmit;
    private NumberPicker numberPicker;
    private EditText testName;
    private int timeDoing;
    private Dialog progressBar;
    private final List<String> stringListOption = new ArrayList<>();
    private DataBaseTests dataBaseTests;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_test, container, false);

        init(view);

        dataBaseTests = new DataBaseTests();

        setListeners();

        return view;
    }

    private void init(View view) {
        requireActivity().setTitle(R.string.nameCreateTest);

        layoutList = view.findViewById(R.id.layoutList);
        btnAdd = view.findViewById(R.id.btnAddQuestion);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        testName = view.findViewById(R.id.testName);
        numberPicker = view.findViewById(R.id.numberPicker);

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarSaving);

        stringListOption.add("Wrong");
        stringListOption.add("Correct");

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(60);

        timeDoing = 1;
    }

    private void setListeners() {
        btnSubmit.setOnClickListener(v -> {
            if (readData()) {
                // if data is valid

                progressBar.show();

                dataBaseTests.createTestData(CreateTestRepository.listOfQuestions, testName.getText().toString(), timeDoing, new CompleteListener() {
                    @Override
                    public void OnSuccess() {
                        Log.i(TAG, "Successfully created");

                        Toast.makeText(getActivity(), "Test successfully created", Toast.LENGTH_SHORT).show();

                        ((MainActivity) requireActivity()).setFragment(new CategoryFragment());

                        progressBar.dismiss();
                    }

                    @Override
                    public void OnFailure() {
                        Log.i(TAG, "Can not create test");

                        Toast.makeText(getActivity(), "Error occurred while saving test data", Toast.LENGTH_SHORT).show();

                        progressBar.dismiss();
                    }
                });

            }
        });

        btnAdd.setOnClickListener(view -> addView());

        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> timeDoing = newVal);
    }

    private boolean readData() {

        int status = new CreateTestRepository().readData(
                testName.getText().toString(),
                timeDoing,
                layoutList
        );

        if (status == CODE_NAME) {
            Toast.makeText(getActivity(), getString(R.string.name_must_be_not_empty), Toast.LENGTH_SHORT).show();

            return false;
        } else if (status == CODE_TIME) {
            Toast.makeText(getActivity(), getString(R.string.please_choose_time_doing), Toast.LENGTH_SHORT).show();

            return false;
        } else if (status == CODE_QUESTION) {
            Toast.makeText(getActivity(), getString(R.string.question_must_be_not_empty), Toast.LENGTH_SHORT).show();

            return false;
        } else if (status == CODE_OPTION) {
            Toast.makeText(getActivity(), getString(R.string.option_must_be_not_empty), Toast.LENGTH_SHORT).show();

            return false;
        } else if (status == CODE_NO_CORRECT_OPTION) {
            Toast.makeText(getActivity(), getString(R.string.must_be_only_1_correct_option), Toast.LENGTH_SHORT).show();

            return false;
        } else if (status == CODE_LOT_CORRECT_OPTIONS) {
            Toast.makeText(getActivity(), getString(R.string.there_are_must_be_1_correct_option), Toast.LENGTH_SHORT).show();

            return false;
        } else if (status == CODE_AMOUNT_OPTIONS) {
            Toast.makeText(getActivity(), getString(R.string.must_be_at_least_2_options), Toast.LENGTH_SHORT).show();

            return false;
        } else if (status == CODE_NO_QUESTIONS) {
            Toast.makeText(getActivity(), getString(R.string.add_at_least_one_question), Toast.LENGTH_SHORT).show();

            return false;
        } else {
            return true;
        }
    }

    private void addView() {
        View view = getLayoutInflater().inflate(R.layout.row_add, null, false);

        ImageView imageView = view.findViewById(R.id.imageRemove);

        Button btnAddOption = view.findViewById(R.id.btnAddOption);

        imageView.setOnClickListener(v -> removeView(view));

        btnAddOption.setOnClickListener(v -> {
            LinearLayout layoutListOptions = view.findViewById(R.id.layoutListOptions);
            addViewOption(layoutListOptions);
        });

        layoutList.addView(view);
    }

    private void removeView(View view) {

        layoutList.removeView(view);

    }

    private void addViewOption(LinearLayout layoutListOptions) {
        View viewOption = getLayoutInflater().inflate(R.layout.row_option, null, false);

        AppCompatSpinner spinner = viewOption.findViewById(R.id.spinnerOptions);
        ImageView imageView = viewOption.findViewById(R.id.imageRemoveOption);


        ArrayAdapter adapter = new ArrayAdapter(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, stringListOption);
        spinner.setAdapter(adapter);

        imageView.setOnClickListener(v -> removeViewOption(layoutListOptions, viewOption));

        layoutListOptions.addView(viewOption);
    }

    private void removeViewOption(LinearLayout layoutList, View view) {
        layoutList.removeView(view);
    }

}