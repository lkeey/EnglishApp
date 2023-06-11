package com.example.englishapp.domain.repositories;

import android.util.Log;

import com.example.englishapp.data.database.DataBase;
import com.example.englishapp.data.database.DataBasePersonalData;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.domain.interfaces.AuthenticationListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class LoginRepository {
    private static final String TAG = "AuthenticationRepository";
    private DataBase dataBase;
    private FirebaseAuth mAuth;

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
                            getToken(new CompleteListener() {
                                @Override
                                public void OnSuccess() {
                                    Log.i(TAG, "token updated");

                                    listener.logInAccount();
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

    public void getToken(CompleteListener listener) {

        FirebaseMessaging.getInstance().getToken()
            .addOnSuccessListener(s -> new DataBasePersonalData().updateToken(s, new CompleteListener() {
                @Override
                public void OnSuccess() {
                    listener.OnSuccess();
                    Log.i(TAG, "Token for - " + DataBasePersonalData.USER_MODEL.getUid());
                }

                @Override
                public void OnFailure() {
                    listener.OnFailure();
                }
            }))
            .addOnFailureListener(e -> listener.OnFailure());
    }

}
