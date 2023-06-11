package com.example.englishapp.presentation.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.englishapp.R;
import com.example.englishapp.domain.interfaces.UserListener;
import com.example.englishapp.data.models.UserModel;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private static final String TAG = "AdapterUsers";
    private final List<UserModel> users;
    private final UserListener listener;


    public UserAdapter(List<UserModel> users, UserListener userListener) {
        this.users = users;
        this.listener = userListener;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "View created");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_user, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView profileImg;
        private final TextView userName, userEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImg = itemView.findViewById(R.id.imgProfile);
            userName = itemView.findViewById(R.id.userName);
            userEmail = itemView.findViewById(R.id.userEmail);
        }

        private void setUserData(UserModel user) {
            try {

                userName.setText(user.getName());
                userEmail.setText(user.getEmail());

                itemView.setOnClickListener(v -> listener.onUserClicked(user));

                Log.i(TAG, user.getPathToImage());

                Glide.with(itemView.getContext()).load(user.getPathToImage()).into(profileImg);

                Log.i(TAG, "Added - " + user.getName());

            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }
}
