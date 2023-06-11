package com.example.englishapp.presentation.activities;

import static com.example.englishapp.data.database.Constants.KEY_INSTABUG;
import static com.example.englishapp.data.database.DataBasePersonalData.DATA_FIRESTORE;
import static com.example.englishapp.data.database.DataBasePersonalData.USER_MODEL;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.englishapp.data.database.Constants;
import com.google.firebase.firestore.DocumentReference;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;

public class BaseActivity extends AppCompatActivity {

    private DocumentReference document;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        document = DATA_FIRESTORE.collection(Constants.KEY_COLLECTION_USERS)
                .document(USER_MODEL.getUid());

        // instabug
        new Instabug.Builder(getApplication(), KEY_INSTABUG)
                .setInvocationEvents(InstabugInvocationEvent.SHAKE, InstabugInvocationEvent.SCREENSHOT, InstabugInvocationEvent.TWO_FINGER_SWIPE_LEFT)
                .build();
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
