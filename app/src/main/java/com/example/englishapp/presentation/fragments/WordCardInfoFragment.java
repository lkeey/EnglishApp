package com.example.englishapp.presentation.fragments;

import static com.example.englishapp.data.database.Constants.KEY_CHOSEN_CARD;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.englishapp.R;
import com.example.englishapp.domain.interfaces.LearningWordListener;
import com.example.englishapp.data.models.CardModel;
import com.example.englishapp.domain.repositories.BeginLearningRepository;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class WordCardInfoFragment extends BottomSheetDialogFragment {

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

            new BeginLearningRepository().loadModel(getContext(), receivedCard.getId(), new LearningWordListener() {
                @Override
                public void beginLearning() {
                    Toast.makeText(getActivity(), "You will get notifications with chosen words", Toast.LENGTH_SHORT).show();

                    progressBar.dismiss();

                    WordCardInfoFragment.this.dismiss();
                }

                @Override
                public void cancelLearning() {
                    Toast.makeText(getActivity(), "You have already learned this words", Toast.LENGTH_SHORT).show();

                    progressBar.dismiss();
                }

                @Override
                public void otherLearning() {
                    Toast.makeText(getActivity(), "You are learning other words", Toast.LENGTH_SHORT).show();

                    progressBar.dismiss();
                }

                @Override
                public void onFail() {
                    progressBar.dismiss();

                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
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
        dialogText.setText(R.string.progressBarLoadingModel);

    }

    @Override
    public int getTheme() {
        // to set border radius
        return R.style.AppBottomSheetDialogTheme;
    }
}