package com.example.englishapp.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.englishapp.MVP.MainActivity;
import com.example.englishapp.R;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapUsersFragment extends Fragment {

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

            mapFragment.getMapAsync(new MapService(getContext(), getParentFragmentManager()));


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
}