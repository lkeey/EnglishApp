package com.example.englishapp.data.database;

import static com.example.englishapp.data.database.Constants.KEY_AMOUNT_CATEGORIES;
import static com.example.englishapp.data.database.Constants.KEY_CATEGORY_ID;
import static com.example.englishapp.data.database.Constants.KEY_CATEGORY_NAME;
import static com.example.englishapp.data.database.Constants.KEY_CATEGORY_NUMBER_OF_TESTS;
import static com.example.englishapp.data.database.Constants.KEY_COLLECTION_CATEGORIES;
import static com.example.englishapp.data.database.Constants.KEY_COLLECTION_STATISTICS;
import static com.example.englishapp.data.database.DataBasePersonalData.DATA_FIRESTORE;

import android.util.ArrayMap;
import android.util.Log;

import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.data.models.CategoryModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataBaseCategories {

    private static final String TAG = "CategoriesDao";
    public static List<CategoryModel> LIST_OF_CATEGORIES = new ArrayList<>();
    public static String CHOSEN_CATEGORY_ID = null;
    public void createCategory(String name, CompleteListener listener) {
        Map<String, Object> categoryData = new ArrayMap<>();

        String randomID = null;

        while (true) {
            try {

                randomID = RandomStringUtils.random(20, true, true);

                Log.i(TAG, "random id - " + randomID);

                findCategoryById(randomID);

            } catch (Exception e) {
                Log.i(TAG, "not found category");

                break;
            }
        }

        categoryData.put(KEY_CATEGORY_ID, randomID);
        categoryData.put(KEY_CATEGORY_NAME, name);
        categoryData.put(KEY_CATEGORY_NUMBER_OF_TESTS, 0);

        WriteBatch batch = DATA_FIRESTORE.batch();

        DocumentReference categoryDocument = DATA_FIRESTORE
                .collection(KEY_COLLECTION_CATEGORIES)
                .document(randomID);

        batch.set(categoryDocument, categoryData, SetOptions.merge());

        // update statistics
        DocumentReference docReference = DATA_FIRESTORE
                .collection(KEY_COLLECTION_STATISTICS)
                .document(KEY_AMOUNT_CATEGORIES);

        batch.update(docReference, KEY_AMOUNT_CATEGORIES, FieldValue.increment(1));

        Log.i(TAG, "set category data");

        String randomId = randomID;
        batch.commit().addOnSuccessListener(unused -> {

            Log.i(TAG, "Category was successfully created");

            LIST_OF_CATEGORIES.add(new CategoryModel(
                    name,
                    randomId,
                    0
            ));

            listener.OnSuccess();

        }).addOnFailureListener(e -> {
            Log.i(TAG, "Can not create category - " + e.getMessage());

            listener.OnFailure();
        });
    }

    public void getListOfCategories(CompleteListener listener) {
        LIST_OF_CATEGORIES.clear();

        Log.i(TAG, "Begin loading categories");

        DATA_FIRESTORE.collection(KEY_COLLECTION_CATEGORIES)
            .limit(20)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                Log.i(TAG, "Get category");

                try {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        CategoryModel categoryModel = new CategoryModel();

                        categoryModel.setId(documentSnapshot.getString(KEY_CATEGORY_ID));
                        categoryModel.setName(documentSnapshot.getString(KEY_CATEGORY_NAME));
                        categoryModel.setNumberOfTests(documentSnapshot.getLong(KEY_CATEGORY_NUMBER_OF_TESTS).intValue());

                        LIST_OF_CATEGORIES.add(categoryModel);

                        Log.i(TAG, "Find - " + categoryModel.getName() + " - " + categoryModel.getId());

                    }
                } catch (Exception e) {
                    Log.i(TAG, "Category error - " + e.getMessage());
                }

                Log.i(TAG, "All good - " + LIST_OF_CATEGORIES.size());

                listener.OnSuccess();

            })
            .addOnFailureListener(e -> listener.OnFailure());
    }


    public CategoryModel findCategoryById(String categoryId) {

        return LIST_OF_CATEGORIES.stream().filter(category -> category.getId().equals(categoryId)).findAny()
                .orElseThrow(() -> new RuntimeException("not found"));
    }

}
