package com.example.englishapp.activities;

import static com.example.englishapp.repositories.PermissionRepository.REQUEST_ID_MULTIPLE_PERMISSIONS;
import static com.example.englishapp.database.Constants.KEY_LOCATION;
import static com.example.englishapp.database.Constants.KEY_USER_UID;
import static com.example.englishapp.database.Constants.REMOTE_MSG_USER_SENDER;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.englishapp.database.DataBasePersonalData;
import com.example.englishapp.repositories.PermissionRepository;
import com.example.englishapp.R;
import com.example.englishapp.database.DataBase;
import com.example.englishapp.interfaces.CompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "BeginApp";
    private FirebaseAuth mAuth;
    private DataBase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // get current user
        mAuth = FirebaseAuth.getInstance();

        dataBase = new DataBase();

        DataBasePersonalData dataBasePersonalData = new DataBasePersonalData();

        // Access a Cloud Firestore instance from your Activity
        dataBasePersonalData.DATA_FIRESTORE = FirebaseFirestore.getInstance();

        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.i(TAG, "Launch App");

            receiveData();

        }).start();

        init();
    }

    private void init() {
        ImageView imgLogo = findViewById(R.id.appLogo);

        Animation blinking = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blinking);

        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(blinking);

        imgLogo.setAnimation(animation);
    }

    private void receiveData() {
        try {
            Intent data = getIntent();

            String userUID = data.getStringExtra(REMOTE_MSG_USER_SENDER);
            boolean isShowMap = data.getBooleanExtra(KEY_LOCATION, false);

            Log.i(TAG, "send uid - " + userUID);

            if (userUID != null) {
                dataBase.loadData(new CompleteListener() {
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
            } else if (isShowMap) {
                dataBase.loadData(new CompleteListener() {
                    @Override
                    public void OnSuccess() {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);

                        intent.putExtra(KEY_LOCATION, true);

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

//                checkPermissions();

                PermissionRepository repository = new PermissionRepository(SplashActivity.this);
                List<String> permissions = repository.checkPermissions();

                if (permissions.size() > 0) {

                    Log.i(TAG, "Show dialog");

                    ActivityCompat.requestPermissions(this, permissions.toArray(
                            new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS
                    );

                } else {

                    Log.i(TAG, "All permissions granted");

                    beginWork();
                }

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

                dataBase.loadData(new CompleteListener() {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionRepository.REQUEST_ID_MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                beginWork();

            } else {
                Log.i(TAG, Arrays.toString(permissions));

                Toast.makeText(this, "Permissions Denied!", Toast.LENGTH_SHORT).show();

                SplashActivity.this.finish();
            }
        }
    }

}