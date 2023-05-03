package com.example.englishapp.MVP;

import static com.example.englishapp.MVP.DataBase.USER_MODEL;
import static com.example.englishapp.messaging.Constants.SHOW_FRAGMENT_DIALOG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.englishapp.Authentication.ProfileInfoDialogFragment;
import com.example.englishapp.Authentication.ProfileInfoFragment;
import com.example.englishapp.R;
import com.example.englishapp.chat.BaseActivity;
import com.example.englishapp.chat.ChatFragment;
import com.example.englishapp.messaging.FCMSend;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class FeedActivity extends BaseActivity {
    private static final String TAG = "ActivityFeed";
    private BottomNavigationView bottomNavigationView;
    private FrameLayout mainFrame;
    private Toolbar toolbar;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        init();

        receiveData();


        // send notification

//        try {
//            PushNotification notification = new PushNotification(
//                    new NotificationData("title", "text", "author"),
//                    USER_MODEL.getUid()
//            );
//
//            ApiClient.getClient().sendNotification(notification).enqueue(new Callback<PushNotification>() {
//                @Override
//                public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
//
////                    Log.i(TAG, response.body().toString());
//
//                    if (response.isSuccessful()) {
//                        Log.i(TAG, "Successss");
//                    } else {
//                        Log.i(TAG, "Can not");
//
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<PushNotification> call, Throwable t) {
//                    Log.i(TAG, "Can not 2");
//
//                }
//            });
//
//
//
//        } catch (Exception e) {
//            Log.i(TAG, e.getMessage());
//        }


        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(s -> DataBase.updateToken(s, new CompleteListener() {
                    @Override
                    public void OnSuccess() {

                        Log.i(TAG, "Token for - " + DataBase.USER_MODEL.getUid());


                        FCMSend send = new FCMSend(
                                USER_MODEL.getFcmToken(),
                                "title 1111",
                                "message 1111",
                                getApplicationContext(),
                                FeedActivity.this
                        );

                        Log.i(TAG, USER_MODEL.getFcmToken());

                        send.SendNotifications();
                    }

                    @Override
                    public void OnFailure() {

                    }
                }));



    }

    private void receiveData() {
        Intent intent = getIntent();
        boolean status = intent.getBooleanExtra(SHOW_FRAGMENT_DIALOG, false);

        Log.i(TAG, "STATUS " + status);

        if (status) {
            new ProfileInfoDialogFragment().show(getSupportFragmentManager(), SHOW_FRAGMENT_DIALOG);
        }
    }

    private void init() {
        try {
            toolbar = findViewById(R.id.toolbar);

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_btn_back);

        bottomNavigationView = findViewById(R.id.bottomNavBar);
        mainFrame = findViewById(R.id.nav_host_fragment_content_feed);

        onNavigationItemSelectedListener = item -> {

            switch (item.getItemId()) {
                case R.id.nav_home_menu:
                    setFragment(new ProfileInfoFragment());
                    return true;

                case R.id.nav_leader_menu:
                    setFragment(new ChatFragment());
                    return true;

                case R.id.nav_account_menu:
//                        setFragment(new AccountFragment());
                    return true;
            }
            return false;
        };

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(mainFrame.getId(), fragment)
                .addToBackStack(String.valueOf(fragment.getId()))
                .commit();
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            Log.i(TAG, "Stack of Fragments - " + getFragmentManager().getBackStackEntryCount());

            if (getFragmentManager().getBackStackEntryCount() > 2){
                getFragmentManager().popBackStackImmediate();

            } else {
                super.onBackPressed();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


}