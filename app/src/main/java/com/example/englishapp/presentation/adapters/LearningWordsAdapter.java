package com.example.englishapp.presentation.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.data.database.DataBasePersonalData;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.data.models.WordModel;
import com.example.englishapp.data.repositories.WordsRepository;

import java.util.List;

public class LearningWordsAdapter extends RecyclerView.Adapter<LearningWordsAdapter.ViewHolder>  {

    private static final String TAG = "AdapterWords";
    private final List<WordModel> listWords;
    private final WordsRepository wordsRepository;

    public LearningWordsAdapter(List<WordModel> listWords) {
        this.listWords = listWords;
        wordsRepository = new WordsRepository();
    }

    @NonNull
    @Override
    public LearningWordsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.learning_word_item_layout, parent, false);

        return new LearningWordsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LearningWordsAdapter.ViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return listWords.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView wordName, wordDescription;
        private final ImageView wordImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            wordName = itemView.findViewById(R.id.wordName);
            wordDescription = itemView.findViewById(R.id.wordDescription);
            wordImg = itemView.findViewById(R.id.wordImg);

        }

        public void setData(int position) {
            WordModel wordModel = listWords.get(position);

            wordsRepository.translateString(wordModel.getTextEn(), DataBasePersonalData.USER_MODEL.getLanguageCode(), new CompleteListener() {
                @Override
                public void OnSuccess() {
                    Log.i(TAG, "translated");

                    wordName.setText(wordModel.getTextEn() + " - " + WordsRepository.translatedText);
                }

                @Override
                public void OnFailure() {
                    Log.i(TAG, "can not translate");

                    wordName.setText(wordModel.getTextEn());
                }
            });
            
            try {
                wordDescription.setText(wordModel.getDescription());
                wordImg.setImageBitmap(wordsRepository.stringToBitMap(itemView.getContext(), wordModel.getImage()));
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }
}
