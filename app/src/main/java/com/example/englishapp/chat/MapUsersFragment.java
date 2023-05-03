package com.example.englishapp.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.englishapp.R;
import com.google.android.gms.maps.SupportMapFragment;

public class MapUsersFragment extends Fragment {

    private static final String TAG = "MapUsersFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map_users, container, false);

        try {

            SupportMapFragment mapFragment = (SupportMapFragment) getParentFragmentManager().findFragmentById(R.id.google_map);

            mapFragment.getMapAsync(new MapService(getContext(), getParentFragmentManager()));
        } catch (Exception e) {
            Log.i(TAG, "Error - " + e.getMessage());
        }
        return view;
    }
}