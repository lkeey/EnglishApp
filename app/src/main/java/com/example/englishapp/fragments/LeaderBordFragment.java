package com.example.englishapp.fragments;

import static com.example.englishapp.database.Constants.KEY_CHOSEN_USER_DATA;
import static com.example.englishapp.database.Constants.SHOW_FRAGMENT_DIALOG;
import static com.example.englishapp.database.DataBasePersonalData.USER_MODEL;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.englishapp.R;
import com.example.englishapp.activities.MainActivity;
import com.example.englishapp.adapters.PlaceAdapter;
import com.example.englishapp.database.DataBasePersonalData;
import com.example.englishapp.database.DataBaseUsers;
import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.interfaces.UserListener;
import com.example.englishapp.models.UserModel;

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

        totalUsers.setText("Amount Of Users: " + DataBaseUsers.LIST_OF_USERS.size());
        userScore.setText("Score: " + USER_MODEL.getScore());
        userRank.setText("Place: " + USER_MODEL.getPlace());

        Glide.with(LeaderBordFragment.this).load(USER_MODEL.getPathToImage()).into(userImg);

        progressBar = new Dialog(getContext());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarLoadingUserData);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerUsers.setLayoutManager(layoutManager);

        progressBar.show();

        DataBasePersonalData dataBasePersonalData = new DataBasePersonalData();

        dataBasePersonalData.getUserData(new CompleteListener() {
            @Override
            public void OnSuccess() {

                DataBaseUsers.getListOfUsers(new CompleteListener() {
                    @Override
                    public void OnSuccess() {
                        adapter = new PlaceAdapter(DataBaseUsers.LIST_OF_USERS, LeaderBordFragment.this, getContext());
                        recyclerUsers.setAdapter(adapter);
                        progressBar.dismiss();


                    }

                    @Override
                    public void OnFailure() {

                        progressBar.dismiss();

                        Toast.makeText(getActivity(), "Try Later", Toast.LENGTH_SHORT).show();

                    }
                });
            }
            @Override
            public void OnFailure() {

                Toast.makeText(getActivity(), "Try Later", Toast.LENGTH_SHORT).show();

                progressBar.dismiss();

            }
        });


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