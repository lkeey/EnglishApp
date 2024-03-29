package com.example.englishapp.data.database;

import android.content.Context;
import android.util.Log;

import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.domain.interfaces.RoomDao;
import com.example.englishapp.data.models.WordModel;

import java.util.ArrayList;
import java.util.List;

public class DataBaseLearningWords {

    public static final String TAG = "LearningWords";
    public static List<WordModel> LIST_OF_LEARNING_WORDS = new ArrayList<>();
    public RoomDao roomDao;

    public void loadLearningWords(Context context, CompleteListener listener) {

        try {
            Log.i(TAG, "load words");

            LIST_OF_LEARNING_WORDS.clear();


            LIST_OF_LEARNING_WORDS =
                    RoomDataBase.getDatabase(context)
                            .roomDao()
                            .getAllWords();

            listener.OnSuccess();

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());

            listener.OnFailure();
        }
    }

    public void uploadLearningWords(Context context, String cardId, CompleteListener listener) {

        LIST_OF_LEARNING_WORDS.clear();

        roomDao = RoomDataBase.getDatabase(context)
                .roomDao();

        roomDao.deleteAll();

        DataBaseWords dataBaseWords = new DataBaseWords();

        dataBaseWords.loadWords(cardId, new CompleteListener() {
            @Override
            public void OnSuccess() {
                for (WordModel wordModel: DataBaseWords.LIST_OF_WORDS) {

                    roomDao.insertWord(wordModel);

                    LIST_OF_LEARNING_WORDS.add(wordModel);

                    Log.i(TAG, "added - " + wordModel.getTextEn());

                }

                listener.OnSuccess();
            }

            @Override
            public void OnFailure() {
                listener.OnFailure();
            }
        });

    }

    public void deleteLearningWords(Context context, CompleteListener listener) {

        try {
            LIST_OF_LEARNING_WORDS.clear();

            RoomDataBase.getDatabase(context)
                    .roomDao()
                    .deleteAll();

            listener.OnSuccess();

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());

            listener.OnFailure();
        }

    }

}
