package com.example.englishapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadProfilePictureActivity extends AppCompatActivity {
    private Button btnUploadPicture, btnChoosePicture;
    private ImageView imageProfile;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private Uri uriImage;
    private static final int PICK_IMAGE_REQUEST = 1;

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uriImage) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uriImage));
    }

    private void uploadPicture() {
        if (uriImage != null) {
            //Save image
            StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid() + "." + getFileExtension(uriImage));
            //Toast.makeText(UploadProfilePictureActivity.this, fileReference.toString(), Toast.LENGTH_SHORT).show();

            //Upload image to Storage
            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            firebaseUser = authProfile.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(profileUpdates);
                        }
                    });

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UploadProfilePictureActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(UploadProfilePictureActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UploadProfilePictureActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(UploadProfilePictureActivity.this, "File was not selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Toast.makeText(UploadProfilePictureActivity.this, "Image", Toast.LENGTH_SHORT).show();

            uriImage = data.getData();
            imageProfile.setImageURI(uriImage);
        } else {
//            Toast.makeText(UploadProfilePictureActivity.this, "NO", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_picture);

        btnChoosePicture = findViewById(R.id.btnChoosePicture);
        btnUploadPicture = findViewById(R.id.btnUploadPicture);
        progressBar = findViewById(R.id.progressBar);
        imageProfile = findViewById(R.id.imageProfile);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("Display Pictures");
        Uri uri = firebaseUser.getPhotoUrl();

        //Set User's current DP in ImageView
        Picasso.get().load(uri).into(imageProfile);

        btnChoosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        btnUploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                uploadPicture();
            }
        });
    }

    //ActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate Menu
        getMenuInflater().inflate(R.menu.common_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Selected item
        int id = item.getItemId();

        if (id == R.id.menuRefresh) {
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
        } else if (id == R.id.menuUpdateProfile) {
            Intent intent = new Intent(UploadProfilePictureActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menuUpdateEmail) {
            Intent intent = new Intent(UploadProfilePictureActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
//        } else if (id == R.id.menuSettings) {
//            Intent intent = new Intent(ProfileActivity.this, ActivitySettings.class);
//            startActivity(intent);
//            finish();
//        } else if (id == R.id.menuChangePassword) {
//            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
//            startActivity(intent);
//            finish();
//        } else if (id == R.id.menuDeleteAccount) {
//            Intent intent = new Intent(ProfileActivity.this, DeleteProfileActivity.class);
//            startActivity(intent);
//            finish();
        } else if (id == R.id.menuLogOut) {
            authProfile.signOut();
            Toast.makeText(UploadProfilePictureActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UploadProfilePictureActivity.this, GreetingActivity.class);

            //Clear stack
            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
