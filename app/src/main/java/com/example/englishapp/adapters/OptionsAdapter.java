package com.example.englishapp.adapters;

import static com.example.englishapp.database.DataBaseExam.ANSWERED;
import static com.example.englishapp.database.DataBaseExam.REVIEW;
import static com.example.englishapp.database.DataBaseExam.UNANSWERED;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.database.DataBaseQuestions;
import com.example.englishapp.models.OptionModel;

import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder>{

    private static final String TAG = "AdapterOptions";
    private final List<OptionModel> optionModels;
    private final int questionId;
    private final boolean isShowing;

    public OptionsAdapter(List<OptionModel> optionModels, int questionId, boolean isShowing) {
        this.optionModels = optionModels;
        this.questionId = questionId;
        this.isShowing = isShowing;
    }

    @NonNull
    @Override
    public OptionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_option_item_layout, parent, false);

        return new OptionsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionsAdapter.ViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return optionModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView optionName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            optionName = itemView.findViewById(R.id.optionName);
        }

        private void setData(int position) {

            OptionModel optionModel = optionModels.get(position);

            Log.i(TAG, "option - " + optionModel.getOption() + " - " + optionModel.isCorrect());

            Log.i(TAG, "isShowing - " + isShowing);

            optionName.setText(optionModel.getOption());

            if (!isShowing) {

                itemView.setOnClickListener(v -> setOption(optionName, position));

                DataBaseQuestions.LIST_OF_QUESTIONS.get(questionId).getOptionsList().get(position).setTv(optionName);

            } else if (optionModel.isCorrect()) {

                optionName.setBackgroundResource(R.drawable.selected_btn);
                optionName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
            }
        }

        private void setOption(TextView tv, int optionNum) {

            int selectedOption;

            selectedOption = DataBaseQuestions.LIST_OF_QUESTIONS.get(questionId).getSelectedOption();

            if (selectedOption != optionNum) {

                Log.i(TAG, "selected - " + optionNum + " - " + DataBaseQuestions.LIST_OF_QUESTIONS.get(questionId).getSelectedOption());

                // select new text view
                tv.setBackgroundResource(R.drawable.selected_btn);
                tv.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));

                if (selectedOption != -1) {
                    // unselect old text view
                    TextView beforeChoice = DataBaseQuestions.LIST_OF_QUESTIONS.get(questionId).getOptionsList().get(selectedOption).getTv();

                    beforeChoice.setBackgroundResource(R.drawable.round_view_with_stroke);
                    beforeChoice.setTextColor(ContextCompat.getColor(itemView.getContext(), com.google.android.material.R.color.design_default_color_primary));
                }

                changeStatus(ANSWERED);

                DataBaseQuestions.LIST_OF_QUESTIONS.get(questionId).setSelectedOption(optionNum);

            } else {

                Log.i(TAG, "unselected - " + optionNum + " - " + DataBaseQuestions.LIST_OF_QUESTIONS.get(questionId).getSelectedOption());

                tv.setBackgroundResource(R.drawable.round_view_with_stroke);
                tv.setTextColor(ContextCompat.getColor(itemView.getContext(), com.google.android.material.R.color.design_default_color_primary));

                changeStatus(UNANSWERED);

                DataBaseQuestions.LIST_OF_QUESTIONS.get(questionId).setSelectedOption(-1);

            }
        }

        private void changeStatus(int status) {

            if(DataBaseQuestions.LIST_OF_QUESTIONS.get(questionId).getStatus() != REVIEW) {
                DataBaseQuestions.LIST_OF_QUESTIONS.get(questionId).setStatus(status);
            }

        }
    }
}
