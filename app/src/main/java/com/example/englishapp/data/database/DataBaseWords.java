package com.example.englishapp.data.database;

import static com.example.englishapp.data.database.Constants.KEY_CARD_ID;
import static com.example.englishapp.data.database.Constants.KEY_COLLECTION_WORDS;
import static com.example.englishapp.data.database.Constants.KEY_WORD_CARD_ID;
import static com.example.englishapp.data.database.Constants.KEY_WORD_DESCRIPTION;
import static com.example.englishapp.data.database.Constants.KEY_WORD_ID;
import static com.example.englishapp.data.database.Constants.KEY_WORD_IMG;
import static com.example.englishapp.data.database.Constants.KEY_WORD_LEVEL;
import static com.example.englishapp.data.database.Constants.KEY_WORD_TEXT_EN;
import static com.example.englishapp.data.database.DataBaseCategories.CHOSEN_CATEGORY_ID;
import static com.example.englishapp.data.database.DataBasePersonalData.DATA_FIRESTORE;

import android.util.ArrayMap;
import android.util.Log;

import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.data.models.WordModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataBaseWords {
    private static final String TAG = "WordsDao";
    public static List<WordModel> LIST_OF_WORDS = new ArrayList<>();

    public void createWordsData(ArrayList<WordModel> listOfWords, String level, String cardId, CompleteListener listener) {

        WriteBatch batch = DATA_FIRESTORE.batch();

        for(int i=0; i < listOfWords.size(); i++) {

            Map<String, Object> wordData = new ArrayMap<>();

            WordModel wordModel = listOfWords.get(i);

            Log.i(TAG, "wordModel - " + wordModel.getTextEn() + " - " + wordModel.getImage());

            wordData.put(KEY_WORD_ID, CHOSEN_CATEGORY_ID + "_" + cardId + "_" + i);
            wordData.put(KEY_WORD_CARD_ID, cardId);
            wordData.put(KEY_WORD_TEXT_EN, wordModel.getTextEn());
            wordData.put(KEY_WORD_DESCRIPTION, wordModel.getDescription());
            wordData.put(KEY_WORD_LEVEL, level);
            wordData.put(KEY_WORD_IMG, wordModel.getImage());

            DocumentReference wordDocument = DATA_FIRESTORE
                    .collection(KEY_COLLECTION_WORDS)
                    .document(CHOSEN_CATEGORY_ID + "_" + cardId +"_" + i);

            batch.set(wordDocument, wordData, SetOptions.merge());

        }

        batch.commit().addOnSuccessListener(unused -> {

            Log.i(TAG, "Words were successfully added");

            listener.OnSuccess();

        }).addOnFailureListener(e -> {

            Log.i(TAG, "Fail to save words - " + e.getMessage());

            listener.OnFailure();

        });

    }

    public void loadWords(String cardId, CompleteListener listener) {

        LIST_OF_WORDS.clear();

        Query data = DATA_FIRESTORE.collection(KEY_COLLECTION_WORDS);

        if (cardId != null) {
            Log.i(TAG, "not null - " + cardId);

            data = data.whereEqualTo(KEY_CARD_ID, cardId);
        }

        data.get().addOnSuccessListener(queryDocumentSnapshots -> {

            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                WordModel wordModel = new WordModel();

                wordModel.setTextEn(documentSnapshot.getString(KEY_WORD_TEXT_EN));
                wordModel.setImage(documentSnapshot.getString(KEY_WORD_IMG));
                wordModel.setDescription(documentSnapshot.getString(KEY_WORD_DESCRIPTION));
                wordModel.setLevel(documentSnapshot.getString(KEY_WORD_LEVEL));

                LIST_OF_WORDS.add(wordModel);

                Log.i(TAG, "added - " + wordModel.getTextEn());

            }

            Log.i(TAG, "size - " + LIST_OF_WORDS.size());

            listener.OnSuccess();

            })
            .addOnFailureListener(e -> {

                Log.i(TAG, "error words - " + e.getMessage());

                listener.OnFailure();

            });

    }

}
