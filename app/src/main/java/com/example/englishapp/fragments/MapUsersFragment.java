package com.example.englishapp.fragments;

import static com.example.englishapp.database.Constants.KEY_CHOSEN_USER_DATA;
import static com.example.englishapp.database.Constants.SHOW_FRAGMENT_DIALOG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.englishapp.R;
import com.example.englishapp.activities.MainActivity;
import com.example.englishapp.interfaces.UserListener;
import com.example.englishapp.models.UserModel;
import com.example.englishapp.services.MapService;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapUsersFragment extends Fragment implements UserListener {

    private static final String TAG = "MapUsersFragment";
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map_users, container, false);

        init(view);

        try {

            SupportMapFragment mapFragment = (SupportMapFragment)
                    getChildFragmentManager().findFragmentById(R.id.google_map);

            mapFragment.getMapAsync(new MapService(MapUsersFragment.this, getContext()));


        } catch (Exception e) {
            Log.i(TAG, "Error - " + e.getMessage());
        }
        return view;
    }

    private void init(View view) {

        ((MainActivity) getActivity()).setTitle("Map");
        fab = view.findViewById(R.id.fab);

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