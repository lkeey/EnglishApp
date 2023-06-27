package com.example.englishapp.presentation.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.englishapp.R;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.data.repositories.SpeechRepository;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechFragment extends Fragment {
    private static final String TAG = "FragmentSpeech";
    private TextView textWord;
    private ProgressBar progressBar;
    private SpeechRepository speechRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_speech, container, false);

        init(view);

        return view;
    }

    private void init(View view) {
        textWord = view.findViewById(R.id.textWord);
        Button btnPronounce = view.findViewById(R.id.btnPronounce);
        progressBar = view.findViewById(R.id.progressBar);

        speechRepository = new SpeechRepository();

        loadWords();

        btnPronounce.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say '" + SpeechRepository.CHOSEN_WORD + "' and we will check your pronunciation");

            try {
                startActivityForResult(intent, 1);

                progressBar.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "error - " + e.getMessage());

                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadWords() {

        progressBar.setVisibility(View.VISIBLE);

        speechRepository.loadWords(new CompleteListener() {
            @Override
            public void OnSuccess() {
                textWord.setText(SpeechRepository.CHOSEN_WORD);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void OnFailure() {
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong_try_later), Toast.LENGTH_SHORT).show();

                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                Log.i(TAG, "word - " + textWord.getText().toString() + " - " + result.get(0));

                if (result.get(0).equalsIgnoreCase(textWord.getText().toString())) {

                    Toast.makeText(getActivity(), getString(R.string.correct), Toast.LENGTH_SHORT).show();

                    new SpeechRepository().addScore(new CompleteListener() {
                        @Override
                        public void OnSuccess() {
                            Toast.makeText(getActivity(), getString(R.string.score_updated), Toast.LENGTH_LONG).show();

                            loadWords();
                        }

                        @Override
                        public void OnFailure() {
                            Toast.makeText(getActivity(), getString(R.string.can_non_update_score_check_your_internet_connection), Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    Toast.makeText(getActivity(), getString(R.string.incorrect), Toast.LENGTH_SHORT).show();
                }
            }
        }

        progressBar.setVisibility(View.GONE);
    }
}
