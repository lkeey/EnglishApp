package com.example.englishapp.fragments;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.Set;

public class WordCardInfoFragment extends BottomSheetDialogFragment {

    private static final String TAG = "FragmentWordCardInfo";
    private TextView textClose, nameCard, level, amountWords, description, dialogText;
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

                learnWords();

            }).addOnFailureListener(e -> {
                progressBar.dismiss();

                Log.i(TAG, "can not load");

            });

            RemoteModelManager modelManager = RemoteModelManager.getInstance();

            modelManager.getDownloadedModels(TranslateRemoteModel.class).addOnSuccessListener(new OnSuccessListener<Set<TranslateRemoteModel>>() {
                @Override
                public void onSuccess(Set<TranslateRemoteModel> translateRemoteModels) {
                    Log.i(TAG, "models - " + translateRemoteModels);
                }

            }).addOnFailureListener(e -> {
                Log.i(TAG, "warning");

            });
        });

    }

    private void receiveData() {
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            receivedCard = (CardModel) bundle.getSerializable(KEY_CHOSEN_CARD);

            nameCard.setText(receivedCard.getName());
            amountWords.setText("" + receivedCard.getAmountOfWords());
            level.setText(receivedCard.getLevel());
            description.setText(receivedCard.getDescription());

        }
    }

    private void init(View view) {
        textClose = view.findViewById(R.id.textClose);
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

        dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarOpening);

    }

    private void learnWords() {

        Log.i(TAG, "learnWords - " + receivedCard.getAmountOfWords() + " - " + receivedCard.getId());

        DataBaseLearningWords dataBaseLearningWords = new DataBaseLearningWords();
        dataBaseLearningWords.uploadLearningWords(getContext(), receivedCard.getId(), new CompleteListener() {
            @Override
            public void OnSuccess() {
                try {

                    Log.i(TAG, "amount loaded words - " + dataBaseLearningWords.LIST_OF_LEARNING_WORDS.size());

                    // create periodic task
                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

                    Intent intent = new Intent(((MainActivity) getActivity()), AlarmReceiver.class);
                    intent.putExtra(KEY_SHOW_NOTIFICATION_WORD, true);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(((MainActivity) getActivity()), 1, intent, PendingIntent.FLAG_MUTABLE);

                    // every 5 minute
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 5 * 60 * 1000, 2 * 60 * 1000, pendingIntent);

                    Log.i(TAG, "Successfully set");

                    // Creating a shared pref object with a file name "MySharedPref" in private mode
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();

                    // write all the data entered by the user in SharedPreference and apply
                    myEdit.putInt(WORD_COUNTER, 0);
                    myEdit.putString(KEY_LANGUAGE_CODE, DataBasePersonalData.USER_MODEL.getLanguageCode());
                    myEdit.apply();

                    Toast.makeText(getActivity(), "You will get notifications with chosen words", Toast.LENGTH_SHORT).show();

                    progressBar.dismiss();

                    WordCardInfoFragment.this.dismiss();

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