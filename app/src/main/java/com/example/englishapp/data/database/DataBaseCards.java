package com.example.englishapp.data.database;

import static com.example.englishapp.data.database.Constants.KEY_AMOUNT_CARDS;
import static com.example.englishapp.data.database.Constants.KEY_AMOUNT_WORDS;
import static com.example.englishapp.data.database.Constants.KEY_AUTHOR;
import static com.example.englishapp.data.database.Constants.KEY_CARD_DESCRIPTION;
import static com.example.englishapp.data.database.Constants.KEY_CARD_ID;
import static com.example.englishapp.data.database.Constants.KEY_CARD_LEVEL;
import static com.example.englishapp.data.database.Constants.KEY_CARD_NAME;
import static com.example.englishapp.data.database.Constants.KEY_CATEGORY_ID;
import static com.example.englishapp.data.database.Constants.KEY_COLLECTION_CARDS;
import static com.example.englishapp.data.database.Constants.KEY_COLLECTION_STATISTICS;
import static com.example.englishapp.data.database.DataBaseCategories.CHOSEN_CATEGORY_ID;
import static com.example.englishapp.data.database.DataBasePersonalData.DATA_FIRESTORE;
import static com.example.englishapp.data.database.DataBasePersonalData.USER_MODEL;

import android.util.ArrayMap;
import android.util.Log;

import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.data.models.CardModel;
import com.example.englishapp.data.models.CategoryModel;
import com.example.englishapp.data.models.WordModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataBaseCards {

    private static final String TAG = "CardsDao";
    public static List<CardModel> LIST_OF_CARDS = new ArrayList<>();
    private WriteBatch batch;

    public void createCardData(ArrayList<WordModel> listOfWords, String name, String description, String level, CompleteListener listener) {

        Map<String, Object> cardData = new ArrayMap<>();

        DataBaseWords dataBaseWords = new DataBaseWords();

        String randomID = getCardId();

        cardData.put(KEY_CARD_ID, randomID);
        cardData.put(KEY_CARD_NAME, name);
        cardData.put(KEY_CARD_LEVEL, level);
        cardData.put(KEY_AMOUNT_WORDS, listOfWords.size());
        cardData.put(KEY_CARD_DESCRIPTION, description);
        cardData.put(KEY_AUTHOR, USER_MODEL.getName());
        cardData.put(KEY_CATEGORY_ID, CHOSEN_CATEGORY_ID);

        Log.i(TAG, "set card data");

        batch = DATA_FIRESTORE.batch();

        DocumentReference testDocument = null;
        if (randomID != null) {
            testDocument = DATA_FIRESTORE
                    .collection(KEY_COLLECTION_CARDS)
                    .document(randomID);
        }

        if (testDocument != null) {
            batch.set(testDocument, cardData, SetOptions.merge());
        }

        Log.i(TAG, "set batch");

        updateStatistics(listOfWords.size());

        batch.commit().addOnSuccessListener(unused -> {

            LIST_OF_CARDS.add(new CardModel(
                    randomID, name, level, description,
                    USER_MODEL.getName(), listOfWords.size()
            ));

            Log.i(TAG, "Card was successfully created - " + name);

            dataBaseWords.createWordsData(listOfWords, level, randomID, listener);

        }).addOnFailureListener(e -> {
            Log.i(TAG, "Can not create card - " + e.getMessage());

            listener.OnFailure();
        });
    }

    private String getCardId() {
        String randomID = null;

        while (true) {
            try {

                randomID = RandomStringUtils.random(20, true, true);

                Log.i(TAG, "random id - " + randomID);

                findCardById(randomID);

            } catch (Exception e) {
                Log.i(TAG, "not found card");

                break;
            }
        }

        return randomID;
    }

    private void updateStatistics(int size) {
        // update amount of tests in category
        Log.i(TAG, "CHOSEN_CATEGORY_ID - " + CHOSEN_CATEGORY_ID);

        // update statistics
        DocumentReference docReference = DATA_FIRESTORE
                .collection(KEY_COLLECTION_STATISTICS)
                .document(KEY_AMOUNT_CARDS);

        batch.update(docReference, KEY_AMOUNT_CARDS, FieldValue.increment(1));

        docReference = DATA_FIRESTORE
                .collection(KEY_COLLECTION_STATISTICS)
                .document(KEY_AMOUNT_WORDS);

        Log.i(TAG, "Size words - " + size);
        batch.update(docReference, KEY_AMOUNT_WORDS, FieldValue.increment(size));

        Log.i(TAG, "update statistics");

    }

    public void loadWordCardsData(CompleteListener listener) {
        Log.i(TAG, "Begin loading cards");

        LIST_OF_CARDS.clear();

        if (CHOSEN_CATEGORY_ID != null) {
            CategoryModel chosenCategory = new DataBaseCategories().findCategoryById(CHOSEN_CATEGORY_ID);

            DATA_FIRESTORE.collection(KEY_COLLECTION_CARDS)
                .limit(20)
                .whereEqualTo(KEY_CATEGORY_ID, chosenCategory.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.i(TAG, "Get cards");

                    try {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            CardModel cardModel = new CardModel();

                            cardModel.setId(documentSnapshot.getString(KEY_CARD_ID));
                            cardModel.setName(documentSnapshot.getString(KEY_CARD_NAME));
                            cardModel.setLevel(documentSnapshot.getString(KEY_CARD_LEVEL));
                            cardModel.setDescription(documentSnapshot.getString(KEY_CARD_DESCRIPTION));
                            cardModel.setAmountOfWords(Objects.requireNonNull(documentSnapshot.getLong(KEY_AMOUNT_WORDS)).intValue());
                            cardModel.setAuthor(documentSnapshot.getString(KEY_AUTHOR));

                            LIST_OF_CARDS.add(cardModel);

                            Log.i(TAG, "Find card - " + cardModel.getId());

                        }
                    } catch (Exception e) {
                        Log.i(TAG, "Card error - " + e.getMessage());
                    }

                    Log.i(TAG, "All good");

                    listener.OnSuccess();

                })
                .addOnFailureListener(e -> listener.OnFailure());
        } else {
            listener.OnFailure();
        }
    }

    public void findCardById(String cardId) {

        LIST_OF_CARDS.stream().filter(card -> card.getId().equals(cardId)).findAny()
                .orElseThrow(() -> new RuntimeException("not found"));
    }

}
