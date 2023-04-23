package com.example.englishapp.chat;

import static com.example.englishapp.messaging.Constants.KEY_CHOSEN_USER_DATA;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.Authentication.CategoryFragment;
import com.example.englishapp.MVP.FeedActivity;
import com.example.englishapp.MVP.UserModel;
import com.example.englishapp.R;
public class DiscussFragment extends Fragment {
    private static final String TAG = "FragmentDiscussion";
    private UserModel receivedUser;
    private RecyclerView recyclerMessages;
    private FrameLayout layoutSend;
    private EditText inputMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discuss, container, false);

        receiveData();

        init(view);

        return view;
    }

    private void init(View view) {

        recyclerMessages = view.findViewById(R.id.recyclerMessages);
        layoutSend = view.findViewById(R.id.layoutSend);
        inputMessage = view.findViewById(R.id.inputMessage);

        ((FeedActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((FeedActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((FeedActivity) getActivity()).getSupportActionBar().setTitle(receivedUser.getName());
        ((FeedActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((FeedActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_btn_back);

    }

    private void receiveData() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            Log.i(TAG, "Data");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                receivedUser = (UserModel) bundle.getSerializable(KEY_CHOSEN_USER_DATA);
            }

            Toast.makeText(getActivity(), "User - " + receivedUser.getName(), Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getActivity(), "Can not find user", Toast.LENGTH_SHORT).show();
        }
    }

    private void setListeners() {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            ((FeedActivity) getActivity()).setFragment(new CategoryFragment());
        }

        return super.onOptionsItemSelected(item);
    }
}