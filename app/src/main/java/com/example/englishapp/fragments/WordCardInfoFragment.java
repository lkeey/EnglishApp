package com.example.englishapp.fragments;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.example.englishapp.database.Constants.KEY_ALREADY_LEARNING;
import static com.example.englishapp.database.Constants.KEY_CHOSEN_CARD;
import static com.example.englishapp.database.Constants.KEY_LANGUAGE_CODE;
import static com.example.englishapp.database.Constants.KEY_SHOW_NOTIFICATION_WORD;
import static com.example.englishapp.database.Constants.MY_SHARED_PREFERENCES;
import static com.example.englishapp.database.Constants.WORD_COUNTER;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.englishapp.R;
import com.example.englishapp.activities.MainActivity;
import com.example.englishapp.database.DataBaseLearningWords;
import com.example.englishapp.database.DataBasePersonalData;
import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.models.CardModel;
import com.example.englishapp.receivers.AlarmReceiver;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class WordCardInfoFragment extends BottomSheetDialogFragment {

    private static final String TAG = "FragmentWordCardInfo";
    private TextView nameCard, level, amountWords, description;
    private Button btnLearn;
    private CardModel receivedCard;
    private Dialog progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_card_info, container, false);
        
        init(view);

        receiveData();

        setListeners();
        
        return view;
    }

    private void setListeners() {

        btnLearn.setOnClickListener(v -> {

            progressBar.show();

            loadModel();

        });

    }

    private void loadModel() {
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(DataBasePersonalData.USER_MODEL.getLanguageCode())
                        .build();

        final Translator translator =
                Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions.Builder()
                .build();

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(unused -> {

            Log.i(TAG, "loaded");

            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE);

            boolean isLearning = sharedPreferences.getBoolean(KEY_ALREADY_LEARNING, false);

            Log.i(TAG, "isLearning - " + isLearning);

            if (!isLearning) {

                learnWords();

            } else {
                Toast.makeText(getActivity(), "You are learning other words", Toast.LENGTH_SHORT).show();

                progressBar.dismiss();
            }

        }).addOnFailureListener(e -> {
            progressBar.dismiss();

            Log.i(TAG, "can not load");
        });
    }

    private void receiveData() {
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            receivedCard = (CardModel) bundle.getSerializable(KEY_CHOSEN_CARD);

            nameCard.setText(receivedCard.getName());
            amountWords.setText(String.valueOf(receivedCard.getAmountOfWords()));
            level.setText(receivedCard.getLevel());
            description.setText(receivedCard.getDescription());

        }
    }

    private void init(View view) {
        TextView textClose = view.findViewById(R.id.textClose);
        nameCard = view.findViewById(R.id.nameCard);
        level = view.findViewById(R.id.level);
        amountWords = view.findViewById(R.id.amountWords);
        btnLearn = view.findViewById(R.id.btnLearn);
        description = view.findViewById(R.id.description);

        textClose.setOnClickListener(v -> WordCardInfoFragment.this.dismiss());

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarOpening);

    }

    private void learnWords() {

        Log.i(TAG, "learnWords - " + receivedCard.getAmountOfWords() + " - " + receivedCard.getId());

        DataBaseLearningWords dataBaseLearningWords = new DataBaseLearningWords();
        dataBaseLearningWords.uploadLearningWords(getContext(), receivedCard.getId(), new CompleteListener() {
            @Override
            public void OnSuccess() {
                try {

                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();

                    try {
                        Log.i(TAG, "amount loaded words - " + DataBaseLearningWords.LIST_OF_LEARNING_WORDS.size());

                        Log.i(TAG, "Successfully set");

                        myEdit.putInt(WORD_COUNTER, 0);
                        myEdit.putString(KEY_LANGUAGE_CODE, DataBasePersonalData.USER_MODEL.getLanguageCode());
                        myEdit.putBoolean(KEY_ALREADY_LEARNING, true);
                        myEdit.apply();

                        // create periodic task
                        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(ALARM_SERVICE);

                        Intent intent = new Intent(((MainActivity) getActivity()), AlarmReceiver.class);
                        intent.putExtra(KEY_SHOW_NOTIFICATION_WORD, true);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(((MainActivity) getActivity()), 1, intent, PendingIntent.FLAG_MUTABLE);

                        // cancel previous
                        alarmManager.cancel(pendingIntent);

                        // every 5 minute
                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 5 * 60 * 1000, 2 * 60 * 1000, pendingIntent);

                        Toast.makeText(getActivity(), "You will get notifications with chosen words", Toast.LENGTH_SHORT).show();

                        progressBar.dismiss();

                        WordCardInfoFragment.this.dismiss();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();

                        Log.i(TAG, "error - " + e.getMessage());
                    }


                } catch (Exception e) {

                    Log.i(TAG, "err - " + e.getMessage());

                    progressBar.dismiss();
                }
            }

            @Override
            public void OnFailure() {
                Log.i(TAG, "can not load learning words");
            }
        });

    }

    @Override
    public int getTheme() {
        // to set border radius
        return R.style.AppBottomSheetDialogTheme;
    }
}