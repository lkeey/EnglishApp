package com.example.englishapp.repositories;

import android.util.Log;

import com.example.englishapp.database.DataBase;
import com.example.englishapp.database.DataBasePersonalData;
import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.interfaces.AuthenticationListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class LoginRepository {
    private static final String TAG = "AuthenticationRepository";
    private DataBase dataBase;
    private FirebaseAuth mAuth;

    public LoginRepository() {}

    public void firebaseAuthWithGoogle(String idToken, AuthenticationListener googleAuthentication) {

        mAuth = FirebaseAuth.getInstance();
        dataBase = new DataBase();
        DataBasePersonalData dataBasePersonalData = new DataBasePersonalData();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(task -> {

                Log.i(TAG, "Completed task " + task);

                if (task.isSuccessful()) {

                    FirebaseUser user = mAuth.getCurrentUser();

                    if (Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser()) {
                        if (user != null) {
                            dataBasePersonalData.createUserData(Objects.requireNonNull(user.getEmail()).trim(), user.getDisplayName(), null, null, user.getPhoneNumber(), null, new CompleteListener() {
                                @Override
                                public void OnSuccess() {
                                    dataBase.loadData(new CompleteListener() {
                                        @Override
                                        public void OnSuccess() {
                                            googleAuthentication.createNewAccount();
                                        }

                                        @Override
                                        public void OnFailure() {
                                            googleAuthentication.onFailure();
                                        }
                                    });

                                }

                                @Override
                                public void OnFailure() {
                                    googleAuthentication.onFailure();
                                }
                            });
                        }

                    } else {
                        dataBase.loadData(new CompleteListener() {
                            @Override
                            public void OnSuccess() {
                                googleAuthentication.logInAccount();
                            }

                            @Override
                            public void OnFailure() {
                                googleAuthentication.onFailure();
                            }
                        });
                    }
                } else {
                    googleAuthentication.onFailure();
                }
            });
    }

    public void login(String email, String password, AuthenticationListener listener) {

        mAuth = FirebaseAuth.getInstance();
        dataBase = new DataBase();

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    dataBase.loadData(new CompleteListener() {
                        @Override
                        public void OnSuccess() {
                            listener.logInAccount();
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
