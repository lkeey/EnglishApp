package com.example.englishapp.testsAndWords;

import static com.example.englishapp.messaging.Constants.ANSWERED;
import static com.example.englishapp.messaging.Constants.REVIEW;
import static com.example.englishapp.messaging.Constants.UNANSWERED;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.MVP.DataBase;
import com.example.englishapp.R;

import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder>{

    private static final String TAG = "AdapterOptions";
    private List<OptionModel> optionModels;
    private int questionId;
    private Context context;
    private boolean isShowing;

    public OptionsAdapter(List<OptionModel> optionModels, int questionId, Context context, boolean isShowing) {
        this.optionModels = optionModels;
        this.questionId = questionId;
        this.context = context;
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

            optionName.setText(optionModel.getOption());

            DataBase.LIST_OF_QUESTIONS.get(questionId).getOptionsList().get(position).setTv(optionName);

            if (!isShowing) {

                itemView.setOnClickListener(v -> setOption(optionName, position));

            } else if (isShowing && optionModel.isCorrect()) {

                optionModel.getTv().setBackgroundResource(R.drawable.selected_btn);
                optionModel.getTv().setTextColor(ContextCompat.getColor(context, R.color.white));

            }
        }

        private void setOption(TextView tv, int optionNum) {

            int selectedOption = DataBase.LIST_OF_QUESTIONS.get(questionId).getSelectedOption();

            if (selectedOption != optionNum) {

                Log.i(TAG, "selected - " + optionNum + " - " + DataBase.LIST_OF_QUESTIONS.get(questionId).getSelectedOption());

                // select new text view
                tv.setBackgroundResource(R.drawable.selected_btn);
                tv.setTextColor(ContextCompat.getColor(context, R.color.white));

                if (selectedOption != -1) {
                    // unselect old text view
                    TextView beforeChoice = DataBase.LIST_OF_QUESTIONS.get(questionId).getOptionsList().get(selectedOption).getTv();

                    beforeChoice.setBackgroundResource(R.drawable.round_view_with_stroke);
                    beforeChoice.setTextColor(ContextCompat.getColor(context, com.google.android.material.R.color.design_default_color_primary));
                }

                changeStatus(ANSWERED);

                DataBase.LIST_OF_QUESTIONS.get(questionId).setSelectedOption(optionNum);

            } else {

                Log.i(TAG, "unselected - " + optionNum + " - " + DataBase.LIST_OF_QUESTIONS.get(questionId).getSelectedOption());

                tv.setBackgroundResource(R.drawable.round_view_with_stroke);
                tv.setTextColor(ContextCompat.getColor(context, com.google.android.material.R.color.design_default_color_primary));

                changeStatus(UNANSWERED);

                DataBase.LIST_OF_QUESTIONS.get(questionId).setSelectedOption(-1);

            }
        }

        private void changeStatus(int status) {

            if(DataBase.LIST_OF_QUESTIONS.get(questionId).getStatus() != REVIEW) {
                DataBase.LIST_OF_QUESTIONS.get(questionId).setStatus(status);
            }

        }
    }
}
