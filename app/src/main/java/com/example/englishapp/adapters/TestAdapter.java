package com.example.englishapp.adapters;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.models.TestModel;
import com.example.englishapp.interfaces.TestClickedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestHolder> {

    private static final String TAG = "AdapterTest";
    private List<TestModel> testModelList;
    private List<TestModel> allTests;
    private TestClickedListener listener;
    private Context context;
    private Timer timer;

    public TestAdapter(List<TestModel> testModelList, TestClickedListener listener, Context context) {
        this.testModelList = testModelList;
        this.listener = listener;
        this.context = context;
        allTests = testModelList;
    }

    @Override
    public TestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "createView");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_item_layout, parent, false);

        return new TestHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestHolder holder, int position) {

        int progress = testModelList.get(position).getTopScore();

        Log.i(TAG, "setData - " + position + " " + progress);

        holder.setData(position, progress);

    }

    @Override
    public int getItemCount() {
        return testModelList.size();
    }

    public void searchTests(final String searchKeyword) {
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(searchKeyword.trim().isEmpty()) {
                    testModelList = allTests;
                } else {

                    ArrayList<TestModel> testWithKey = new ArrayList<>();
                    for(TestModel test: allTests) {

                        if (test.getName().contains(searchKeyword.toLowerCase())) {
                            testWithKey.add(test);
                        }
                    }

                    testModelList = testWithKey;
                }

                new android.os.Handler(Looper.getMainLooper()).post(() -> notifyDataSetChanged());
            }
        }, 500);
    }


    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }


    public class TestHolder extends RecyclerView.ViewHolder {
        private TextView title, numberOfQuestions, time, percent;
        private ProgressBar progressBar;

        public TestHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            numberOfQuestions = itemView.findViewById(R.id.numberOfQuestions);
            time = itemView.findViewById(R.id.time);
            percent = itemView.findViewById(R.id.percent);
            progressBar = itemView.findViewById(R.id.progressBar);

        }

        private void setData(int pos, int progress) {

            TestModel testModel = testModelList.get(pos);

            title.setText(testModel.getName());
            if (testModel.getAmountOfQuestion() == 1) {
                numberOfQuestions.setText(testModel.getAmountOfQuestion() + " Question");
            } else {
                numberOfQuestions.setText(testModel.getAmountOfQuestion() + " Questions");
            }

            percent.setText(progress + "%");
            time.setText(testModel.getTime() + " min");

            progressBar.setProgress(progress);

            Log.i(TAG, "Data was set");

            itemView.setOnClickListener(v -> listener.onTestClicked(testModel));

        }
    }
}
