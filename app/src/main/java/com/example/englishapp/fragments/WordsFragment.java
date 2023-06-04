package com.example.englishapp.fragments;

import static com.example.englishapp.database.Constants.KEY_CHOSEN_CARD;
import static com.example.englishapp.database.Constants.SHOW_FRAGMENT_DIALOG;

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
import com.example.englishapp.activities.MainActivity;
import com.example.englishapp.adapters.CardWordAdapter;
import com.example.englishapp.database.DataBase;
import com.example.englishapp.interfaces.CardClickedListener;
import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.models.CardModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class WordsFragment extends Fragment implements CardClickedListener {

    private static final String TAG = "WordsFragment";
    private RecyclerView cardRecycler;
    private CardWordAdapter cardAdapter;
    private FloatingActionButton fab;
    private EditText inputSearch;
    private Dialog progressBar;
    private TextView dialogText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_words, container, false);

        init(view);

        setListeners();

        return view;
    }

    private void init(View view) {
        ((MainActivity) getActivity()).setTitle(R.string.nameCardWords);

        cardRecycler = view.findViewById(R.id.cardsRecyclerView);
        fab = view.findViewById(R.id.fab);
        inputSearch = view.findViewById(R.id.inputSearch);

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogText = progressBar.findViewById(R.id.dialogText);

        dialogText.setText(R.string.progressBarOpening);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        cardRecycler.setLayoutManager(manager);

        progressBar.show();

        DataBase.loadWordCardsData(new CompleteListener() {
            @Override
            public void OnSuccess() {

                cardAdapter = new CardWordAdapter(DataBase.LIST_OF_CARDS, WordsFragment.this, getActivity());

                cardRecycler.setAdapter(cardAdapter);

                Log.i(TAG, "Cards were successfully loaded");

                progressBar.dismiss();
            }

            @Override
            public void OnFailure() {

                Toast.makeText(getActivity(), "Try Later", Toast.LENGTH_SHORT).show();

                progressBar.dismiss();
            }
        });

    }

    private void setListeners() {
        fab.setOnClickListener(v -> ((MainActivity) getActivity()).setFragment(new CreateWordCardFragment()));

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cardAdapter.cancelTimer();

            }

            @Override
            public void afterTextChanged(Editable key) {
                if(DataBase.LIST_OF_CARDS.size() != 0) {
                    cardAdapter.searchCards(key.toString());
                }
            }
        });
    }

    @Override
    public void onCardClicked(CardModel cardModel) {

//        Toast.makeText(getActivity(), "Clicked - " + cardModel.getName(), Toast.LENGTH_SHORT).show();

        Log.i(TAG, "clicked - " + cardModel.getName());

        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_CHOSEN_CARD, cardModel);

        WordCardInfoFragment fragment = new WordCardInfoFragment();
        fragment.setArguments(bundle);

        fragment.show(getParentFragmentManager(), SHOW_FRAGMENT_DIALOG);

    }

}