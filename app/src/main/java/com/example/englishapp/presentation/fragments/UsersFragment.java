package com.example.englishapp.presentation.fragments;

import static com.example.englishapp.data.database.Constants.KEY_CHOSEN_USER_DATA;
import static com.example.englishapp.data.database.DataBasePersonalData.USER_MODEL;

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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.presentation.activities.MainActivity;
import com.example.englishapp.presentation.adapters.UserAdapter;
import com.example.englishapp.data.database.DataBaseUsers;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.domain.interfaces.UserListener;
import com.example.englishapp.data.models.UserModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class UsersFragment extends BottomSheetDialogFragment implements UserListener {

    private static final String TAG = "FragmentUsers";
    private RecyclerView recyclerView;
    private DataBaseUsers dataBaseUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataBaseUsers = new DataBaseUsers();

        View view = inflater.inflate(R.layout.fragment_users, container, false);

        init(view);

        return view;
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);

        Dialog progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarLoadingUserData);

        dataBaseUsers.getListOfUsers(new CompleteListener() {
            @Override
            public void OnSuccess() {
                Log.i(TAG, "Successfully got user data - " + DataBaseUsers.LIST_OF_USERS.size());

                UserAdapter userAdapter = new UserAdapter(DataBaseUsers.LIST_OF_USERS, UsersFragment.this);
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

            ((MainActivity) requireActivity()).setFragment(fragment, false);

            UsersFragment.this.dismiss();

        } else {
            Toast.makeText(getActivity(), getString(R.string.it_s_you), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getTheme() {
        // to set border radius

        return R.style.AppBottomSheetDialogTheme;
    }
}
