package com.example.englishapp.data.database;

import android.util.Log;

import com.example.englishapp.domain.interfaces.CompleteListener;

public class DataBase {
    private static final String TAG = "FirestoreDB";

    public void loadData(CompleteListener listener) {
        Log.i(TAG, "Load Data");

        DataBasePersonalData dataBasePersonalData = new DataBasePersonalData();

        DataBaseCategories dataBaseCategories = new DataBaseCategories();

        DataBaseUsers dataBaseUsers = new DataBaseUsers();

        dataBasePersonalData.getUserData(new CompleteListener() {
            @Override
            public void OnSuccess() {
                Log.i(TAG, "User data was loaded");
                dataBaseUsers.getListOfUsers(new CompleteListener() {
                    @Override
                    public void OnSuccess() {
                        Log.i(TAG, "Users were successfully loaded");
                        dataBaseCategories.getListOfCategories(new CompleteListener() {
                            @Override
                            public void OnSuccess() {
                                Log.i(TAG, "Categories were successfully loaded");
                                new DataBaseBookmarks().loadBookmarkIds(new CompleteListener() {
                                    @Override
                                    public void OnSuccess() {
                                        Log.i(TAG, "bookmarked successfully loaded");
                                        listener.OnSuccess();
                                    }
                                    @Override
                                    public void OnFailure() {
                                        Log.i(TAG, "can not load bookmark ids");
                                        listener.OnFailure();
                                    }
                                });
                            }
                            @Override
                            public void OnFailure() {
                                Log.i(TAG, "Can not load categories");
                                listener.OnFailure();
                            }
                        });
                    }
                    @Override
                    public void OnFailure() {
                        Log.i(TAG, "Exception: User Data can not be loaded");
                        listener.OnFailure();
                    }
                });
            }
            @Override
            public void OnFailure() {
                Log.i(TAG, "Can not load users");
                listener.OnFailure();
            }
        });
    }
}
