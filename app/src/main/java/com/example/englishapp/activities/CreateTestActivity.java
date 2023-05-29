package com.example.englishapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.englishapp.models.ComplexTest;
import com.example.englishapp.ModelTest;
import com.example.englishapp.Option;
import com.example.englishapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CreateTestActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private List<ModelTest> testsList;
    private RecyclerView TestsRecyclerView;

    private void createModelTest(String question, List<Option> options) {

        ModelTest modelTest = new ModelTest(
                question, options
        );

        testsList.add(modelTest);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tests");

    }

    private void createTest() {

        String userID = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");

        ComplexTest complexTest = new ComplexTest(
              testsList
        );

        databaseReference.child(userID).setValue(complexTest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(CreateTestActivity.this, "Tests added!!!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(CreateTestActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(CreateTestActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_test);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        TestsRecyclerView = findViewById(R.id.notesRecyclerView);
        TestsRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1,
                        StaggeredGridLayoutManager.VERTICAL
                )
        );

    }
}