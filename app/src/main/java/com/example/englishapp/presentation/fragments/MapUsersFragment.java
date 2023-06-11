package com.example.englishapp.presentation.fragments;

import static com.example.englishapp.data.database.Constants.KEY_CHOSEN_USER_DATA;
import static com.example.englishapp.data.database.Constants.SHOW_FRAGMENT_DIALOG;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.englishapp.R;
import com.example.englishapp.domain.interfaces.UserListener;
import com.example.englishapp.data.models.UserModel;
import com.example.englishapp.domain.services.MapService;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapUsersFragment extends Fragment implements UserListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map_users, container, false);

        init(view);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(new MapService(MapUsersFragment.this, getContext()));
        }

        return view;
    }

    private void init(View view) {

        requireActivity().setTitle(R.string.nameMap);
        FloatingActionButton fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(v -> new UsersFragment().show(getChildFragmentManager(), "UsersFragment"));

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