package com.example.englishapp.repositories;

import static com.example.englishapp.database.Constants.NAME_USER_PROFILE_IMG;
import static com.example.englishapp.database.Constants.PATH_PROFILE_IMG;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.example.englishapp.database.DataBasePersonalData;
import com.example.englishapp.interfaces.CompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class UpdateProfileRepository {

    private static final String TAG = "ProfileUpdate";
    private StorageReference storageReference;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private String pathToImage;
    private DataBasePersonalData dataBasePersonalData;

    public UpdateProfileRepository() {}

    public void uploadPicture(Uri uriImg, Context context, CompleteListener listener) {

        storageReference = FirebaseStorage.getInstance().getReference(PATH_PROFILE_IMG);
        authProfile = FirebaseAuth.getInstance();
        dataBasePersonalData = new DataBasePersonalData();

        Log.i(TAG, "Create fileReference");

        Log.i(TAG, "UID - " + Objects.requireNonNull(authProfile.getCurrentUser()).getUid());

        Log.i(TAG, "EXTENSION - " + getFileExtension(uriImg, context));

        //Save image
        StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid() + "/" + NAME_USER_PROFILE_IMG + "." + getFileExtension(uriImg, context));

        Log.i(TAG, fileReference.toString());

        //Upload image to Storage
        fileReference.putFile(uriImg).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.i(TAG, "PATH" + uri.toString());

            firebaseUser = authProfile.getCurrentUser();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(uri).build();

            firebaseUser.updateProfile(profileUpdates);

            pathToImage = uri.toString();

            Log.i(TAG, "PATH01 - " + pathToImage);

            dataBasePersonalData.updateImage(pathToImage, new CompleteListener() {
                @Override
                public void OnSuccess() {
                    Log.i(TAG, "Photo successfully saved");

                    listener.OnSuccess();
                }

                @Override
                public void OnFailure() {
                    Log.i(TAG, "Unable to save photo");

                    listener.OnFailure();
                }
            });

        })).addOnFailureListener(e -> listener.OnFailure());
    }

    private String getFileExtension(Uri uriImage, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uriImage));
    }

}
