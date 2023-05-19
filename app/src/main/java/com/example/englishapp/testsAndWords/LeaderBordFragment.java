package com.example.englishapp.testsAndWords;

import static com.example.englishapp.MVP.DataBase.USER_MODEL;
import static com.example.englishapp.messaging.Constants.KEY_CHOSEN_USER_DATA;
import static com.example.englishapp.messaging.Constants.SHOW_FRAGMENT_DIALOG;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.englishapp.MVP.DataBase;
import com.example.englishapp.MVP.MainActivity;
import com.example.englishapp.MVP.UserModel;
import com.example.englishapp.R;
import com.example.englishapp.chat.UserInfoFragment;
import com.example.englishapp.chat.UserListener;

public class LeaderBordFragment extends Fragment implements UserListener {

    private TextView totalUsers, userScore, userRank, dialogText;
    private RecyclerView recyclerUsers;
    private PlaceAdapter adapter;
    private Dialog progressBar;
    private ImageView userImg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_leader_bord, container, false);

        init(view);

        return view;
    }

    private void init(View view) {
        totalUsers = view.findViewById(R.id.totalUsers);
        userImg = view.findViewById(R.id.userImage);
        userScore = view.findViewById(R.id.userScore);
        userRank = view.findViewById(R.id.userRank);
        recyclerUsers = view.findViewById(R.id.recyclerUsers);

        totalUsers.setText("Amount Of Users: " + DataBase.LIST_OF_USERS.size());
        userScore.setText("Score: " + USER_MODEL.getScore());
        userRank.setText("Place: " + USER_MODEL.getPlace());

        Glide.with(LeaderBordFragment.this).load(USER_MODEL.getPathToImage()).into(userImg);

        progressBar = new Dialog(getContext());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarLoadingUserData);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerUsers.setLayoutManager(layoutManager);

        adapter = new PlaceAdapter(DataBase.LIST_OF_USERS, LeaderBordFragment.this, getContext());
        recyclerUsers.setAdapter(adapter);

        ((MainActivity) getActivity()).setTitle(R.string.nameLeaderBord);

    }

    @Override
    public void onUserClicked(UserModel user) {

        UserInfoFragment fragment = new UserInfoFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_CHOSEN_USER_DATA, user);
        fragment.setArguments(bundle);

        fragment.show(getParentFragmentManager(), SHOW_FRAGMENT_DIALOG);

    }
}