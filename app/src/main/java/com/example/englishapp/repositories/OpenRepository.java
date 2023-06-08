package com.example.englishapp.repositories;

import static com.example.englishapp.database.Constants.KEY_LOCATION;
import static com.example.englishapp.database.Constants.KEY_PROFILE;
import static com.example.englishapp.database.Constants.REMOTE_MSG_USER_SENDER;

import android.content.Intent;

import com.example.englishapp.database.DataBase;
import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.interfaces.OpeningListener;

public class OpenRepository {

    public void open(Intent data, OpeningListener listener) {

        String userUID = data.getStringExtra(REMOTE_MSG_USER_SENDER);
        boolean isShowMap = data.getBooleanExtra(KEY_LOCATION, false);
        boolean isShowProfile = data.getBooleanExtra(KEY_PROFILE, false);

        DataBase dataBase = new DataBase();

        if (userUID != null) {
            dataBase.loadData(new CompleteListener() {
                @Override
                public void OnSuccess() {
                    listener.showDiscussion();
                }

                @Override
                public void OnFailure() {
                    listener.onFail();
                }
            });
        } else if (isShowMap) {
            dataBase.loadData(new CompleteListener() {
                @Override
                public void OnSuccess() {
                    listener.showMap();
                }

                @Override
                public void OnFailure() {
                    listener.onFail();
                }
            });
        } else if (isShowProfile) {

            dataBase.loadData(new CompleteListener() {
                @Override
                public void OnSuccess() {
                    listener.showProfile();
                }

                @Override
                public void OnFailure() {
                    listener.onFail();
                }
            });

        } else {
            listener.startWorking();
        }
    }


}
