package com.example.englishapp.chat;

import static com.example.englishapp.MVP.DataBase.USER_MODEL;
import static com.example.englishapp.messaging.Constants.KEY_CHOSEN_USER_DATA;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.MVP.CompleteListener;
import com.example.englishapp.MVP.DataBase;
import com.example.englishapp.MVP.MainActivity;
import com.example.englishapp.MVP.UserModel;
import com.example.englishapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class UsersFragment extends BottomSheetDialogFragment implements UserListener {

    private static final String TAG = "FragmentUsers";
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private Dialog progressBar;
    private TextView dialogText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "Show List Of Users");

        View view = inflater.inflate(R.layout.fragment_users, container, false);

        init(view);

        return view;
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarLoadingUserData);

        DataBase.getListOfUsers(new CompleteListener() {
            @Override
            public void OnSuccess() {
                Log.i(TAG, "Successfully got user data - " + DataBase.LIST_OF_USERS.size());

                UserAdapter userAdapter = new UserAdapter(DataBase.LIST_OF_USERS, UsersFragment.this, getContext());
                recyclerView.setAdapter(userAdapter);

                LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                manager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(manager);

            }

            @Override
            public void OnFailure() {
                Log.i(TAG, "Can not get users");
            }
        });

    }

    @Override
    public void onUserClicked(UserModel user) {
        if (!user.getUid().equals(USER_MODEL.getUid())){
            Log.i(TAG, "USER - " + user.getName());

            Bundle bundle = new Bundle();
            bundle.putSerializable(KEY_CHOSEN_USER_DATA, user);
            DiscussFragment fragment = new DiscussFragment();
            fragment.setArguments(bundle);

            ((MainActivity) getActivity()).setFragment(fragment);

        } else {
            Toast.makeText(getActivity(), "It's You!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getTheme() {
        // to set border radius

        return R.style.AppBottomSheetDialogTheme;
    }
}