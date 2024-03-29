package com.example.englishapp.domain.services;

import static com.example.englishapp.data.database.Constants.KEY_COLLECTION_USERS;
import static com.example.englishapp.data.database.Constants.KEY_LOCATION;
import static com.example.englishapp.data.database.Constants.KEY_NAME;
import static com.example.englishapp.data.database.Constants.KEY_USER_UID;
import static com.example.englishapp.data.database.DataBasePersonalData.DATA_FIRESTORE;
import static com.example.englishapp.data.database.DataBaseUsers.LIST_OF_USERS;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.englishapp.data.database.DataBasePersonalData;
import com.example.englishapp.data.database.DataBaseUsers;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.domain.interfaces.UserListener;
import com.example.englishapp.data.models.UserModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapService implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private static final String TAG = "MapService";
    private final Context context;
    private final UserListener listener;
    private static List<MarkerOptions> markers;
    private final DataBaseUsers dataBaseUsers;

    public MapService(UserListener listener, Context context) {
        this.listener = listener;
        this.context = context;
        markers = new ArrayList<>();
        this.dataBaseUsers = new DataBaseUsers();
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

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                DataBasePersonalData.USER_MODEL.getLatitude(),
                DataBasePersonalData.USER_MODEL.getLongitude()
        ), 5));

        Log.i(TAG, "ready");

        listenMarkers();

        try {

            Log.i(TAG, "size - " + markers.size());

            dataBaseUsers.getListOfUsers(new CompleteListener() {
                @Override
                public void OnSuccess() {
                    for(UserModel user: LIST_OF_USERS) {
                        MarkerOptions marker = new MarkerOptions()
                                .position(new LatLng(
                                    user.getLatitude(),
                                    user.getLongitude()

                                ))
                                .title(user.getName())
                                .snippet(user.getUid())
                                .flat(true);

                        if (user.getLongitude() != 0 && user.getLatitude() != 0) {

                            Log.i(TAG, "added2 - " + marker.getTitle());

                            googleMap.addMarker(marker);

                            markers.add(marker);
                        }
                    }
                }

                @Override
                public void OnFailure() {
                    Log.i(TAG, "OnFailure: ");
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }


        googleMap.setOnMarkerClickListener(marker -> {
            try {

                UserModel user = new DataBaseUsers().findUserById(marker.getSnippet());

                listener.onUserClicked(user);

            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }

            return false;
        });

    }

    private void listenMarkers() {
        DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
                .addSnapshotListener(eventListener);
    }


    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        try {

            Log.i(TAG, "Begin Listening");

            if (error != null) {
                Log.i(TAG, "Error - " + error.getMessage());
            }

            if (value != null) {
                for (DocumentChange documentChange : value.getDocumentChanges()) {

                    // update name or location
                    if (documentChange.getType() == DocumentChange.Type.MODIFIED) {

                        try {
                            MarkerOptions marker = findMarkerByUserId(documentChange.getDocument().getString(KEY_USER_UID));

                            marker.position(new LatLng(
                                    Objects.requireNonNull(documentChange.getDocument().getGeoPoint(KEY_LOCATION)).getLatitude(),
                                    Objects.requireNonNull(documentChange.getDocument().getGeoPoint(KEY_LOCATION)).getLongitude()
                            ));

                            marker.title(documentChange.getDocument().getString(KEY_NAME));

                            Log.i(TAG, "modified " + marker.getTitle());

                        } catch (Exception e) {
                            Log.i(TAG, "err - " + e.getMessage());
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }

                        // create new marker if new user
                    } else if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        MarkerOptions marker = new MarkerOptions()
                                .position(new LatLng(
                                        Objects.requireNonNull(documentChange.getDocument().getGeoPoint(KEY_LOCATION)).getLatitude(),
                                        Objects.requireNonNull(documentChange.getDocument().getGeoPoint(KEY_LOCATION)).getLongitude()

                                ))
                                .title(documentChange.getDocument().getString(KEY_NAME))
                                .snippet(documentChange.getDocument().getString(KEY_USER_UID))
                                .flat(true);

                        Log.i(TAG, "added2 - " + marker.getTitle());

                        markers.add(marker);
                    }
                }

            }

        } catch (Exception e) {
            Log.i(TAG, "eventListener err - " + e.getMessage());
        }
    };

    private MarkerOptions findMarkerByUserId(String userUID) {

        Log.i(TAG, "Amount markers - " + markers.size());

        return markers.stream().filter(marker -> Objects.equals(marker.getSnippet(), userUID)).findAny()
                .orElseThrow(() -> new RuntimeException("not found"));
    }
}
