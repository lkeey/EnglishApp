package com.example.englishapp.testsAndWords;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;

import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestHolder> {

    private static final String TAG = "AdapterTest";
    private List<TestModel> testModelList;
    private TestClickedListener listener;
    private Context context;

    public TestAdapter(List<TestModel> testModelList, TestClickedListener listener, Context context) {
        this.testModelList = testModelList;
        this.listener = listener;
        this.context = context;
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
