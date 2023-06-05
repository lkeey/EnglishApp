package com.example.englishapp.fragments;

import static android.app.Activity.RESULT_OK;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_USERS;
import static com.example.englishapp.database.DataBasePersonalData.USER_MODEL;
import static com.example.englishapp.database.DataBaseWords.LIST_OF_WORDS;

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
import com.example.englishapp.database.Constants;
import com.example.englishapp.database.DataBasePersonalData;
import com.example.englishapp.database.DataBaseWords;
import com.example.englishapp.interfaces.CompleteListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class SpeechFragment extends Fragment {
    private static final String TAG = "FragmentSpeech";
    private TextView textWord;
    private Button btnPronounce;
    private ProgressBar progressBar;
    private String chosenStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_speech, container, false);

        init(view);

        return view;
    }

    private void init(View view) {
        textWord = view.findViewById(R.id.textWord);
        btnPronounce = view.findViewById(R.id.btnPronounce);
        progressBar = view.findViewById(R.id.progressBar);

        loadWords();

        btnPronounce.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to convert into text");

            try {
                startActivityForResult(intent, 1);

                progressBar.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                Toast.makeText(getActivity(), "Try Later", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "error - " + e.getMessage());

                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadWords() {

        progressBar.setVisibility(View.VISIBLE);

        DataBaseWords dataBaseWords = new DataBaseWords();

        dataBaseWords.loadWords(null, new CompleteListener() {
            @Override
            public void OnSuccess() {
                Log.i(TAG, "successfully load words");

                if (LIST_OF_WORDS.size() > 1) {

                    chosenStr = LIST_OF_WORDS.get(new Random().nextInt(LIST_OF_WORDS.size() - 1)).getTextEn();

                    textWord.setText(chosenStr);

                } else if (LIST_OF_WORDS.size() == 1) {

                    textWord.setText(LIST_OF_WORDS.get(0).getTextEn());

                } else {

                    Toast.makeText(getActivity(), "Try Later", Toast.LENGTH_SHORT).show();
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void OnFailure() {

                Toast.makeText(getActivity(), "Try Later", Toast.LENGTH_SHORT).show();

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

//                btnPronounce.setText(result.get(0));

                Log.i(TAG, "word - " + textWord.getText().toString() + " - " + result.get(0));

                if (result.get(0).equals(textWord.getText().toString())) {

                    Toast.makeText(getActivity(), "Correct!", Toast.LENGTH_SHORT).show();

                    addScore();

                } else {

                    Toast.makeText(getActivity(), "Incorrect!", Toast.LENGTH_SHORT).show();

                }
            }
        }

        progressBar.setVisibility(View.GONE);
    }

    private void addScore() {
        USER_MODEL.setScore(USER_MODEL.getScore() + 25);

        DocumentReference reference = DataBasePersonalData.DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
                .document(DataBasePersonalData.USER_MODEL.getUid());

        reference.update(Constants.KEY_SCORE, USER_MODEL.getScore())
            .addOnSuccessListener(unused -> {

                Log.i(TAG, "updated score - " + USER_MODEL.getScore());

                Toast.makeText(getActivity(), "Score Updated!", Toast.LENGTH_SHORT).show();

                loadWords();
            })
            .addOnFailureListener(e -> Log.i(TAG, "error - " + e.getMessage()));
    }
}