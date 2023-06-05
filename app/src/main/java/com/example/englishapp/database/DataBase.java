package com.example.englishapp.database;

import static com.example.englishapp.database.Constants.KEY_AMOUNT_CARDS;
import static com.example.englishapp.database.Constants.KEY_AMOUNT_CATEGORIES;
import static com.example.englishapp.database.Constants.KEY_AMOUNT_DISCUSSIONS;
import static com.example.englishapp.database.Constants.KEY_AMOUNT_OF_QUESTIONS;
import static com.example.englishapp.database.Constants.KEY_AMOUNT_SENT_MESSAGES;
import static com.example.englishapp.database.Constants.KEY_AMOUNT_TESTS;
import static com.example.englishapp.database.Constants.KEY_AMOUNT_WORDS;
import static com.example.englishapp.database.Constants.KEY_ANSWER;
import static com.example.englishapp.database.Constants.KEY_AUTHOR;
import static com.example.englishapp.database.Constants.KEY_BOOKMARKS;
import static com.example.englishapp.database.Constants.KEY_CARD_DESCRIPTION;
import static com.example.englishapp.database.Constants.KEY_CARD_ID;
import static com.example.englishapp.database.Constants.KEY_CARD_LEVEL;
import static com.example.englishapp.database.Constants.KEY_CARD_NAME;
import static com.example.englishapp.database.Constants.KEY_CATEGORY_ID;
import static com.example.englishapp.database.Constants.KEY_CATEGORY_NAME;
import static com.example.englishapp.database.Constants.KEY_CATEGORY_NUMBER_OF_TESTS;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_CARDS;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_CATEGORIES;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_CHAT;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_CONVERSATION;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_PERSONAL_DATA;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_QUESTIONS;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_STATISTICS;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_TESTS;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_USERS;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_WORDS;
import static com.example.englishapp.database.Constants.KEY_FCM_TOKEN;
import static com.example.englishapp.database.Constants.KEY_LOCATION;
import static com.example.englishapp.database.Constants.KEY_NUMBER_OF_OPTIONS;
import static com.example.englishapp.database.Constants.KEY_OPTION;
import static com.example.englishapp.database.Constants.KEY_PROFILE_IMG;
import static com.example.englishapp.database.Constants.KEY_QUESTION_ID;
import static com.example.englishapp.database.Constants.KEY_SCORE;
import static com.example.englishapp.database.Constants.KEY_TEST_ID;
import static com.example.englishapp.database.Constants.KEY_TEST_NAME;
import static com.example.englishapp.database.Constants.KEY_TEST_QUESTION;
import static com.example.englishapp.database.Constants.KEY_TEST_TIME;
import static com.example.englishapp.database.Constants.KEY_USER_SCORES;
import static com.example.englishapp.database.Constants.KEY_WORD_CARD_ID;
import static com.example.englishapp.database.Constants.KEY_WORD_DESCRIPTION;
import static com.example.englishapp.database.Constants.KEY_WORD_ID;
import static com.example.englishapp.database.Constants.KEY_WORD_IMG;
import static com.example.englishapp.database.Constants.KEY_WORD_LEVEL;
import static com.example.englishapp.database.Constants.KEY_WORD_TEXT_EN;
import static com.example.englishapp.database.Constants.NOT_VISITED;
import static com.example.englishapp.database.DataBasePersonalData.DATA_FIRESTORE;
import static com.example.englishapp.database.DataBasePersonalData.USER_MODEL;
import static com.example.englishapp.database.DataBaseUsers.LIST_OF_USERS;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.Log;

import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.models.CardModel;
import com.example.englishapp.models.CategoryModel;
import com.example.englishapp.models.OptionModel;
import com.example.englishapp.models.QuestionModel;
import com.example.englishapp.models.TestModel;
import com.example.englishapp.models.UserModel;
import com.example.englishapp.models.WordModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBase {
    private static final String TAG = "FirestoreDB";
    public static String CURRENT_CONVERSATION_ID = null;
    public static String CHOSEN_CATEGORY_ID = null;
    public static String CHOSEN_TEST_ID = null;
    public static List<CategoryModel> LIST_OF_CATEGORIES = new ArrayList<>();
    public static List<TestModel> LIST_OF_TESTS = new ArrayList<>();
    public static List<QuestionModel> LIST_OF_QUESTIONS = new ArrayList<>();
    public static List<String> LIST_OF_BOOKMARK_IDS = new ArrayList<>();
    public static List<QuestionModel> LIST_OF_BOOKMARKS = new ArrayList<>();
    public static List<CardModel> LIST_OF_CARDS = new ArrayList<>();
    public static List<String> LIST_OF_WORDS = new ArrayList<>();
    public static List<WordModel> LIST_OF_LEARNING_WORDS = new ArrayList<>();

    public void loadData(CompleteListener listener) {
        Log.i(TAG, "Load Data");

        DataBasePersonalData dataBasePersonalData = new DataBasePersonalData();

        dataBasePersonalData.getUserData(new CompleteListener() {
            @Override
            public void OnSuccess() {
                Log.i(TAG, "User data was loaded");
                DataBaseUsers.getListOfUsers(new CompleteListener() {
                    @Override
                    public void OnSuccess() {
                        Log.i(TAG, "Users were successfully loaded");
                        getListOfCategories(new CompleteListener() {
                            @Override
                            public void OnSuccess() {
                                Log.i(TAG, "Categories were successfully loaded");

                                loadBookmarkIds(new CompleteListener() {
                                    @Override
                                    public void OnSuccess() {
                                        Log.i(TAG, "bookmarked successfully loaded");
                                        listener.OnSuccess();
                                    }

                                    @Override
                                    public void OnFailure() {
                                        Log.i(TAG, "can not load bookmark ids");
                                        listener.OnFailure();
                                    }
                                });
                            }
                            @Override
                            public void OnFailure() {
                                Log.i(TAG, "Can not load categories");
                                listener.OnFailure();
                            }
                        });
                    }

                    @Override
                    public void OnFailure() {
                        Log.i(TAG, "Exception: User Data can not be loaded");
                        listener.OnFailure();
                    }
                });

            }
            @Override
            public void OnFailure() {
                Log.i(TAG, "Can not load users");
                listener.OnFailure();
            }
        });
    }

    public static void updateToken(String token, CompleteListener listener) {

        DocumentReference reference = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
                .document(USER_MODEL.getUid());

        reference.update(KEY_FCM_TOKEN, token)
                .addOnSuccessListener(unused -> listener.OnSuccess())
                .addOnFailureListener(e -> listener.OnFailure());
    }


    public static void updateImage(String path, CompleteListener listener) {

        Log.i(TAG, "Path - " + path);
        try {
            DocumentReference reference = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
                    .document(USER_MODEL.getUid());

            reference.update(KEY_PROFILE_IMG, path)
                    .addOnSuccessListener(unused -> {
                        Log.i(TAG, "Image was changed");

                        USER_MODEL.setPathToImage(path);
                        listener.OnSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.i(TAG, "Can not load image - " + e.getMessage());

                        listener.OnFailure();
                    });
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public static void getListOfCategories(CompleteListener listener) {
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

                        Log.i(TAG, "Created - " + categoryModel.getName() + " - " + categoryModel.getId());

                    }
                } catch (Exception e) {
                    Log.i(TAG, "Category error - " + e.getMessage());
                }

                Log.i(TAG, "All good");

                listener.OnSuccess();

            })
            .addOnFailureListener(e -> listener.OnFailure());
    }

    public static void createCategory(String name, CompleteListener listener) {
        Map<String, Object> categoryData = new ArrayMap<>();

//        String randomID = new Random()
//                .ints(0, 99)
//                .limit(20)
//                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
//                .toString();

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

    public static void updateProfileData(Map profileMap, CompleteListener listener) {

        WriteBatch batch = DATA_FIRESTORE.batch();

        DocumentReference reference = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
                .document(USER_MODEL.getUid());

        batch.update(reference, profileMap);

        batch.commit()
            .addOnSuccessListener(unused -> {
               listener.OnSuccess();
            })
            .addOnFailureListener(e -> {
                listener.OnFailure();
            });
    }

    public static void sendMessage(HashMap<String, Object> mapMsg, CompleteListener listener) {

        DATA_FIRESTORE.collection(KEY_COLLECTION_CHAT).add(mapMsg)
                .addOnSuccessListener(documentReference -> {

                    WriteBatch batch = DATA_FIRESTORE.batch();

                    DocumentReference docReference = DATA_FIRESTORE
                            .collection(KEY_COLLECTION_STATISTICS)
                            .document(KEY_AMOUNT_SENT_MESSAGES);

                    // increment amount of messages
                    batch.update(docReference, KEY_AMOUNT_SENT_MESSAGES, FieldValue.increment(1));

                    batch.commit()
                        .addOnSuccessListener(unused -> {
                            listener.OnSuccess();
                        })
                        .addOnFailureListener(e -> {
                            listener.OnFailure();
                        });

                })
                .addOnFailureListener(e -> listener.OnFailure());
    }

    public static void addConversation(HashMap<String, Object> conversation, CompleteListener listener) {
        DATA_FIRESTORE.collection(KEY_COLLECTION_CONVERSATION)
                .add(conversation)
                .addOnSuccessListener(documentReference -> {

                    CURRENT_CONVERSATION_ID = documentReference.getId();

                    WriteBatch batch = DATA_FIRESTORE.batch();

                    DocumentReference docReference = DATA_FIRESTORE
                            .collection(KEY_COLLECTION_STATISTICS)
                            .document(KEY_AMOUNT_DISCUSSIONS);

                    // increment amount of discussions
                    batch.update(docReference, KEY_AMOUNT_DISCUSSIONS, FieldValue.increment(1));

                    batch.commit()
                        .addOnSuccessListener(unused -> listener.OnSuccess())
                        .addOnFailureListener(e -> listener.OnFailure());
                });
    }

    public static UserModel findUserById(String userUID) {

        Log.i(TAG, "Amount users - " + LIST_OF_USERS.size());

        return LIST_OF_USERS.stream().filter(user -> user.getUid().equals(userUID)).findAny()
                .orElseThrow(() -> new RuntimeException("not found"));
    }

    public static void updateUserGeoPosition(double latitude, double longitude, CompleteListener listener) {

        if (latitude != USER_MODEL.getLatitude() && longitude != USER_MODEL.getLongitude()) {

            Log.i(TAG, "new geo - " + USER_MODEL.getUid());

            DocumentReference reference = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
                    .document(USER_MODEL.getUid());

            USER_MODEL.setLongitude(longitude);
            USER_MODEL.setLatitude(latitude);

            reference.update(KEY_LOCATION, new GeoPoint(latitude, longitude))
                .addOnSuccessListener(unused -> {

                    Log.i(TAG, "updated");

                    listener.OnSuccess();

                }).addOnFailureListener(e -> {

                    Log.i(TAG, "can not update user geo position - " + e.getMessage());

                    listener.OnFailure();

                    });

        } else {

            Log.i(TAG, "the same geo");

            listener.OnFailure();
        }
    }

    public static CategoryModel findCategoryById(String categoryId) {

        return LIST_OF_CATEGORIES.stream().filter(category -> category.getId().equals(categoryId)).findAny()
                .orElseThrow(() -> new RuntimeException("not found"));
    }

    public static void loadTestsData(CompleteListener listener) {
        LIST_OF_TESTS.clear();

        Log.i(TAG, "Begin loading tests");

        CategoryModel chosenCategory = findCategoryById(CHOSEN_CATEGORY_ID);

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
                        testModel.setAmountOfQuestion(documentSnapshot.getLong(KEY_AMOUNT_OF_QUESTIONS).intValue());
                        testModel.setTopScore(0);
                        testModel.setTime(documentSnapshot.getLong(KEY_TEST_TIME).intValue());
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

    public static void createTestData(ArrayList<QuestionModel> listOfQuestions, String name, int time, CompleteListener listener) {
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

            DocumentReference testDocument = DATA_FIRESTORE
                    .collection(KEY_COLLECTION_TESTS)
                    .document(randomID);

            batch.set(testDocument, testData, SetOptions.merge());

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

                createQuestionData(listOfQuestions, randomId, listener);

            }).addOnFailureListener(e -> {
                Log.i(TAG, "Can not create test - " + e.getMessage());

                listener.OnFailure();
            });
        } catch (Exception e) {
            Log.i(TAG, "error - " + e.getMessage());
        }
    }

    public static void createQuestionData(ArrayList<QuestionModel> listOfQuestions, String testID, CompleteListener listener) {

        WriteBatch batch = DATA_FIRESTORE.batch();

        for(int i=0; i < listOfQuestions.size(); i++) {

            Map<String, Object> questionData = new ArrayMap<>();

            QuestionModel questionModel = listOfQuestions.get(i);

            Log.i(TAG, "questionModel - " + questionModel.getQuestion());

            questionData.put(KEY_QUESTION_ID, CHOSEN_CATEGORY_ID + "_" + testID + "_" + i);
            questionData.put(KEY_TEST_QUESTION, questionModel.getQuestion());
            questionData.put(KEY_NUMBER_OF_OPTIONS, questionModel.getOptionsList().size());
            questionData.put(KEY_TEST_ID, testID);

            for (int n=0; n < questionModel.getOptionsList().size(); n++) {
                OptionModel optionModel = questionModel.getOptionsList().get(n);

                Log.i(TAG, "optionModel - " + optionModel.getOption());

                if (optionModel.isCorrect()) {
                    questionData.put(KEY_ANSWER, n);
                }

                questionData.put(KEY_OPTION + "_" + n, optionModel.getOption());

            }

            DocumentReference questionDocument = DATA_FIRESTORE
                    .collection(KEY_COLLECTION_QUESTIONS)
                    .document(CHOSEN_CATEGORY_ID + "_" + testID +"_" + i);

            batch.set(questionDocument, questionData, SetOptions.merge());

        }

        batch.commit().addOnSuccessListener(unused -> {
            Log.i(TAG, "Questions successfully added");
            listener.OnSuccess();
        }).addOnFailureListener(e -> {
            Log.i(TAG, "Fail to save test - " + e.getMessage());
            listener.OnFailure();
        });

    }

    public static TestModel findTestById(String testId) {

        return LIST_OF_TESTS.stream().filter(test -> test.getId().equals(testId)).findAny()
                .orElseThrow(() -> new RuntimeException("not found"));
    }

    public static void loadQuestions(CompleteListener listener) {
        LIST_OF_QUESTIONS.clear();

        DATA_FIRESTORE.collection(KEY_COLLECTION_QUESTIONS)
            .whereEqualTo(KEY_TEST_ID, CHOSEN_TEST_ID)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {

                Log.i(TAG, "Begin loading questions");

                for (DocumentSnapshot document: queryDocumentSnapshots) {

                    Log.i(TAG, document.getString(KEY_TEST_QUESTION));

                    // load options
                    ArrayList<OptionModel> optionModels = new ArrayList<>();

                    for(int i=0; i < (document.getLong(KEY_NUMBER_OF_OPTIONS).intValue()); i++) {
                        OptionModel optionModel = new OptionModel();

                        if (i == document.getLong(KEY_ANSWER).intValue()) {
                            optionModel.setCorrect(true);
                        } else {
                            optionModel.setCorrect(false);
                        }

                        optionModel.setOption(document.getString(KEY_OPTION + "_" + i));

                        optionModels.add(optionModel);

                    }

                    // load question
                    QuestionModel questionModel = new QuestionModel();

                    questionModel.setQuestion(document.getString(KEY_TEST_QUESTION));
                    questionModel.setId(document.getString(KEY_QUESTION_ID));
                    questionModel.setCorrectAnswer(document.getLong(KEY_ANSWER).intValue());
                    questionModel.setOptionsList(optionModels);
                    questionModel.setStatus(NOT_VISITED);
                    questionModel.setSelectedOption(-1);

                    // if bookmarked
                    if (LIST_OF_BOOKMARK_IDS.contains(questionModel.getId())) {
                        questionModel.setBookmarked(true);

                        Log.i(TAG, "bookmarked - " + questionModel.getQuestion());
                    }

                    LIST_OF_QUESTIONS.add(questionModel);

                }

                Log.i(TAG, "Questions successfully loaded");

                listener.OnSuccess();

            })
            .addOnFailureListener(e -> {
                Log.i(TAG, "Error was occurred while loading questions - " + e.getMessage());
                listener.OnFailure();
            });
    }

    public static void saveResult(int finalScore, CompleteListener listener) {
        WriteBatch batch = DATA_FIRESTORE.batch();

        DocumentReference userDocument = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS).document(USER_MODEL.getUid());

        Map<String, Object> bookmarksData = new ArrayMap<>();

        for (int i=0; i < LIST_OF_BOOKMARK_IDS.size(); i++) {
            bookmarksData.put("BOOKMARK" + i + "_ID", LIST_OF_BOOKMARK_IDS.get(i));
            Log.i(TAG, "bookMark - " + LIST_OF_BOOKMARK_IDS.get(i));
        }

        DocumentReference bookmarkDocument = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS).document(USER_MODEL.getUid())
                .collection(KEY_COLLECTION_PERSONAL_DATA)
                .document(KEY_BOOKMARKS);

        batch.set(bookmarkDocument, bookmarksData);

        userDocument.get()
            .addOnSuccessListener(documentSnapshot -> {
                Map<String, Object> userData = new ArrayMap<>();

                TestModel testModel = findTestById(CHOSEN_TEST_ID);

                if (finalScore > testModel.getTopScore()) {

                    int userExperience = documentSnapshot.getLong(KEY_SCORE).intValue();
                    int allScore = userExperience + finalScore - testModel.getTopScore();

                    Log.i(TAG, "user - " + userExperience + " - all score - " + allScore + " - top - " + testModel.getTopScore());

                    userData.put(KEY_SCORE, allScore);

                    DocumentReference scoreDocument = userDocument.collection(KEY_COLLECTION_PERSONAL_DATA).document(KEY_USER_SCORES);

                    Map<String, Object> testData = new ArrayMap<>();

                    testData.put(testModel.getId(), finalScore);
                    batch.set(scoreDocument, testData, SetOptions.merge());

                    USER_MODEL.setScore(allScore);

                }

                userData.put(KEY_BOOKMARKS, LIST_OF_BOOKMARK_IDS.size());

                batch.update(userDocument, userData);

                batch.commit()
                    .addOnSuccessListener(unused -> {

                        listener.OnSuccess();
                    })
                    .addOnFailureListener(e -> listener.OnFailure());
            })
            .addOnFailureListener(e -> listener.OnFailure());
    }

    public static void loadMyScores(CompleteListener listener) {
        Log.i(TAG, "load scores");

        DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
            .document(USER_MODEL.getUid())
            .collection(KEY_COLLECTION_PERSONAL_DATA)
            .document(KEY_USER_SCORES)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                Log.i(TAG, "amount tests - " + LIST_OF_TESTS.size());

                for (int i=0; i < LIST_OF_TESTS.size(); i++) {
                    int top = 0;

                    Log.i(TAG, "test id - " + LIST_OF_TESTS.get(i).getId() + " - " + LIST_OF_TESTS.get(i).getName());

                    if (documentSnapshot.getLong(LIST_OF_TESTS.get(i).getId()) != null) {
                        top = documentSnapshot.getLong(LIST_OF_TESTS.get(i).getId()).intValue();

                        Log.i(TAG, LIST_OF_TESTS.get(i).getName() + " - " + top);

                    }

                    Log.i(TAG, "top - " + top);

                    LIST_OF_TESTS.get(i).setTopScore(top);
                }
                listener.OnSuccess();
            })
            .addOnFailureListener(e -> {
                Log.i(TAG, "error while loading scores - " + e.getMessage());

                listener.OnFailure();
            });
    }

    public static void loadBookmarkIds(CompleteListener listener) {
        Log.i(TAG, "begin load bookmarks ids");

        LIST_OF_BOOKMARK_IDS.clear();

        DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
            .document(USER_MODEL.getUid())
            .collection(KEY_COLLECTION_PERSONAL_DATA)
            .document(KEY_BOOKMARKS)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                int count = USER_MODEL.getBookmarksCount();

                    Log.i(TAG, "amount bookmarks - " + count);

                    for(int i=0; i < count; i++) {

                        String questionId = documentSnapshot.getString("BOOKMARK" + i + "_ID");

                        LIST_OF_BOOKMARK_IDS.add(questionId);

                    }

                    Log.i(TAG, "found bookmarks - " + LIST_OF_BOOKMARKS.size());

                    listener.OnSuccess();

            })
            .addOnFailureListener(e -> {
                listener.OnFailure();
                Log.i(TAG, "error while loading bookmarks - " + e.getMessage());
            });
    }

    public static void loadBookmarks(CompleteListener listener) {
        Log.i(TAG, "begin load bookmarks");

        LIST_OF_BOOKMARKS.clear();

        for (int i=0; i < LIST_OF_BOOKMARK_IDS.size(); i++) {

            String questionId = LIST_OF_BOOKMARK_IDS.get(i);

            //load bookmarks from questions list
            int number = i;
            DATA_FIRESTORE.collection(KEY_COLLECTION_QUESTIONS)
                .document(questionId)
                .get()
                .addOnSuccessListener(document -> {

                    ArrayList<OptionModel> optionModels = new ArrayList<>();

                    // find options for question
                    for (int n = 0; n < (document.getLong(KEY_NUMBER_OF_OPTIONS).intValue()); n++) {
                        OptionModel optionModel = new OptionModel();

                        if (n == document.getLong(KEY_ANSWER).intValue()) {
                            optionModel.setCorrect(true);
                        } else {
                            optionModel.setCorrect(false);
                        }

                        optionModel.setOption(document.getString(KEY_OPTION + "_" + n));

                        optionModels.add(optionModel);

                    }

                    QuestionModel questionModel = new QuestionModel();

                    questionModel.setQuestion(document.getString(KEY_TEST_QUESTION));
                    questionModel.setId(document.getString(KEY_QUESTION_ID));
                    questionModel.setCorrectAnswer(document.getLong(KEY_ANSWER).intValue());
                    questionModel.setOptionsList(optionModels);
                    questionModel.setBookmarked(true);
                    questionModel.setStatus(NOT_VISITED);
                    questionModel.setSelectedOption(-1);

                    Log.i(TAG, "found - " + questionModel.getQuestion());

                    LIST_OF_BOOKMARKS.add(questionModel);

                    Log.i(TAG, "added - " + questionModel.getQuestion() + " - all - " + LIST_OF_BOOKMARKS.size());

                    if (number == LIST_OF_BOOKMARK_IDS.size() - 1) {
                        Log.i(TAG, "amount bookmarks - " + LIST_OF_BOOKMARKS.size());
                        listener.OnSuccess();
                    }

                })
                .addOnFailureListener(e -> {
                    Log.i(TAG, "can not find bookmark - " + e.getMessage());
                    listener.OnFailure();
                });
        }

    }

    public static void saveBookmarks(CompleteListener listener) {

        WriteBatch batch = DATA_FIRESTORE.batch();

        DocumentReference userDocument = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
                .document(USER_MODEL.getUid());

        Log.i(TAG, "amount saved bookmarks - " + LIST_OF_BOOKMARKS.size());

        Map<String, Object> bookmarksData = new ArrayMap<>();

        for (int i=0; i < LIST_OF_BOOKMARKS.size(); i++) {
            bookmarksData.put("BOOKMARK" + i + "_ID", LIST_OF_BOOKMARKS.get(i).getId());

            Log.i(TAG, "bookMark - " + LIST_OF_BOOKMARKS.get(i).getId() + " - " + LIST_OF_BOOKMARKS.get(i).getQuestion());
        }

        DocumentReference bookmarkDocument = DATA_FIRESTORE.collection(KEY_COLLECTION_USERS)
            .document(USER_MODEL.getUid())
            .collection(KEY_COLLECTION_PERSONAL_DATA)
            .document(KEY_BOOKMARKS);

        batch.set(bookmarkDocument, bookmarksData);

        userDocument.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> userData = new ArrayMap<>();

            userData.put(KEY_BOOKMARKS, LIST_OF_BOOKMARKS.size());

            batch.update(userDocument, userData);

            batch.commit()
                .addOnSuccessListener(unused -> listener.OnSuccess())
                .addOnFailureListener(e -> listener.OnFailure());
        })
        .addOnFailureListener(e -> listener.OnFailure());
    }

    public static void loadWordCardsData(CompleteListener listener) {
        LIST_OF_CARDS.clear();

        Log.i(TAG, "Begin loading cards");

        CategoryModel chosenCategory = findCategoryById(CHOSEN_CATEGORY_ID);

        DATA_FIRESTORE.collection(KEY_COLLECTION_CARDS)
            .limit(20)
            .whereEqualTo(KEY_CATEGORY_ID, chosenCategory.getId())
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                Log.i(TAG, "Get cards");

                try {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        CardModel cardModel = new CardModel();

                        cardModel.setId(documentSnapshot.getString(KEY_CARD_ID));
                        cardModel.setName(documentSnapshot.getString(KEY_CARD_NAME));
                        cardModel.setLevel(documentSnapshot.getString(KEY_CARD_LEVEL));
                        cardModel.setDescription(documentSnapshot.getString(KEY_CARD_DESCRIPTION));
                        cardModel.setAmountOfWords(documentSnapshot.getLong(KEY_AMOUNT_WORDS).intValue());
                        cardModel.setAuthor(documentSnapshot.getString(KEY_AUTHOR));

                        LIST_OF_CARDS.add(cardModel);

                        Log.i(TAG, "Find card - " + cardModel.getId());

                    }
                } catch (Exception e) {
                    Log.i(TAG, "Card error - " + e.getMessage());
                }

                Log.i(TAG, "All good");

                listener.OnSuccess();

            })
            .addOnFailureListener(e -> listener.OnFailure());

    }

    public static void createCardData(ArrayList<WordModel> listOfWords, String name, String description, String level, CompleteListener listener) {
        try {
            Map<String, Object> cardData = new ArrayMap<>();

            String randomID = null;

            while (true) {
                try {

                    randomID = RandomStringUtils.random(20, true, true);

                    Log.i(TAG, "random id - " + randomID);

                    findCardById(randomID);

                } catch (Exception e) {
                    Log.i(TAG, "not found card");

                    break;
                }
            }

            cardData.put(KEY_CARD_ID, randomID);
            cardData.put(KEY_CARD_NAME, name);
            cardData.put(KEY_CARD_LEVEL, level);
            cardData.put(KEY_AMOUNT_WORDS, listOfWords.size());
            cardData.put(KEY_CARD_DESCRIPTION, description);
            cardData.put(KEY_AUTHOR, USER_MODEL.getName());
            cardData.put(KEY_CATEGORY_ID, CHOSEN_CATEGORY_ID);

            Log.i(TAG, "set card data");

            WriteBatch batch = DATA_FIRESTORE.batch();

            DocumentReference testDocument = DATA_FIRESTORE
                    .collection(KEY_COLLECTION_CARDS)
                    .document(randomID);

            batch.set(testDocument, cardData, SetOptions.merge());

            Log.i(TAG, "set batch");

            // update amount of tests in category
            Log.i(TAG, "CHOSEN_CATEGORY_ID - " + CHOSEN_CATEGORY_ID);

            // update statistics
            DocumentReference docReference = DATA_FIRESTORE
                    .collection(KEY_COLLECTION_STATISTICS)
                    .document(KEY_AMOUNT_CARDS);

            batch.update(docReference, KEY_AMOUNT_CARDS, FieldValue.increment(1));

            docReference = DATA_FIRESTORE
                    .collection(KEY_COLLECTION_STATISTICS)
                    .document(KEY_AMOUNT_WORDS);

            Log.i(TAG, "Size words - " + listOfWords.size());
            batch.update(docReference, KEY_AMOUNT_WORDS, FieldValue.increment(listOfWords.size()));

            Log.i(TAG, "update statistics");

            String randomId = randomID;
            batch.commit().addOnSuccessListener(unused -> {

                LIST_OF_CARDS.add(new CardModel(
                        randomId,
                        name,
                        level,
                        description,
                        USER_MODEL.getName(),
                        listOfWords.size()
                ));

                Log.i(TAG, "Card was successfully created - " + name);

//                listener.OnSuccess();

                createWordsData(listOfWords, level, randomId, listener);

            }).addOnFailureListener(e -> {
                Log.i(TAG, "Can not create card - " + e.getMessage());

                listener.OnFailure();
            });

        } catch (Exception e) {
            Log.i(TAG, "error - " + e.getMessage());
        }
    }

    private static void createWordsData(ArrayList<WordModel> listOfWords, String level, String cardId, CompleteListener listener) {

        WriteBatch batch = DATA_FIRESTORE.batch();

        for(int i=0; i < listOfWords.size(); i++) {

            Map<String, Object> wordData = new ArrayMap<>();

            WordModel wordModel = listOfWords.get(i);

            Log.i(TAG, "wordModel - " + wordModel.getTextEn() + " - " + wordModel.getImage().toString());

            wordData.put(KEY_WORD_ID, CHOSEN_CATEGORY_ID + "_" + cardId + "_" + i);
            wordData.put(KEY_WORD_CARD_ID, cardId);
            wordData.put(KEY_WORD_TEXT_EN, wordModel.getTextEn());
            wordData.put(KEY_WORD_DESCRIPTION, wordModel.getDescription());
            wordData.put(KEY_WORD_LEVEL, level);
            wordData.put(KEY_WORD_IMG, wordModel.getImage());

            DocumentReference wordDocument = DATA_FIRESTORE
                    .collection(KEY_COLLECTION_WORDS)
                    .document(CHOSEN_CATEGORY_ID + "_" + cardId +"_" + i);

            batch.set(wordDocument, wordData, SetOptions.merge());

        }

        batch.commit().addOnSuccessListener(unused -> {

            Log.i(TAG, "Words were successfully added");

            listener.OnSuccess();

        }).addOnFailureListener(e -> {

            Log.i(TAG, "Fail to save words - " + e.getMessage());

            listener.OnFailure();

        });

    }

    public static CardModel findCardById(String cardId) {

        return LIST_OF_CARDS.stream().filter(card -> card.getId().equals(cardId)).findAny()
                .orElseThrow(() -> new RuntimeException("not found"));
    }

    public static void loadWords(CompleteListener listener) {
        DATA_FIRESTORE.collection(KEY_COLLECTION_WORDS)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {

                for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                    String str = documentSnapshot.getString(KEY_WORD_TEXT_EN);

                    Log.i(TAG, "found str - " + str);

                    LIST_OF_WORDS.add(str);
                }

                Log.i(TAG, "size - " + LIST_OF_WORDS.size());

                listener.OnSuccess();

            })
            .addOnFailureListener(e -> {

                Log.i(TAG, "error words - " + e.getMessage());

                listener.OnFailure();

            });
    }

    public static void loadWordsByCard(Context context, String cardId, CompleteListener listener) {

        Log.i(TAG, "card - " + cardId);

        DATA_FIRESTORE.collection(KEY_COLLECTION_WORDS)
            .limit(20)
//            .whereEqualTo(KEY_WORD_CARD_ID, cardId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {

                for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots) {

                    WordModel wordModel = new WordModel();
                    wordModel.setLevel(documentSnapshot.getString(KEY_WORD_LEVEL));
                    wordModel.setDescription(documentSnapshot.getString(KEY_WORD_DESCRIPTION));
                    wordModel.setTextEn(documentSnapshot.getString(KEY_WORD_TEXT_EN));

                    // TODO load bitmap

                    wordModel.setImage(documentSnapshot.getString(KEY_WORD_TEXT_EN));

                    Log.i(TAG, "found word - " + wordModel.getTextEn());

                    LIST_OF_LEARNING_WORDS.add(wordModel);
                }

                Log.i(TAG, "size - " + LIST_OF_LEARNING_WORDS.size());

                listener.OnSuccess();

            })
            .addOnFailureListener(e -> {

                Log.i(TAG, "error words learning - " + e.getMessage());

                listener.OnFailure();

            });
}
    public static String bitMapToString(Bitmap bitmap){

        ByteArrayOutputStream bmp = new  ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG,100, bmp);

        byte [] b = bmp.toByteArray();

        String temp = Base64.encodeToString(b, Base64.DEFAULT);

        return temp;
    }

    public static void loadLearningWords(Context context, CompleteListener listener) {

        Log.i(TAG, "load words");

        LIST_OF_LEARNING_WORDS.clear();

        new Thread(() -> {
            try {

                LIST_OF_LEARNING_WORDS =
                        RoomDataBase.getDatabase(context)
                                .roomDao()
                                .getAllWords();

                listener.OnSuccess();

            } catch (Exception e) {
                Log.i(TAG, "can not load learning words - " + e.getMessage());

                listener.OnFailure();
            }
        });
    }
}
