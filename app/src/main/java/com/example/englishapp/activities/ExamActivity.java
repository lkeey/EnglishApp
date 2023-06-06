package com.example.englishapp.activities;

import static com.example.englishapp.database.Constants.ANSWERED;
import static com.example.englishapp.database.Constants.KEY_TEST_TIME;
import static com.example.englishapp.database.Constants.NOT_VISITED;
import static com.example.englishapp.database.Constants.REVIEW;
import static com.example.englishapp.database.Constants.SHOW_FRAGMENT_DIALOG;
import static com.example.englishapp.database.Constants.UNANSWERED;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.englishapp.R;
import com.example.englishapp.adapters.QuestionsAdapter;
import com.example.englishapp.database.DataBaseQuestions;
import com.example.englishapp.database.DataBaseTests;
import com.example.englishapp.fragments.ExamInfoFragment;
import com.example.englishapp.models.QuestionModel;
import com.example.englishapp.models.TestModel;

import java.util.concurrent.TimeUnit;

public class ExamActivity extends BaseActivity {
    private static final String TAG = "ActivityExam";
    private QuestionsAdapter questionsAdapter;
    private ExamInfoFragment fragment;
    private TestModel testModel;
    private static RecyclerView recyclerQuestions;
    private TextView questionNumber, amountTime, testName;
    private Button btnSubmit, btnContinue, btnExit, btnCancel, btnComplete;
    private ImageView questionList, bookMarkImg, previousQuestion, nextQuestion;
    private CountDownTimer timer;
    private long timeCounter;
    int numberOfQuestion;
    private Dialog dialogExit, dialogComplete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        init();

        setListeners();

        setSnapHelper();

        startTimer();

    }

    private void setListeners() {

        btnContinue.setOnClickListener(v -> dialogExit.dismiss());

        btnExit.setOnClickListener(v -> {
            Intent intent = new Intent(ExamActivity.this, MainActivity.class);
            startActivity(intent);

            ExamActivity.this.finish();

            dialogExit.dismiss();
        });

        previousQuestion.setOnClickListener(v -> {
            if (numberOfQuestion > 0) {
                recyclerQuestions.smoothScrollToPosition(numberOfQuestion - 1);
            }
        });

        nextQuestion.setOnClickListener(v -> {
            if (numberOfQuestion < testModel.getAmountOfQuestion()) {
                recyclerQuestions.smoothScrollToPosition(numberOfQuestion + 1);
            }
        });

        questionList.setOnClickListener(v -> {
            fragment = new ExamInfoFragment();

            fragment.show(getSupportFragmentManager(), SHOW_FRAGMENT_DIALOG);
        });

        bookMarkImg.setOnClickListener(v -> addToBookmark());

        btnSubmit.setOnClickListener(v -> dialogComplete.show());

        btnCancel.setOnClickListener(v -> dialogComplete.dismiss());

        btnComplete.setOnClickListener(v -> {
            timer.cancel();
            timer.onFinish();
            dialogComplete.dismiss();
        });
    }

    private void addToBookmark() {

        QuestionModel questionModel = DataBaseQuestions.LIST_OF_QUESTIONS.get(numberOfQuestion);

        if (questionModel.isBookmarked()) {

            Log.i(TAG, "Already bookmark");

            DataBaseQuestions.LIST_OF_QUESTIONS.get(numberOfQuestion).setBookmarked(false);

            bookMarkImg.setColorFilter(ContextCompat.getColor(ExamActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

            if (questionModel.getSelectedOption() != -1) {
                Log.i(TAG, "New status - ANSWERED");

                DataBaseQuestions.LIST_OF_QUESTIONS.get(numberOfQuestion).setStatus(ANSWERED);

            } else {
                Log.i(TAG, "New status - UNANSWERED");

                DataBaseQuestions.LIST_OF_QUESTIONS.get(numberOfQuestion).setStatus(UNANSWERED);
            }

        } else {

            Log.i(TAG, "New bookmark");

            DataBaseQuestions.LIST_OF_QUESTIONS.get(numberOfQuestion).setBookmarked(true);

            DataBaseQuestions.LIST_OF_QUESTIONS.get(numberOfQuestion).setStatus(REVIEW);

            bookMarkImg.setColorFilter(ContextCompat.getColor(ExamActivity.this, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    private void setSnapHelper() {
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerQuestions);

        recyclerQuestions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View view = snapHelper.findSnapView(recyclerView.getLayoutManager());
                numberOfQuestion = recyclerView.getLayoutManager().getPosition(view);

                QuestionModel questionModel = DataBaseQuestions.LIST_OF_QUESTIONS.get(numberOfQuestion);

                // if user did not answer
                if (questionModel.getStatus() == NOT_VISITED) {
                    DataBaseQuestions.LIST_OF_QUESTIONS.get(numberOfQuestion).setStatus(UNANSWERED);
                }

                // if question was bookmarked
                if (questionModel.isBookmarked()) {
                    bookMarkImg.setColorFilter(ContextCompat.getColor(ExamActivity.this, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    bookMarkImg.setColorFilter(ContextCompat.getColor(ExamActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                }

                questionNumber.setText((numberOfQuestion + 1) + "/" + testModel.getAmountOfQuestion());
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void init() {
        recyclerQuestions = findViewById(R.id.recyclerQuestions);
        questionNumber = findViewById(R.id.questionNumber);
        amountTime = findViewById(R.id.amountTime);
        testName = findViewById(R.id.testName);
        btnSubmit = findViewById(R.id.btnSubmit);
        questionList = findViewById(R.id.questionList);
        bookMarkImg = findViewById(R.id.bookMarkImg);
        previousQuestion = findViewById(R.id.previousQuestion);
        nextQuestion = findViewById(R.id.nextQuestion);

        dialogExit = new Dialog(ExamActivity.this);
        dialogExit.setContentView(R.layout.dialog_exit);
        dialogExit.setCancelable(false);
        dialogExit.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogExit.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnContinue = dialogExit.findViewById(R.id.btnContinue);
        btnExit = dialogExit.findViewById(R.id.btnExit);

        dialogComplete = new Dialog(ExamActivity.this);
        dialogComplete.setContentView(R.layout.dialog_submit_layout);
        dialogComplete.setCancelable(false);
        dialogComplete.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogComplete.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnCancel = dialogComplete.findViewById(R.id.btnCancel);
        btnComplete = dialogComplete.findViewById(R.id.btnComplete);

        numberOfQuestion = 0;

        testModel = new DataBaseTests().findTestById(DataBaseTests.CHOSEN_TEST_ID);

        questionNumber.setText("1 / " + DataBaseQuestions.LIST_OF_QUESTIONS.size());
        testName.setText(testModel.getName());
        amountTime.setText(testModel.getTime() + " minutes");

        questionsAdapter = new QuestionsAdapter(DataBaseQuestions.LIST_OF_QUESTIONS, ExamActivity.this, false);
        recyclerQuestions.setAdapter(questionsAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(ExamActivity.this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerQuestions.setLayoutManager(manager);

        // if question was bookmarked
        if (DataBaseQuestions.LIST_OF_QUESTIONS.get(0).isBookmarked()) {
            bookMarkImg.setColorFilter(ContextCompat.getColor(ExamActivity.this, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            bookMarkImg.setColorFilter(ContextCompat.getColor(ExamActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        }

    }

    private void startTimer() {
        long totalTime = testModel.getTime()*60*1_000;
        timer = new CountDownTimer(totalTime, 1_000) {
            @Override
            public void onTick(long remainingTime) {
                timeCounter = remainingTime;

                String time = String.format(
                        "%02d : %02d minutes",
                        TimeUnit.MILLISECONDS.toMinutes(remainingTime),
                        TimeUnit.MILLISECONDS.toSeconds(remainingTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingTime))
                );

                amountTime.setText(time);
            }

            @Override
            public void onFinish() {
//                Intent intent = new Intent(QuestionsActivity.this, ScoreActivity.class);
//
//                long totalTime = DbQuery.testModelList.get(DbQuery.selectedTestIndex).getTime()*60*1_000;
//                intent.putExtra("TIME_TAKEN", totalTime - timeCounter);
//
//                startActivity(intent);
                try {
                    Toast.makeText(ExamActivity.this, "Finished", Toast.LENGTH_SHORT).show();

                    Log.i(TAG, "totalTime - " + totalTime + " - timeCounter - " + timeCounter);

                    Intent intent = new Intent(ExamActivity.this, MainActivity.class);
                    intent.putExtra(KEY_TEST_TIME, totalTime - timeCounter);

                    startActivity(intent);

                    ExamActivity.this.finish();

                } catch (Exception e) {
                    Log.i(TAG, "error to send - " + e.getMessage());
                }
            }
        };

        timer.start();
    }

    public static void goToQuestion(int position) {
        recyclerQuestions.smoothScrollToPosition(position);
    }

    @Override
    public void onBackPressed() {
        dialogExit.show();
    }
}
