package com.example.englishapp.MVP;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "BeginApp";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // get current user
        mAuth = FirebaseAuth.getInstance();

        // Access a Cloud Firestore instance from your Activity
        DataBase.DATA_FIRESTORE = FirebaseFirestore.getInstance();

        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // if user exist
            if (mAuth.getCurrentUser() != null) {

                Log.i(TAG, "EMAIL - " + mAuth.getCurrentUser().getEmail());

                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);

                startActivity(intent);
                SplashActivity.this.finish();

            }

        }).start();
    }

}