package com.example.englishapp.MVP;

import android.util.ArrayMap;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.Map;

public class DataBase {

    private static final String TAG = "FirestoreDB";

    public static FirebaseFirestore DATA_FIRESTORE;
    public static FirebaseFirestore FIRESTORE;

    public static final String USER_COLLECTION = "USERS";
    public static final String TOTAL_OF_USERS = "TOTAL_USERS";

    public static void createUserData(String email, String name, CompleteListener listener) {
        Map<String, Object> userData = new ArrayMap<>();
        userData.put("EMAIL_ID", email);
        userData.put("NAME", name);
        userData.put("TOTAL_SCORE", 0);
        userData.put("BOOKMARKS", 0);

        DocumentReference userDoc = FIRESTORE.collection(USER_COLLECTION).document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        WriteBatch batch = FIRESTORE.batch();

        batch.set(userDoc, userData);

        DocumentReference docReference = FIRESTORE.collection(USER_COLLECTION).document(TOTAL_OF_USERS);
        batch.update(docReference, "COUNT", FieldValue.increment(1));

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.OnSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.OnFailure();
                    }
                });
    }

    public static void loadData(CompleteListener listener) {
        listener.OnSuccess();
    }
}
