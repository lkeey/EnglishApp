package com.example.englishapp.testsAndWords;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.MVP.DataBase;
import com.example.englishapp.R;

public class ExamActivity extends AppCompatActivity {

    private RecyclerView recyclerQuestions;
    private TextView questionNumber, amountTime, testName;
    private Button btnSubmit, btnClear;
    private ImageView questionList, bookMarkImg, previousQuestion, nextQuestion;
    private int numberOfQuestion;
    private DrawerLayout drawerLayout;
    private GridView gridQuestions;
    private CountDownTimer timer;
    private long timeCounter;
    private QuestionsAdapter questionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        init();
    }

    private void init() {
        recyclerQuestions = findViewById(R.id.recyclerQuestions);
        questionNumber = findViewById(R.id.questionNumber);
        amountTime = findViewById(R.id.amountTime);
        testName = findViewById(R.id.testName);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnClear = findViewById(R.id.btnClear);
        questionList = findViewById(R.id.questionList);
        bookMarkImg = findViewById(R.id.bookMarkImg);
        previousQuestion = findViewById(R.id.previousQuestion);
        nextQuestion = findViewById(R.id.nextQuestion);
        gridQuestions = findViewById(R.id.gridQuestions);
        nextQuestion = findViewById(R.id.nextQuestion);

        numberOfQuestion = 0;

        TestModel testModel = DataBase.findTestById(DataBase.CHOSEN_TEST_ID);

        questionNumber.setText("1 / " + DataBase.LIST_OF_QUESTIONS.size());
        testName.setText(testModel.getName());
        amountTime.setText(testModel.getTime() + "min");

        questionsAdapter = new QuestionsAdapter(DataBase.LIST_OF_QUESTIONS, ExamActivity.this);
        recyclerQuestions.setAdapter(questionsAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(ExamActivity.this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerQuestions.setLayoutManager(manager);

    }

}