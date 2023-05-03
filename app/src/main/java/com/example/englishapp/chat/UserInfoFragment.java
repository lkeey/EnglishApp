package com.example.englishapp.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.englishapp.MVP.UserModel;
import com.example.englishapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class UserInfoFragment extends BottomSheetDialogFragment {

    private UserModel receivedUser;
    private TextView userName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        init(view);

        receiveData();

        return view;
    }

    private void receiveData() {
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            receivedUser = (UserModel) bundle.getSerializable("USER_MODEL");

            userName.setText(receivedUser.getName());
        }
    }

    private void init(View view) {
        // TODO close fragment if it needs

        userName = view.findViewById(R.id.userName);
    }

    @Override
    public int getTheme() {
        // to set border radius

        return R.style.AppBottomSheetDialogTheme;
    }
}