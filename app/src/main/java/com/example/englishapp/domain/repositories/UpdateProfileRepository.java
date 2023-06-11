package com.example.englishapp.domain.repositories;

import static com.example.englishapp.data.database.Constants.KEY_DOB;
import static com.example.englishapp.data.database.Constants.KEY_EMAIL;
import static com.example.englishapp.data.database.Constants.KEY_GENDER;
import static com.example.englishapp.data.database.Constants.KEY_LANGUAGE_CODE;
import static com.example.englishapp.data.database.Constants.KEY_NAME;
import static com.example.englishapp.data.database.Constants.KEY_SCORE;
import static com.example.englishapp.data.database.Constants.NAME_USER_PROFILE_IMG;
import static com.example.englishapp.data.database.Constants.PATH_PROFILE_IMG;
import static com.example.englishapp.data.database.DataBasePersonalData.USER_MODEL;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.ArrayMap;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.example.englishapp.data.database.DataBase;
import com.example.englishapp.data.database.DataBasePersonalData;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;
import java.util.Objects;

public class UpdateProfileRepository {

    private static final String TAG = "ProfileUpdate";
    private StorageReference storageReference;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private String pathToImage;
    private DataBasePersonalData dataBasePersonalData;
    private DataBase dataBase;

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

    public void updateUser(String textEmail, String textName, String textDOB, String textGender, String langCode, boolean isAddingScore, Uri imgUri, Context context, CompleteListener listener)  {

        dataBase = new DataBase();
        dataBasePersonalData = new DataBasePersonalData();

        Map<String, Object> userData = new ArrayMap<>();

        userData.put(KEY_EMAIL, textEmail);
        userData.put(KEY_NAME, textName);
        userData.put(KEY_DOB, textDOB);
        userData.put(KEY_GENDER, textGender);
        userData.put(KEY_LANGUAGE_CODE, langCode);

        if(isAddingScore) {
            userData.put(KEY_SCORE, USER_MODEL.getScore() + 50);
        }

        dataBasePersonalData.updateProfileData(userData, new CompleteListener() {
            @Override
            public void OnSuccess() {
                Log.i(TAG, "personal data updated");

                dataBase.loadData(new CompleteListener() {
                    @Override
                    public void OnSuccess() {
                        Log.i(TAG, "data loaded");

                        if(imgUri != null) {
                            Log.i(TAG, "HAVE IMAGE");

                            uploadPicture(imgUri, context, new CompleteListener() {
                                @Override
                                public void OnSuccess() {
                                    Log.i(TAG, "upload image");

                                    listener.OnSuccess();
                                }

                                @Override
                                public void OnFailure() {
                                    Log.i(TAG, "fail upload image");

                                    listener.OnFailure();
                                }
                            });
                        } else {

                            Log.i(TAG, "success without img");

                            listener.OnSuccess();

                        }
                    }

                    @Override
                    public void OnFailure() {
                        Log.i(TAG, "can not load data");

                        listener.OnFailure();
                    }
                });
            }
            @Override
            public void OnFailure() {
                Log.i(TAG, "can not update personal data");

                listener.OnFailure();
            }
        });
    }


    private String getFileExtension(Uri uriImage, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uriImage));
    }

}
