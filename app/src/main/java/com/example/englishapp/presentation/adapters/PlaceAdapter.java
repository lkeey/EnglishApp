package com.example.englishapp.presentation.adapters;

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

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private final List<UserModel> listOfUsers;
    private final UserListener listener;

    public PlaceAdapter(List<UserModel> listOfUsers, UserListener listener) {
        this.listOfUsers = listOfUsers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceAdapter.ViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return listOfUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView userScore, userPlace, userName;
        private final ImageView imgUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUser = itemView.findViewById(R.id.userImage);
            userScore = itemView.findViewById(R.id.userScore);
            userPlace = itemView.findViewById(R.id.userPlace);
            userName = itemView.findViewById(R.id.userName);

        }

        private void setData(int position) {

            UserModel userModel = listOfUsers.get(position);

            userName.setText(userModel.getName());
            userScore.setText("Score: " + userModel.getScore());
            userPlace.setText("Place: " + userModel.getPlace());

            Glide.with(itemView.getContext()).load(userModel.getPathToImage()).into(imgUser);

            itemView.setOnClickListener(v -> listener.onUserClicked(userModel));
        }
    }
}
