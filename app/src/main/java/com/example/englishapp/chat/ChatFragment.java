package com.example.englishapp.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.englishapp.MVP.CompleteListener;
import com.example.englishapp.MVP.DataBase;
import com.example.englishapp.R;
import com.google.firebase.messaging.FirebaseMessaging;

public class ChatFragment extends Fragment {

    private static final String TAG = "FragmentChat";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        init();

        getToken(new CompleteListener() {
            @Override
            public void OnSuccess() {
                Log.i(TAG, "Token successfully got");
            }

            @Override
            public void OnFailure() {
                Log.i(TAG, "Can not get token");

            }
        });

        new UsersFragment().show(getChildFragmentManager(), "UsersFragment");

        return view;
    }

    private void init() {
    }

    private void getToken(CompleteListener listener) {

        FirebaseMessaging.getInstance().getToken()
            .addOnSuccessListener(s -> DataBase.updateToken(s, new CompleteListener() {
                @Override
                public void OnSuccess() {
                    listener.OnSuccess();
                    Log.i(TAG, DataBase.USER_MODEL.getUid());
                }

                @Override
                public void OnFailure() {
                    listener.OnFailure();
                }
            }))
            .addOnFailureListener(e -> listener.OnFailure());

    }

}