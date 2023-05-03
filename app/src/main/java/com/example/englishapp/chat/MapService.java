package com.example.englishapp.chat;

import static com.example.englishapp.MVP.DataBase.USER_MODEL;
import static com.example.englishapp.messaging.Constants.SHOW_FRAGMENT_DIALOG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.example.englishapp.Authentication.ProfileInfoDialogFragment;
import com.example.englishapp.MVP.CompleteListener;
import com.example.englishapp.MVP.DataBase;
import com.example.englishapp.MVP.UserModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapService implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private static final String TAG = "MapService";
    private final Context context;
    private final FragmentManager manager;

    public MapService(Context context, FragmentManager manager) {
        this.context = context;
        this.manager = manager;
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        Toast.makeText(context, latLng.latitude + " "
                + latLng.longitude, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        Toast.makeText(context, "LONG " + latLng.latitude + " "
                + latLng.longitude, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);

        Log.i(TAG, "ready");

        DataBase.getListOfUsers(new CompleteListener() {
            @Override
            public void OnSuccess() {
                for (UserModel user: DataBase.LIST_OF_USERS) {
                    Log.i(TAG, user.getName());
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(1, 1))
                            .title(user.getUid()));
                }

                googleMap.setOnMarkerClickListener(marker -> {
                    marker.getTitle();

                    ProfileInfoDialogFragment fragment = new ProfileInfoDialogFragment();

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("USER_MODEL", USER_MODEL);
                    fragment.setArguments(bundle);

                    fragment.show(manager, SHOW_FRAGMENT_DIALOG);

                    return false;
                });
            }

            @Override
            public void OnFailure() {
                Log.i(TAG, "fail");
                Toast.makeText(context, "Please, Try Later", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
