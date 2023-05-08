package com.example.englishapp.chat;

import static com.example.englishapp.MVP.DataBase.USER_MODEL;
import static com.example.englishapp.messaging.Constants.KEY_CHOSEN_USER_DATA;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.englishapp.MVP.FeedActivity;
import com.example.englishapp.MVP.UserModel;
import com.example.englishapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class UserInfoFragment extends BottomSheetDialogFragment {

    private UserModel receivedUser;
    private TextView userName, textClose;
    private ImageView imgUser;
    private Button btnSendMsg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        init(view);

        setListeners();

        receiveData();

        return view;
    }

    private void setListeners() {
        textClose.setOnClickListener(v -> UserInfoFragment.this.dismiss());

        btnSendMsg.setOnClickListener(v -> {
            if (!receivedUser.getUid().equals(USER_MODEL.getUid())) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_CHOSEN_USER_DATA, receivedUser);
                DiscussFragment fragment = new DiscussFragment();
                fragment.setArguments(bundle);

                ((FeedActivity) getActivity()).setFragment(fragment);

                UserInfoFragment.this.dismiss();
            } else {
                Toast.makeText(getActivity(), "It's you!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void receiveData() {
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            receivedUser = (UserModel) bundle.getSerializable(KEY_CHOSEN_USER_DATA);

            setData();

        }
    }

    private void setData() {

        userName.setText(receivedUser.getName());

        Glide.with(getContext()).load(receivedUser.getPathToImage()).into(imgUser);

    }

    private void init(View view) {

        textClose = view.findViewById(R.id.textClose);
        imgUser = view.findViewById(R.id.imgUser);
        userName = view.findViewById(R.id.userName);
        btnSendMsg = view.findViewById(R.id.btnSendMessage);

    }

    @Override
    public int getTheme() {
        // to set border radius

        return R.style.AppBottomSheetDialogTheme;
    }
}
