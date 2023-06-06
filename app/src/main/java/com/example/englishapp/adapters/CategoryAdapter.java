package com.example.englishapp.adapters;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.interfaces.CategoryClickedListener;
import com.example.englishapp.models.CategoryModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private static final String TAG = "AdapterCategory";
    private List<CategoryModel> categoryModelList;
    private CategoryClickedListener listener;
    private List<CategoryModel> allCategories;
    private Timer timer;

    public CategoryAdapter(List<CategoryModel> categoryModelList, CategoryClickedListener listener) {
        this.categoryModelList = categoryModelList;
        this.listener = listener;
        allCategories = categoryModelList;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        try {
            Log.i(TAG, "BEGIN");

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_layout, parent, false);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public void searchCategories(final String searchKeyword) {
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(searchKeyword.trim().isEmpty()) {
                    categoryModelList = allCategories;
                } else {

                    ArrayList<CategoryModel> categoriesWithKey = new ArrayList<>();
                    for(CategoryModel category: allCategories) {


                        if (category.getName().contains(searchKeyword.toLowerCase())) {
                            categoriesWithKey.add(category);
                        }
                    }

                    categoryModelList = categoriesWithKey;
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView categoryName, numberOfTests;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryName = itemView.findViewById(R.id.categoryName);
            numberOfTests = itemView.findViewById(R.id.numberOfTests);

        }


        public void setData(int position) {

            categoryName.setText(categoryModelList.get(position).getName());
            numberOfTests.setText(String.valueOf(categoryModelList.get(position).getNumberOfTests()));

            itemView.setOnClickListener(v -> {
                Log.i(TAG, "find - " + categoryModelList.get(position));

                listener.onCategoryClicked(categoryModelList.get(position));
            });

        }
    }
}
