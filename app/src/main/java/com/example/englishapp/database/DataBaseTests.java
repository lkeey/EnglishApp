package com.example.englishapp.database;

import static com.example.englishapp.database.Constants.KEY_AMOUNT_OF_QUESTIONS;
import static com.example.englishapp.database.Constants.KEY_AMOUNT_TESTS;
import static com.example.englishapp.database.Constants.KEY_AUTHOR;
import static com.example.englishapp.database.Constants.KEY_CATEGORY_ID;
import static com.example.englishapp.database.Constants.KEY_CATEGORY_NUMBER_OF_TESTS;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_CATEGORIES;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_STATISTICS;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_TESTS;
import static com.example.englishapp.database.Constants.KEY_TEST_ID;
import static com.example.englishapp.database.Constants.KEY_TEST_NAME;
import static com.example.englishapp.database.Constants.KEY_TEST_TIME;
import static com.example.englishapp.database.DataBaseCategories.CHOSEN_CATEGORY_ID;
import static com.example.englishapp.database.DataBasePersonalData.DATA_FIRESTORE;
import static com.example.englishapp.database.DataBasePersonalData.USER_MODEL;

import android.util.ArrayMap;
import android.util.Log;

import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.models.CategoryModel;
import com.example.englishapp.models.QuestionModel;
import com.example.englishapp.models.TestModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataBaseTests {

    private static final String TAG = "TestsDao";
    public static List<TestModel> LIST_OF_TESTS = new ArrayList<>();
    public static String CHOSEN_TEST_ID = null;

    public void loadTestsData(CompleteListener listener) {
        LIST_OF_TESTS.clear();

        Log.i(TAG, "Begin loading tests");

        CategoryModel chosenCategory = new DataBaseCategories().findCategoryById(CHOSEN_CATEGORY_ID);

        DATA_FIRESTORE.collection(KEY_COLLECTION_TESTS)
            .limit(20)
            .whereEqualTo(KEY_CATEGORY_ID, chosenCategory.getId())
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                Log.i(TAG, "Get tests");

                try {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        TestModel testModel = new TestModel();

                        testModel.setId(documentSnapshot.getString(KEY_TEST_ID));
                        testModel.setName(documentSnapshot.getString(KEY_TEST_NAME));
                        testModel.setAmountOfQuestion(Objects.requireNonNull(documentSnapshot.getLong(KEY_AMOUNT_OF_QUESTIONS)).intValue());
                        testModel.setTopScore(0);
                        testModel.setTime(Objects.requireNonNull(documentSnapshot.getLong(KEY_TEST_TIME)).intValue());
                        testModel.setAuthor(documentSnapshot.getString(KEY_AUTHOR));

                        LIST_OF_TESTS.add(testModel);

                        Log.i(TAG, "Created - " + testModel.getId());

                    }
                } catch (Exception e) {
                    Log.i(TAG, "Test error - " + e.getMessage());
                }

                Log.i(TAG, "All good");

                listener.OnSuccess();

            })
            .addOnFailureListener(e -> listener.OnFailure());

    }

    public void createTestData(ArrayList<QuestionModel> listOfQuestions, String name, int time, CompleteListener listener) {
        try {
            Map<String, Object> testData = new ArrayMap<>();

            String randomID = null;

            while (true) {
                try {

                    randomID = RandomStringUtils.random(20, true, true);

                    Log.i(TAG, "random id - " + randomID);

                    findTestById(randomID);

                } catch (Exception e) {
                    Log.i(TAG, "not found test");

                    break;
                }
            }

            testData.put(KEY_TEST_ID, randomID);
            testData.put(KEY_TEST_NAME, name);
            testData.put(KEY_TEST_TIME, time);
            testData.put(KEY_AMOUNT_OF_QUESTIONS, listOfQuestions.size());
            testData.put(KEY_AUTHOR, USER_MODEL.getName());
            testData.put(KEY_CATEGORY_ID, CHOSEN_CATEGORY_ID);

            Log.i(TAG, "set test data");

            WriteBatch batch = DATA_FIRESTORE.batch();

            DocumentReference testDocument = null;
            if (randomID != null) {
                testDocument = DATA_FIRESTORE
                        .collection(KEY_COLLECTION_TESTS)
                        .document(randomID);
            }

            if (testDocument != null) {
                batch.set(testDocument, testData, SetOptions.merge());
            }

            Log.i(TAG, "set batch");

            // update amount of tests in category
            Log.i(TAG, "CHOSEN_CATEGORY_ID - " + CHOSEN_CATEGORY_ID);

            DocumentReference docCategory = DATA_FIRESTORE
                    .collection(KEY_COLLECTION_CATEGORIES)
                    .document(CHOSEN_CATEGORY_ID);

            batch.update(docCategory, KEY_CATEGORY_NUMBER_OF_TESTS, FieldValue.increment(1));

            Log.i(TAG, "update amount");

            // update statistics
            DocumentReference docReference = DATA_FIRESTORE
                    .collection(KEY_COLLECTION_STATISTICS)
                    .document(KEY_AMOUNT_TESTS);

            batch.update(docReference, KEY_AMOUNT_TESTS, FieldValue.increment(1));

            docReference = DATA_FIRESTORE
                    .collection(KEY_COLLECTION_STATISTICS)
                    .document(KEY_AMOUNT_OF_QUESTIONS);

            Log.i(TAG, "Size questions - " + listOfQuestions.size());
            batch.update(docReference, KEY_AMOUNT_OF_QUESTIONS, FieldValue.increment(listOfQuestions.size()));

            Log.i(TAG, "update statistics");

            String randomId = randomID;
            batch.commit().addOnSuccessListener(unused -> {

                Log.i(TAG, "Test was successfully created");

                LIST_OF_TESTS.add(new TestModel(
                        randomId,
                        name,
                        USER_MODEL.getName(),
                        listOfQuestions.size(),
                        0,
                        time
                ));

                new DataBaseQuestions().createQuestionData(listOfQuestions, randomId, listener);

            }).addOnFailureListener(e -> {
                Log.i(TAG, "Can not create test - " + e.getMessage());

                listener.OnFailure();
            });
        } catch (Exception e) {
            Log.i(TAG, "error - " + e.getMessage());
        }
    }

    public TestModel findTestById(String testId) {

        return LIST_OF_TESTS.stream().filter(test -> test.getId().equals(testId)).findAny()
                .orElseThrow(() -> new RuntimeException("not found"));
    }

}
