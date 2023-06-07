package com.example.englishapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.models.QuestionModel;

import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    private static final String TAG = "AdapterQuestions";
    private List<QuestionModel> listQuestions;
    private boolean isShowing, isWordExam;

    public QuestionsAdapter(List<QuestionModel> listQuestions, boolean isShowing, boolean isWordExam) {
        this.listQuestions = listQuestions;
        this.isShowing = isShowing;
        this.isWordExam = isWordExam;
    }

    @NonNull
    @Override
    public QuestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionsAdapter.ViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return listQuestions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView questionName;
        private ImageView img;
        private RecyclerView layoutOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            questionName = itemView.findViewById(R.id.questionName);
            layoutOptions = itemView.findViewById(R.id.layoutOptions);
            img = itemView.findViewById(R.id.imgQuestion);

        }

        private void setData(final int position) {
            try {
                QuestionModel questionModel = listQuestions.get(position);

                questionName.setText(questionModel.getQuestion());

                if (isWordExam) {
                    Log.i(TAG, "show img - " + questionModel.getBmp());

                    img.setImageBitmap(questionModel.getBmp());

                } else {
                    Log.i(TAG, "hide image - " + isWordExam);

                    img.setVisibility(View.GONE);
                }

                // set adapter for options
                OptionsAdapter optionsAdapter = new OptionsAdapter(questionModel.getOptionsList(), position, isShowing);
                layoutOptions.setAdapter(optionsAdapter);

                LinearLayoutManager manager = new LinearLayoutManager(itemView.getContext());
                manager.setOrientation(RecyclerView.VERTICAL);
                layoutOptions.setLayoutManager(manager);

            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }

    }
}
