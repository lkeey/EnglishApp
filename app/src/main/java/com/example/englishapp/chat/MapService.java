package com.example.englishapp.chat;

import static com.example.englishapp.MVP.DataBase.findUserById;
import static com.example.englishapp.messaging.Constants.KEY_BOOKMARKS;
import static com.example.englishapp.messaging.Constants.KEY_CHOSEN_USER_DATA;
import static com.example.englishapp.messaging.Constants.KEY_DOB;
import static com.example.englishapp.messaging.Constants.KEY_EMAIL;
import static com.example.englishapp.messaging.Constants.KEY_FCM_TOKEN;
import static com.example.englishapp.messaging.Constants.KEY_GENDER;
import static com.example.englishapp.messaging.Constants.KEY_MOBILE;
import static com.example.englishapp.messaging.Constants.KEY_NAME;
import static com.example.englishapp.messaging.Constants.KEY_PROFILE_IMG;
import static com.example.englishapp.messaging.Constants.KEY_SCORE;
import static com.example.englishapp.messaging.Constants.KEY_USER_UID;
import static com.example.englishapp.messaging.Constants.SHOW_FRAGMENT_DIALOG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.example.englishapp.MVP.CompleteListener;
import com.example.englishapp.MVP.DataBase;
import com.example.englishapp.MVP.UserModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MapService implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {
//    TODO https://stackoverflow.com/questions/44670356/how-to-store-location-in-firebase-in-real-time
    private static final String TAG = "MapService";
    private final Context context;
    private final FragmentManager manager;
    private static ArrayList<MarkerOptions> markers;

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
                    try {

                        MarkerOptions marker = new MarkerOptions()
                                .position(new LatLng(1, 1))
                                .title(user.getName())
                                .snippet(user.getUid())
                                .flat(true);

                        googleMap.addMarker(marker);
                        markers.add(marker);

//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.app_logo)));

                    } catch (Exception e) {
                        Log.i(TAG, e.getMessage());
                    }
                }

                googleMap.setOnMarkerClickListener(marker -> {
                    try {

                        UserInfoFragment fragment = new UserInfoFragment();

                        UserModel user = findUserById(marker.getSnippet());

                        Bundle bundle = new Bundle();
                        bundle.putSerializable(KEY_CHOSEN_USER_DATA, user);
                        fragment.setArguments(bundle);

                        fragment.show(manager, SHOW_FRAGMENT_DIALOG);

                    } catch (Exception e) {
                        Log.i(TAG, e.getMessage());
                    }

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

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                Log.i(TAG, (String) snapshot.child("longitude").getValue());
                Log.i(TAG, (String) snapshot.child("latitute").getValue());

            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.i(TAG, "Error - " + databaseError);
        }
    };

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        try {

            Log.i(TAG, "Begin Listening");

            if (error != null) {
                Log.i(TAG, "Error - " + error.getMessage());
            }

            if (value != null) {
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                        UserModel user = new UserModel(
                                documentChange.getDocument().getString(KEY_USER_UID),
                                documentChange.getDocument().getString(KEY_NAME),
                                documentChange.getDocument().getString(KEY_EMAIL),
                                documentChange.getDocument().getString(KEY_GENDER),
                                documentChange.getDocument().getString(KEY_MOBILE),
                                documentChange.getDocument().getString(KEY_PROFILE_IMG),
                                documentChange.getDocument().getString(KEY_DOB),
                                documentChange.getDocument().getString(KEY_FCM_TOKEN),
                                documentChange.getDocument().getLong(KEY_SCORE).intValue(),
                                documentChange.getDocument().getLong(KEY_BOOKMARKS).intValue(),
                                documentChange.getDocument().getLong(KEY_SCORE).doubleValue(),
                                documentChange.getDocument().getLong(KEY_BOOKMARKS).doubleValue()
                        );

                        Log.i(TAG, "Message added");

//                        TODO link to SnapShot
                        for (MarkerOptions markerOptions: markers) {
                            if (markerOptions.getSnippet().equals(user.getUid())) {
                                markerOptions.position(new LatLng(user.getLatitude(), user.getLongitude()));
                            }
                        }
                    }
                }

            }

        } catch (Exception e) {
            Log.i(TAG, "eventListener - " + e.getMessage());
        }
    };

}
