package com.example.englishapp.data.repositories;

import android.util.Log;

import com.example.englishapp.data.database.DataBase;
import com.example.englishapp.data.database.DataBasePersonalData;
import com.example.englishapp.domain.interfaces.AuthenticationListener;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.google.firebase.auth.FirebaseAuth;

public class SignupRepository {

    private static final String TAG = "RepositorySignUp";
    private FirebaseAuth mAuth;
    private DataBase dataBase;
    private DataBasePersonalData dataBasePersonalData;

    public SignupRepository () {}

    public void signUpUser(String textEmail, String textPassword, AuthenticationListener listener)  {

        mAuth = FirebaseAuth.getInstance();
        dataBasePersonalData = new DataBasePersonalData();
        dataBase = new DataBase();

        mAuth.createUserWithEmailAndPassword(textEmail, textPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "Sign up successfully");
                        dataBasePersonalData.createUserData(textEmail, null, null, null, null, null, new CompleteListener() {
                            @Override
                            public void OnSuccess() {
                                dataBase.loadData(new CompleteListener() {
                                    @Override
                                    public void OnSuccess() {
                                        listener.createNewAccount();
                                    }

                                    @Override
                                    public void OnFailure() {
                                        listener.onFailure();
                                    }
                                });
                            }
                            @Override
                            public void OnFailure() {
                                Log.i(TAG, "can not create user data");
                                listener.onFailure();
                            }
                        });
                    } else {
                        Log.i(TAG, "fail - " + task.getException());
                        listener.onFailure();
                    }
                });
    }

}
