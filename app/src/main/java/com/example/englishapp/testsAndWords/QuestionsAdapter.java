package com.example.englishapp.testsAndWords;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;

import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    private List<QuestionModel> listQuestions;
    private Context context;
    private boolean isShowing;

    public QuestionsAdapter(List<QuestionModel> listQuestions, Context context, boolean isShowing) {
        this.listQuestions = listQuestions;
        this.context = context;
        this.isShowing = isShowing;
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
        private RecyclerView layoutOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            questionName = itemView.findViewById(R.id.questionName);
            layoutOptions = itemView.findViewById(R.id.layoutOptions);

        }

        private void setData(final int position) {

            QuestionModel questionModel = listQuestions.get(position);

            questionName.setText(questionModel.getQuestion());

            // set adapter for options
            OptionsAdapter optionsAdapter = new OptionsAdapter(questionModel.getOptionsList(), position, context, isShowing);
            layoutOptions.setAdapter(optionsAdapter);

            LinearLayoutManager manager = new LinearLayoutManager(context);
            manager.setOrientation(RecyclerView.VERTICAL);
            layoutOptions.setLayoutManager(manager);

        }

    }
}
