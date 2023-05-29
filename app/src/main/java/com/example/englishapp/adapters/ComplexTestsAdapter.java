package com.example.englishapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.models.ComplexTest;
import com.example.englishapp.R;

import java.util.List;

public class ComplexTestsAdapter extends RecyclerView.Adapter<ComplexTestsAdapter.ComplexTestsHolder> {

    private List<ComplexTest> list;

    public ComplexTestsAdapter(List<ComplexTest> list) {
        this.list = list;
    }

    static class ComplexTestsHolder extends RecyclerView.ViewHolder {

        public ComplexTestsHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public ComplexTestsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ComplexTestsHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_test_container,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ComplexTestsHolder holder, int position) {
//        holder.
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
