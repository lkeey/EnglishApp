package com.example.englishapp.domain.interfaces;

import com.example.englishapp.data.models.UserModel;

public interface ConversationListener {
    void onConversationClicked(UserModel userModel);
}
