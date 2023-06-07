package com.example.englishapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.englishapp.R;
import com.example.englishapp.adapters.LearningWordsAdapter;
import com.example.englishapp.database.DataBaseLearningWords;
import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.models.WordModel;

public class LearningWordsFragment extends Fragment {

    private ProgressBar progressBar;
    private RecyclerView recyclerLearningWords;
    private Button btnExam;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learning_words, container, false);

        init(view);

        setListeners();

        setSnapHelper();

        return view;
    }

    private void setListeners() {
        btnExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(WordModel wordModel: DataBaseLearningWords.LIST_OF_LEARNING_WORDS) {

                }
            }
        });
    }

    private void init(View view) {

        progressBar = view.findViewById(R.id.progressBar);
        recyclerLearningWords = view.findViewById(R.id.recyclerLearningWords);
        btnExam = view.findViewById(R.id.btnExam);

        progressBar.setVisibility(View.VISIBLE);

        new DataBaseLearningWords().loadLearningWords(getContext(), new CompleteListener() {
            @Override
            public void OnSuccess() {
                LearningWordsAdapter learningWordsAdapter = new LearningWordsAdapter(DataBaseLearningWords.LIST_OF_LEARNING_WORDS);
                recyclerLearningWords.setAdapter(learningWordsAdapter);

                LinearLayoutManager manager = new LinearLayoutManager(requireActivity());
                manager.setOrientation(RecyclerView.HORIZONTAL);
                recyclerLearningWords.setLayoutManager(manager);

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void OnFailure() {
                Toast.makeText(getActivity(), "Can not load words", Toast.LENGTH_SHORT).show();

                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void setSnapHelper() {
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerLearningWords);
    }

}