package com.example.englishapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.englishapp.models.UserModel;
import com.example.englishapp.R;
import com.example.englishapp.interfaces.UserListener;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private List<UserModel> listOfUsers;
    private UserListener listener;
    private Context context;

    public PlaceAdapter(List<UserModel> listOfUsers, UserListener listener, Context context) {
        this.listOfUsers = listOfUsers;
        this.listener = listener;
        this.context = context;
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
        private TextView userScore, userPlace, userName;
        private ImageView imgUser;

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

            Glide.with(context).load(userModel.getPathToImage()).into(imgUser);

            itemView.setOnClickListener(v -> listener.onUserClicked(userModel));
        }
    }
}
