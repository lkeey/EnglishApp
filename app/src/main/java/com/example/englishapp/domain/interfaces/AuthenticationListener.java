package com.example.englishapp.domain.interfaces;

public interface AuthenticationListener {
    void createNewAccount();
    void logInAccount();
    void onFailure();
}
