package com.example.englishapp.MVP;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishapp.Authentication.MainAuthenticationActivity;
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
//        mAuth.signOut();

        // Access a Cloud Firestore instance from your Activity
        DataBase.DATA_FIRESTORE = FirebaseFirestore.getInstance();

        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.i(TAG, "Launch App");

            // if user exist
            if (mAuth.getCurrentUser() != null) {

                Log.i(TAG, "EMAIL - " + mAuth.getCurrentUser().getEmail());
                DataBase.loadData(new CompleteListener() {
                    @Override
                    public void OnSuccess() {
                        Intent intent = new Intent(SplashActivity.this, FeedActivity.class);

                        startActivity(intent);
                        SplashActivity.this.finish();
                    }

                    @Override
                    public void OnFailure() {
                        Toast.makeText(SplashActivity.this, "Something went wrong! Try later", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {

                Log.i(TAG, "User not found");

                Intent intent = new Intent(SplashActivity.this, MainAuthenticationActivity.class);

                startActivity(intent);
                SplashActivity.this.finish();
            }

        }).start();
    }

}