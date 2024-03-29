package com.example.englishapp.presentation.fragments;

import static com.example.englishapp.data.database.Constants.KEY_CHOSEN_CARD;
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
import com.example.englishapp.data.database.DataBaseCards;
import com.example.englishapp.domain.interfaces.CardClickedListener;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.domain.interfaces.RefreshListener;
import com.example.englishapp.data.models.CardModel;
import com.example.englishapp.presentation.activities.MainActivity;
import com.example.englishapp.presentation.adapters.CardWordAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class WordsFragment extends Fragment implements CardClickedListener, RefreshListener {

    private static final String TAG = "WordsFragment";
    private RecyclerView cardRecycler;
    private CardWordAdapter cardAdapter;
    private FloatingActionButton fab;
    private EditText inputSearch;
    private DataBaseCards dataBaseCards;
    private Dialog progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_words, container, false);

        dataBaseCards = new DataBaseCards();

        init(view);

        setListeners();

        return view;
    }

    private void init(View view) {

        requireActivity().setTitle(R.string.nameCardWords);

        cardRecycler = view.findViewById(R.id.cardsRecyclerView);
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
        cardRecycler.setLayoutManager(manager);

        progressBar.show();

        dataBaseCards.loadWordCardsData(new CompleteListener() {
            @Override
            public void OnSuccess() {

                cardAdapter = new CardWordAdapter(DataBaseCards.LIST_OF_CARDS, WordsFragment.this);

                cardRecycler.setAdapter(cardAdapter);

                Log.i(TAG, "Cards were successfully loaded - " + DataBaseCards.LIST_OF_CARDS.size());

                progressBar.dismiss();
            }

            @Override
            public void OnFailure() {

                Toast.makeText(getActivity(), getString(R.string.something_went_wrong_try_later), Toast.LENGTH_SHORT).show();

                progressBar.dismiss();
            }
        });

    }

    private void setListeners() {
        fab.setOnClickListener(v -> ((MainActivity) requireActivity()).setFragment(new CreateWordCardFragment(), false));

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (cardAdapter != null) {
                    cardAdapter.cancelTimer();
                }
            }

            @Override
            public void afterTextChanged(Editable key) {
                if(DataBaseCards.LIST_OF_CARDS.size() != 0 && cardAdapter != null) {
                    cardAdapter.searchCards(key.toString());
                }
            }
        });
    }

    @Override
    public void onCardClicked(CardModel cardModel) {

        Log.i(TAG, "clicked - " + cardModel.getName());

        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_CHOSEN_CARD, cardModel);

        WordCardInfoFragment fragment = new WordCardInfoFragment();
        fragment.setArguments(bundle);

        fragment.show(getParentFragmentManager(), SHOW_FRAGMENT_DIALOG);

    }

    @Override
    public void onRefresh() {
        progressBar.show();

        new DataBaseCards().loadWordCardsData(new CompleteListener() {
            @Override
            public void OnSuccess() {

                cardAdapter = new CardWordAdapter(DataBaseCards.LIST_OF_CARDS, WordsFragment.this);

                cardRecycler.setAdapter(cardAdapter);

                Log.i(TAG, "Cards were successfully loaded - " + DataBaseCards.LIST_OF_CARDS.size());

                progressBar.dismiss();
            }

            @Override
            public void OnFailure() {
                progressBar.dismiss();

                Toast.makeText(getActivity(), getString(R.string.database_isn_t_available), Toast.LENGTH_SHORT).show();
            }
        });
    }

}