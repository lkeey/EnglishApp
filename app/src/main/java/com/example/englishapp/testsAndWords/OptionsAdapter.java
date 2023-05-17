package com.example.englishapp.testsAndWords;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;

import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder>{

    private List<OptionModel> optionModels;
    private Context context;

    public OptionsAdapter(List<OptionModel> optionModels, Context context) {
        this.optionModels = optionModels;
        this.context = context;
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

        private TextView optionName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            optionName = itemView.findViewById(R.id.optionName);
        }

        private void setData(int position) {
            OptionModel optionModel = optionModels.get(position);

            optionName.setText(optionModel.getOption());
        }
    }
}
