package com.example.englishapp.repositories;

import static com.example.englishapp.database.Constants.KEY_IS_WORDS;
import static com.example.englishapp.database.Constants.KEY_LOCATION;
import static com.example.englishapp.database.Constants.KEY_PROFILE;
import static com.example.englishapp.database.Constants.KEY_TEST_TIME;
import static com.example.englishapp.database.Constants.KEY_USER_UID;
import static com.example.englishapp.database.Constants.SHOW_FRAGMENT_DIALOG;
import static com.example.englishapp.database.DataBaseUsers.LIST_OF_USERS;

import android.content.Intent;
import android.util.Log;

import com.example.englishapp.interfaces.TasksChecking;

public class TasksRepository {

    private static final String TAG = "RepositoryTasks";

    public void checkTasks(Intent intent, TasksChecking listener) {

        boolean status = intent.getBooleanExtra(SHOW_FRAGMENT_DIALOG, false);
        boolean isShowMap = intent.getBooleanExtra(KEY_LOCATION, false);
        boolean isWordExam = intent.getBooleanExtra(KEY_IS_WORDS, false);
        boolean isShowProfile = intent.getBooleanExtra(KEY_PROFILE, false);
        String userUID = intent.getStringExtra(KEY_USER_UID);
        long totalTime = intent.getLongExtra(KEY_TEST_TIME, -1);

        Log.i(TAG, "STATUS " + status);
        Log.i(TAG, "UserUID - " + userUID + " - " + LIST_OF_USERS.size());
        Log.i(TAG, "totalTime - " + totalTime + " - " + isWordExam);

        if (status) {
            listener.showDialog();
        } else if (userUID != null) {
            listener.showDiscussion();
        } else if (totalTime != -1L) {
            listener.checkingExam();
        } else if (isShowMap) {
            listener.showMap();
        } else if (isShowProfile) {
            listener.showProfile();
        }

    }


}
