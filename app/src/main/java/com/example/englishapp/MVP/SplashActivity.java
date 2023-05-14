package com.example.englishapp.MVP;

import static com.example.englishapp.MVP.DataBase.loadData;
import static com.example.englishapp.messaging.Constants.KEY_USER_UID;
import static com.example.englishapp.messaging.Constants.REMOTE_MSG_USER_SENDER;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.englishapp.Authentication.MainAuthenticationActivity;
import com.example.englishapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "BeginApp";
    private FirebaseAuth mAuth;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getApplicationContext().getTheme().applyStyle(R.style.FullScreenTheme, true);

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

//            Initialize the app
//            FirebaseApp.initializeApp(/*context=*/ this);
//            FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
//            firebaseAppCheck.installAppCheckProviderFactory(
//                    PlayIntegrityAppCheckProviderFactory.getInstance());

//            check user's permissions

            receiveData();

        }).start();
    }

    private void receiveData() {
        try {
            Intent data = getIntent();
            String userUID = data.getStringExtra(REMOTE_MSG_USER_SENDER);

            Log.i(TAG, "send uid - " + userUID);

            if (userUID != null) {
                loadData(new CompleteListener() {
                    @Override
                    public void OnSuccess() {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);

                        intent.putExtra(KEY_USER_UID, userUID);

                        startActivity(intent);

                        SplashActivity.this.finish();

                        Log.i(TAG, "work");

                    }

                    @Override
                    public void OnFailure() {
                        Log.i(TAG, "Can not load data");
                    }
                });
            } else {

                checkPermissions();
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void beginWork() {
        // if user exist
        if (mAuth.getCurrentUser() != null) {
            try {

                Log.i(TAG, "EMAIL - " + mAuth.getCurrentUser().getEmail());

                DataBase.loadData(new CompleteListener() {
                    @Override
                    public void OnSuccess() {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);

                        startActivity(intent);
                        SplashActivity.this.finish();
                    }

                    @Override
                    public void OnFailure() {
                        Toast.makeText(SplashActivity.this, "Something went wrong! Try later", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.i(TAG, e.getMessage());

                mAuth.signOut();

                Intent intent = new Intent(SplashActivity.this, MainAuthenticationActivity.class);

                startActivity(intent);
                SplashActivity.this.finish();

            }
        } else {

            Log.i(TAG, "User not found");

            Intent intent = new Intent(SplashActivity.this, MainAuthenticationActivity.class);

            startActivity(intent);
            SplashActivity.this.finish();
        }
    }

    private void checkPermissions() {

        int wallpaper = ContextCompat.checkSelfPermission(this, Manifest.permission.SET_WALLPAPER);
        int internet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int notifications = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS);
        int sms = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int storage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int background_location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        int coarse_location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();


        if (wallpaper != PackageManager.PERMISSION_GRANTED) {

            listPermissionsNeeded.add(Manifest.permission.SET_WALLPAPER);

            Log.i(TAG, "wallpaper");

        }

        if (internet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);

            Log.i(TAG, "internet");

        }

        if (notifications != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);

            Log.i(TAG, "notifications");
        }

        if (sms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);

            Log.i(TAG, "sms");
        }

//        if (storage != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//
//            Log.i(TAG, "storage");
//        }
//
//        if (background_location != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
//
//            Log.i(TAG, "background_location");
//        }
//
//        if (coarse_location != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//
//            Log.i(TAG, "coarse_location");
//        }

        if (listPermissionsNeeded.size() > 0) {

            Log.i(TAG, "Show dialog");

            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(
                    new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS
            );

        } else {

            Log.i(TAG, "All permissions granted");

            beginWork();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();

                    beginWork();

                } else {
                    Log.i(TAG, Arrays.toString(permissions));

                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();

                    SplashActivity.this.finish();
                }
        }
    }

}