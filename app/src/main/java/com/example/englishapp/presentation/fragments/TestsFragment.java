package com.example.englishapp.presentation.fragments;

import static com.example.englishapp.data.database.Constants.KEY_CHOSEN_TEST;
import static com.example.englishapp.data.database.Constants.SHOW_FRAGMENT_DIALOG;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.data.database.DataBaseBookmarks;
import com.example.englishapp.data.database.DataBaseCategories;
import com.example.englishapp.data.database.DataBaseQuestions;
import com.example.englishapp.data.database.DataBaseScores;
import com.example.englishapp.data.database.DataBaseTests;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.domain.interfaces.RefreshListener;
import com.example.englishapp.domain.interfaces.TestClickedListener;
import com.example.englishapp.data.models.TestModel;
import com.example.englishapp.presentation.activities.MainActivity;
import com.example.englishapp.presentation.adapters.TestAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TestsFragment extends Fragment implements TestClickedListener, RefreshListener {

    private static final String TAG = "TestsFragment";
    private RecyclerView testRecycler;
    private TestAdapter testAdapter;
    private FloatingActionButton fab;
    private EditText inputSearch;
    private Dialog progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tests, container, false);

        init(view);

        setListeners();

        return view;
    }

    private void init(View view) {
        DataBaseTests dataBaseTests = new DataBaseTests();

        requireActivity().setTitle(R.string.nameTests);

        testRecycler = view.findViewById(R.id.testRecyclerView);
        fab = view.findViewById(R.id.fab);
        inputSearch = view.findViewById(R.id.inputSearch);

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView dialogText = progressBar.findViewById(R.id.dialogText);

        dialogText.setText(R.string.progressBarOpening);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        testRecycler.setLayoutManager(manager);

        progressBar.show();

        dataBaseTests.loadTestsData(new CompleteListener() {
            @Override
            public void OnSuccess() {
                new DataBaseScores().loadMyScores(new CompleteListener() {
                    @Override
                    public void OnSuccess() {
                        testAdapter = new TestAdapter(DataBaseTests.LIST_OF_TESTS, TestsFragment.this);
                        testRecycler.setAdapter(testAdapter);

                        Log.i(TAG, "Successfully loaded");
                        progressBar.dismiss();
                    }

                    @Override
                    public void OnFailure() {
                        Log.i(TAG, "Can not load scores");
                        progressBar.dismiss();
                    }
                });
            }
            @Override
            public void OnFailure() {
                Log.i(TAG, "Can not load tests");
                progressBar.dismiss();
            }
        });
    }

    private void setListeners() {
        fab.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            CreateTestFragment fragment = new CreateTestFragment();
            fragment.setArguments(bundle);

            ((MainActivity) requireActivity()).setFragment(fragment, false);
        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                testAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable key) {
                if(DataBaseCategories.LIST_OF_CATEGORIES.size() != 0) {
                    testAdapter.searchTests(key.toString());
                }
            }
        });
    }

    @Override
    public void onTestClicked(TestModel test) {

        progressBar.show();

        DataBaseTests.CHOSEN_TEST_ID = test.getId();

        TestInfoDialogFragment fragment = new TestInfoDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_CHOSEN_TEST, test);
        fragment.setArguments(bundle);
        new DataBaseBookmarks().loadBookmarkIds(new CompleteListener() {
            @Override
            public void OnSuccess() {
                new DataBaseQuestions().loadQuestions(new CompleteListener() {
                    @Override
                    public void OnSuccess() {

                        Log.i(TAG, "Questions loaded");

                        fragment.show(getParentFragmentManager(), SHOW_FRAGMENT_DIALOG);

                        progressBar.dismiss();

                    }

                    @Override
                    public void OnFailure() {
                        Log.i(TAG, "Can not load questions");

                        Toast.makeText(getActivity(), "Can not load test... Try later", Toast.LENGTH_SHORT).show();

                        progressBar.dismiss();

                    }
                });

            }

            @Override
            public void OnFailure() {
                Toast.makeText(getActivity(), "Try Later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        progressBar.show();

        new DataBaseTests().loadTestsData(new CompleteListener() {
            @Override
            public void OnSuccess() {
                testAdapter = new TestAdapter(DataBaseTests.LIST_OF_TESTS, TestsFragment.this);
                testRecycler.setAdapter(testAdapter);

                Log.i(TAG, "Successfully loaded");
                progressBar.dismiss();

            }

            @Override
            public void OnFailure() {
                progressBar.dismiss();
                Toast.makeText(getActivity(), "Database isn't available", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
