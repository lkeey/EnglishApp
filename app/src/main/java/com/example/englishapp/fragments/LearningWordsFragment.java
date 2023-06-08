package com.example.englishapp.fragments;

import static com.example.englishapp.database.Constants.KEY_IS_WORDS;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.englishapp.R;
import com.example.englishapp.activities.ExamActivity;
import com.example.englishapp.adapters.LearningWordsAdapter;
import com.example.englishapp.database.DataBaseLearningWords;
import com.example.englishapp.database.DataBasePersonalData;
import com.example.englishapp.database.DataBaseQuestions;
import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.models.OptionModel;
import com.example.englishapp.models.QuestionModel;
import com.example.englishapp.models.WordModel;
import com.example.englishapp.repositories.WordsRepository;

import java.util.ArrayList;

public class LearningWordsFragment extends Fragment {

    private static final String TAG = "FragmentLearningWords";
    private ProgressBar progressBar;
    private TextView learningHelper;
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
        btnExam.setOnClickListener(v -> {
            try {

                WordsRepository wordsRepository = new WordsRepository();

                DataBaseQuestions.LIST_OF_QUESTIONS.clear();

                for (WordModel wordModel : DataBaseLearningWords.LIST_OF_LEARNING_WORDS) {

                    QuestionModel questionModel = new QuestionModel();

                    wordsRepository.translateString(wordModel.getTextEn(), DataBasePersonalData.USER_MODEL.getLanguageCode(), new CompleteListener() {
                        @Override
                        public void OnSuccess() {
                            questionModel.setQuestion(WordsRepository.translatedText);
                        }

                        @Override
                        public void OnFailure() {
                            questionModel.setQuestion(wordModel.getTextEn());
                        }
                    });

                    questionModel.setBookmarked(false);
                    questionModel.setSelectedOption(-1);
                    questionModel.setBmp(wordsRepository.stringToBitMap(getContext(), wordModel.getImage()));

                    ArrayList<OptionModel> optionModelList = new ArrayList<>();

                    for (int i=0; i < DataBaseLearningWords.LIST_OF_LEARNING_WORDS.size(); i++) {

                        WordModel word = DataBaseLearningWords.LIST_OF_LEARNING_WORDS.get(i);

                        OptionModel optionModel = new OptionModel();

                        optionModel.setOption(word.getTextEn());

                        if (word.equals(wordModel)) {
                            questionModel.setCorrectAnswer(i);
                            optionModel.setCorrect(true);
                        } else {
                            optionModel.setCorrect(false);
                        }

                        optionModelList.add(optionModel);
                    }

                    questionModel.setOptionsList(optionModelList);

                    DataBaseQuestions.LIST_OF_QUESTIONS.add(questionModel);

                }

                Intent intent = new Intent(requireActivity(), ExamActivity.class);

                intent.putExtra(KEY_IS_WORDS, true);

                startActivity(intent);

            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        });
    }

    private void init(View view) {

        progressBar = view.findViewById(R.id.progressBar);
        recyclerLearningWords = view.findViewById(R.id.recyclerLearningWords);
        btnExam = view.findViewById(R.id.btnExam);
        learningHelper = view.findViewById(R.id.textHelper);

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

                if (DataBaseLearningWords.LIST_OF_LEARNING_WORDS.size() == 0) {
                    recyclerLearningWords.setVisibility(View.GONE);
                    btnExam.setVisibility(View.GONE);
                    learningHelper.setVisibility(View.VISIBLE);
                }

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