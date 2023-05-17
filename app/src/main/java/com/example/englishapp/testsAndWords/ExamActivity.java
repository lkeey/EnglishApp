package com.example.englishapp.testsAndWords;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.englishapp.MVP.DataBase;
import com.example.englishapp.MVP.MainActivity;
import com.example.englishapp.R;

import java.util.concurrent.TimeUnit;

public class ExamActivity extends AppCompatActivity {
    private QuestionsAdapter questionsAdapter;
    private TestModel testModel;
    private RecyclerView recyclerQuestions;
    private TextView questionNumber, amountTime, testName;
    private Button btnSubmit, btnContinue, btnExit;
    private ImageView questionList, bookMarkImg, previousQuestion, nextQuestion;
    private CountDownTimer timer;
    private long timeCounter;
    int numberOfQuestion;
    private Dialog dialogExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        nextQuestion = findViewById(R.id.nextQuestion);

        dialogExit = new Dialog(ExamActivity.this);
        dialogExit.setContentView(R.layout.dialog_exit);
        dialogExit.setCancelable(false);
        dialogExit.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogExit.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnContinue = dialogExit.findViewById(R.id.btnContinue);
        btnExit = dialogExit.findViewById(R.id.btnExit);

        numberOfQuestion = 0;

        testModel = DataBase.findTestById(DataBase.CHOSEN_TEST_ID);

        questionNumber.setText("1 / " + DataBase.LIST_OF_QUESTIONS.size());
        testName.setText(testModel.getName());
        amountTime.setText(testModel.getTime() + " minutes");

        questionsAdapter = new QuestionsAdapter(DataBase.LIST_OF_QUESTIONS, ExamActivity.this);
        recyclerQuestions.setAdapter(questionsAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(ExamActivity.this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerQuestions.setLayoutManager(manager);

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

                Toast.makeText(ExamActivity.this, "Finished", Toast.LENGTH_SHORT).show();

                ExamActivity.this.finish();
            }
        };

        timer.start();
    }

    @Override
    public void onBackPressed() {
        dialogExit.show();
    }
}