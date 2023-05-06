package com.example.englishapp.chat;

import static com.example.englishapp.MVP.DataBase.DATA_FIRESTORE;
import static com.example.englishapp.MVP.DataBase.USER_MODEL;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.englishapp.messaging.Constants;
import com.google.firebase.firestore.DocumentReference;

public class BaseActivity extends AppCompatActivity {

    private DocumentReference document;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        document = DATA_FIRESTORE.collection(Constants.KEY_COLLECTION_USERS)
                .document(USER_MODEL.getUid());
    }

    @Override
    protected void onPause() {
        super.onPause();

        document.update(
                Constants.KEY_AVAILABILITY, false
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        document.update(
                Constants.KEY_AVAILABILITY, true
        );
    }

}