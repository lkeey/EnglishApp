package com.example.englishapp.fragments;

import static com.example.englishapp.database.Constants.KEY_CHOSEN_CATEGORY;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.activities.MainActivity;
import com.example.englishapp.adapters.CategoryAdapter;
import com.example.englishapp.database.DataBase;
import com.example.englishapp.interfaces.CategoryClickedListener;
import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.models.CategoryModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CategoryFragment extends Fragment implements CategoryClickedListener {

    private static final String TAG = "CategoryFragment";
    private CategoryAdapter categoryAdapter;
    private RecyclerView recyclerCategories;
    private Dialog progressBar;
    private TextView dialogText, textClose;
    private Button btnCreateCategory;
    private ProgressBar progressCategory;
    private FloatingActionButton fab;
    private EditText inputSearch, inputNameCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_category, container, false);

        try {
            init(view);

            setListeners();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

        return view;
        
    }

    private void setListeners() {

        fab.setOnClickListener(v -> {

            // show dialog
            inputNameCategory.setText(null);
            progressCategory.setVisibility(View.GONE);
            progressBar.show();

        });

        textClose.setOnClickListener(v -> progressBar.dismiss());

        btnCreateCategory.setOnClickListener(v -> {
            String nameCategory = inputNameCategory.getText().toString().trim();

            if (nameCategory.isEmpty()) {
                Toast.makeText(getActivity(), "Name must be not null", Toast.LENGTH_SHORT).show();
            } else {
                progressCategory.setVisibility(View.VISIBLE);

                DataBase.createCategory(nameCategory, new CompleteListener() {
                    @Override
                    public void OnSuccess() {
                        Log.i(TAG, "Category was created");
                        Toast.makeText(getContext(), "Category was created", Toast.LENGTH_SHORT).show();

                        // refresh adapter
                        categoryAdapter.notifyDataSetChanged();

                        progressBar.dismiss();

                    }

                    @Override
                    public void OnFailure() {
                        Log.i(TAG, "Can not create category");
                        Toast.makeText(getContext(), "Can not create category", Toast.LENGTH_SHORT).show();

                        progressBar.dismiss();
                    }
                });
            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                categoryAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable key) {
                if(DataBase.LIST_OF_CATEGORIES.size() != 0) {
                    categoryAdapter.searchCategories(key.toString());
                }
            }
        });

    }

    private void init(View view) {

        ((MainActivity) getActivity()).setTitle(R.string.nameFeed);

        recyclerCategories = view.findViewById(R.id.recyclerCategories);
        fab = view.findViewById(R.id.fab);
        inputSearch = view.findViewById(R.id.inputSearch);

        categoryAdapter = new CategoryAdapter(DataBase.LIST_OF_CATEGORIES, this, getActivity());
        recyclerCategories.setAdapter(categoryAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);

        recyclerCategories.setLayoutManager(manager);

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_create_category);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogText = progressBar.findViewById(R.id.dialogText);
        textClose = progressBar.findViewById(R.id.textCancel);
        btnCreateCategory = progressBar.findViewById(R.id.btnCreateCategory);
        inputNameCategory = progressBar.findViewById(R.id.inputSearch);
        progressCategory = progressBar.findViewById(R.id.progressBar);

        dialogText.setText(R.string.progressBarCreating);

    }

    @Override
    public void onCategoryClicked(CategoryModel category) {
        Log.i(TAG, "Category - " + category.getName());

        DataBase.CHOSEN_CATEGORY_ID = category.getId();

        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_CHOSEN_CATEGORY, category);
        SplashLearningFragment fragment = new SplashLearningFragment();
        fragment.setArguments(bundle);

        ((MainActivity) getActivity()).setFragment(fragment);
    }
}