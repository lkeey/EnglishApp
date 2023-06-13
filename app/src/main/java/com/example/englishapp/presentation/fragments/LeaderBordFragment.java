package com.example.englishapp.presentation.fragments;

import static com.example.englishapp.data.database.Constants.KEY_CHOSEN_USER_DATA;
import static com.example.englishapp.data.database.Constants.SHOW_FRAGMENT_DIALOG;
import static com.example.englishapp.data.database.DataBasePersonalData.USER_MODEL;

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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.englishapp.R;
import com.example.englishapp.data.database.DataBasePersonalData;
import com.example.englishapp.data.database.DataBaseUsers;
import com.example.englishapp.data.models.UserModel;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.domain.interfaces.UserListener;
import com.example.englishapp.presentation.adapters.PlaceAdapter;

public class LeaderBordFragment extends BaseFragment implements UserListener {

    private RecyclerView recyclerUsers;
    private PlaceAdapter adapter;
    private Dialog progressBar;
    private DataBaseUsers dataBaseUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_leader_bord, container, false);

        dataBaseUsers = new DataBaseUsers();

        init(view);

        return view;
    }

    private void init(View view) {
        TextView totalUsers = view.findViewById(R.id.totalUsers);
        ImageView userImg = view.findViewById(R.id.userImage);
        TextView userScore = view.findViewById(R.id.userScore);
        TextView userRank = view.findViewById(R.id.userRank);
        recyclerUsers = view.findViewById(R.id.recyclerUsers);

        totalUsers.setText("Amount Of Users: " + DataBaseUsers.LIST_OF_USERS.size());
        userScore.setText("Score: " + USER_MODEL.getScore());
        userRank.setText("Place: " + USER_MODEL.getPlace());

        Glide.with(LeaderBordFragment.this).load(USER_MODEL.getPathToImage()).into(userImg);
        userImg.setBackground(null);

        progressBar = new Dialog(getContext());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarLoadingUserData);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerUsers.setLayoutManager(layoutManager);

        progressBar.show();

        DataBasePersonalData dataBasePersonalData = new DataBasePersonalData();

        dataBasePersonalData.getUserData(new CompleteListener() {
            @Override
            public void OnSuccess() {

                dataBaseUsers.getListOfUsers(new CompleteListener() {
                    @Override
                    public void OnSuccess() {
                        adapter = new PlaceAdapter(DataBaseUsers.LIST_OF_USERS, LeaderBordFragment.this);
                        recyclerUsers.setAdapter(adapter);
                        progressBar.dismiss();
                    }

                    @Override
                    public void OnFailure() {
                        progressBar.dismiss();

                        Toast.makeText(getActivity(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void OnFailure() {

                Toast.makeText(getActivity(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();

                progressBar.dismiss();

            }
        });


        requireActivity().setTitle(R.string.nameLeaderBord);

    }

    @Override
    public void onUserClicked(UserModel user) {

        UserInfoFragment fragment = new UserInfoFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_CHOSEN_USER_DATA, user);
        fragment.setArguments(bundle);

        fragment.show(getParentFragmentManager(), SHOW_FRAGMENT_DIALOG);

    }

    @Override
    public void onRefresh() {
        progressBar.show();

        dataBaseUsers.getListOfUsers(new CompleteListener() {
            @Override
            public void OnSuccess() {
                adapter = new PlaceAdapter(DataBaseUsers.LIST_OF_USERS, LeaderBordFragment.this);
                recyclerUsers.setAdapter(adapter);
                progressBar.dismiss();
            }

            @Override
            public void OnFailure() {
                progressBar.dismiss();

                Toast.makeText(getActivity(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
