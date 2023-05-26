package com.example.englishapp.testsAndWords;

import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.MVP.CardModel;
import com.example.englishapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CardWordAdapter extends RecyclerView.Adapter<CardWordAdapter.ViewHolder> {

    private static final String TAG = "CardWordAdapter";
    private List<CardModel> cardModelList;
    private List<CardModel> allCards;
    private CardClickedListener listener;
    private Context context;
    private Timer timer;

    public CardWordAdapter(List<CardModel> cardModelList, CardClickedListener listener, Context context) {

        this.cardModelList = cardModelList;
        this.listener = listener;
        this.context = context;
        allCards = cardModelList;

    }

    @NonNull
    @Override
    public CardWordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_layout, parent, false);

        return new CardWordAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardWordAdapter.ViewHolder holder, int position) {

        holder.setData(position);

    }

    @Override
    public int getItemCount() {
        return cardModelList.size();
    }

    public void searchCards(String searchKeyword) {
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            if(searchKeyword.trim().isEmpty()) {
                cardModelList = allCards;
            } else {

                ArrayList<CardModel> cardWithKey = new ArrayList<>();
                for(CardModel card: allCards) {

                    if (card.getName().contains(searchKeyword.toLowerCase())) {
                        cardWithKey.add(card);
                    }
                }

                cardModelList = cardWithKey;
            }

            new android.os.Handler(Looper.getMainLooper()).post(() -> notifyDataSetChanged());
            }
        }, 500);
    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title, numberOfCards, level;
        private ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            numberOfCards = itemView.findViewById(R.id.numberOfCards);
            level = itemView.findViewById(R.id.level);
            progressBar = itemView.findViewById(R.id.progressBar);

        }

        public void setData(int position) {

            CardModel cardModel = cardModelList.get(position);

            title.setText(cardModel.getName());

            numberOfCards.setText("" + cardModel.getAmountOfWords());

            level.setText(cardModel.getLevel());

            switch (cardModel.getLevel()) {

                case "A2": {
                    progressBar.setProgress(40);

                }

                case "B1": {
                    progressBar.setProgress(60);

                }

                case "B2": {
                    progressBar.setProgress(70);

                }

                case "C1": {
                    progressBar.setProgress(90);

                }

                case "C2": {
                    progressBar.setProgress(100);

                }

                default: {

                    Log.i(TAG, "level - " + cardModel.getLevel());

                    progressBar.setProgress(20);
                    progressBar.getIndeterminateDrawable().setColorFilter(Color.GREEN, android.graphics.PorterDuff.Mode.MULTIPLY);
                }
            }


            itemView.setOnClickListener(v -> listener.onCardClicked(cardModel));
        }

    }
}
