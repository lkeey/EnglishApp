package com.example.englishapp.presentation.activities;

import static com.example.englishapp.database.Constants.KEY_IS_WORDS;
import static com.example.englishapp.database.Constants.KEY_TEST_TIME;
import static com.example.englishapp.database.Constants.SHOW_FRAGMENT_DIALOG;
import static com.example.englishapp.database.DataBaseExam.NOT_VISITED;
import static com.example.englishapp.database.DataBaseExam.UNANSWERED;

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
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.example.englishapp.repositories.ExamRepository;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ExamActivity extends BaseActivity {
    private static final String TAG = "ActivityExam";
    private ExamInfoFragment fragment;
    private LinearLayout layoutInfo;
    private TestModel testModel;
    private static RecyclerView recyclerQuestions;
    private TextView questionNumber, amountTime, testName;
    private Button btnSubmit, btnContinue, btnExit, btnCancel, btnComplete;
    private ImageView questionList, bookMarkImg, previousQuestion, nextQuestion;
    private CountDownTimer timer;
    private long timeCounter;
    int numberOfQuestion;
    private Dialog dialogExit, dialogComplete;
    private boolean isWordExam;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        receiveData();

        init();

        setListeners();

        setSnapHelper();

    }

    private void receiveData() {

        Intent intent = getIntent();

        isWordExam = intent.getBooleanExtra(KEY_IS_WORDS, false);

        if (isWordExam) {
            Log.i(TAG, "Word-Exam");

            startTimer(DataBaseQuestions.LIST_OF_QUESTIONS.size());
        }
    }

    private void setLayoutInfo() {
        layoutInfo.setVisibility(View.VISIBLE);

        numberOfQuestion = 0;

        testModel = new DataBaseTests().findTestById(DataBaseTests.CHOSEN_TEST_ID);

        testName.setText(testModel.getName());
        amountTime.setText(testModel.getTime() + getString(R.string.minutes));

        startTimer(testModel.getTime());

        // if question was bookmarked
        if (DataBaseQuestions.LIST_OF_QUESTIONS.get(0).isBookmarked()) {
            bookMarkImg.setColorFilter(ContextCompat.getColor(ExamActivity.this, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            bookMarkImg.setColorFilter(ContextCompat.getColor(ExamActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    private void setListeners() {

        btnContinue.setOnClickListener(v -> dialogExit.dismiss());

        btnExit.setOnClickListener(v -> {

            timer.cancel();

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
            if (numberOfQuestion < DataBaseQuestions.LIST_OF_QUESTIONS.size()) {
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

        int status = new ExamRepository().addToBookmark(numberOfQuestion);

        if (status == ExamRepository.CODE_UNBOOKMARKED) {
            bookMarkImg.setColorFilter(ContextCompat.getColor(ExamActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
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

                if (view != null) {
                    numberOfQuestion = Objects.requireNonNull(recyclerView.getLayoutManager()).getPosition(view);
                }

                QuestionModel questionModel = DataBaseQuestions.LIST_OF_QUESTIONS.get(numberOfQuestion);

                if (!isWordExam) {
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
                }

                questionNumber.setText((numberOfQuestion + 1) + " / " + DataBaseQuestions.LIST_OF_QUESTIONS.size());

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
        layoutInfo = findViewById(R.id.layoutInfo);

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

        QuestionsAdapter questionsAdapter = new QuestionsAdapter(DataBaseQuestions.LIST_OF_QUESTIONS,false, isWordExam);
        recyclerQuestions.setAdapter(questionsAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(ExamActivity.this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerQuestions.setLayoutManager(manager);

        questionNumber.setText("1 / " + DataBaseQuestions.LIST_OF_QUESTIONS.size());

        if (!isWordExam) {
            setLayoutInfo();
        }

    }

    private void startTimer(int time) {
        long totalTime = (long) time * 60 * 1_000;
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
                try {
                    //Toast.makeText(ExamActivity.this, "Finished", Toast.LENGTH_SHORT).show();

                    Log.i(TAG, "totalTime - " + totalTime + " - timeCounter - " + timeCounter);

                    Intent intent = new Intent(ExamActivity.this, MainActivity.class);
                    intent.putExtra(KEY_TEST_TIME, totalTime - timeCounter);
                    intent.putExtra(KEY_IS_WORDS, isWordExam);

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
