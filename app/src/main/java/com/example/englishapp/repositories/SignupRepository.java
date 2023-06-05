package com.example.englishapp.repositories;

import com.example.englishapp.database.DataBase;
import com.example.englishapp.database.DataBasePersonalData;
import com.example.englishapp.interfaces.AuthenticationListener;
import com.example.englishapp.interfaces.CompleteListener;
import com.google.firebase.auth.FirebaseAuth;

public class SignupRepository {

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
                                listener.onFailure();
                            }
                        });
                    } else {
                        listener.onFailure();
                    }
                });
    }

}
