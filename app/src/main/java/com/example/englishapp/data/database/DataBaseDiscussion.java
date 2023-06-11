package com.example.englishapp.data.database;

import static com.example.englishapp.data.database.Constants.KEY_AMOUNT_DISCUSSIONS;
import static com.example.englishapp.data.database.Constants.KEY_AMOUNT_SENT_MESSAGES;
import static com.example.englishapp.data.database.Constants.KEY_COLLECTION_CHAT;
import static com.example.englishapp.data.database.Constants.KEY_COLLECTION_CONVERSATION;
import static com.example.englishapp.data.database.Constants.KEY_COLLECTION_STATISTICS;
import static com.example.englishapp.data.database.DataBasePersonalData.DATA_FIRESTORE;

import com.example.englishapp.domain.interfaces.CompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;

public class DataBaseDiscussion {

    public static String CURRENT_CONVERSATION_ID;

    public void sendMessage(HashMap<String, Object> mapMsg, CompleteListener listener) {

        DATA_FIRESTORE.collection(KEY_COLLECTION_CHAT).add(mapMsg)
            .addOnSuccessListener(documentReference -> {

                WriteBatch batch = DATA_FIRESTORE.batch();

                DocumentReference docReference = DATA_FIRESTORE
                        .collection(KEY_COLLECTION_STATISTICS)
                        .document(KEY_AMOUNT_SENT_MESSAGES);

                // increment amount of messages
                batch.update(docReference, KEY_AMOUNT_SENT_MESSAGES, FieldValue.increment(1));

                batch.commit()
                        .addOnSuccessListener(unused -> listener.OnSuccess())
                        .addOnFailureListener(e -> {
                            listener.OnFailure();
                        });

            })
            .addOnFailureListener(e -> listener.OnFailure());
    }

    public void addConversation(HashMap<String, Object> conversation, CompleteListener listener) {
        DATA_FIRESTORE.collection(KEY_COLLECTION_CONVERSATION)
            .add(conversation)
            .addOnSuccessListener(documentReference -> {

                CURRENT_CONVERSATION_ID = documentReference.getId();

                WriteBatch batch = DATA_FIRESTORE.batch();

                DocumentReference docReference = DATA_FIRESTORE
                        .collection(KEY_COLLECTION_STATISTICS)
                        .document(KEY_AMOUNT_DISCUSSIONS);

                // increment amount of discussions
                batch.update(docReference, KEY_AMOUNT_DISCUSSIONS, FieldValue.increment(1));

                batch.commit()
                        .addOnSuccessListener(unused -> listener.OnSuccess())
                        .addOnFailureListener(e -> listener.OnFailure());
            });
    }


}
