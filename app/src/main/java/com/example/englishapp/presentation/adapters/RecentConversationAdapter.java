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
import com.example.englishapp.data.database.DataBasePersonalData;
import com.example.englishapp.data.database.DataBaseUsers;
import com.example.englishapp.domain.interfaces.ConversationListener;
import com.example.englishapp.data.models.ChatMessage;
import com.example.englishapp.data.models.UserModel;

import java.util.List;

public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.ViewHolder> {

    private final static String TAG = "AdapterRecentConversation";
    private final List<ChatMessage> chatMessages;
    private final ConversationListener conversationListener;

    public RecentConversationAdapter(List<ChatMessage> chatMessages, ConversationListener conversationListener) {
        this.chatMessages = chatMessages;
        this.conversationListener = conversationListener;
    }

    @NonNull
    @Override
    public RecentConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_recent_conversation, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentConversationAdapter.ViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder{
        private final ImageView profileImg;
        private final TextView userName, recentMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImg = itemView.findViewById(R.id.imgProfile);
            userName = itemView.findViewById(R.id.userName);
            recentMessage = itemView.findViewById(R.id.recentMessage);
        }

        private void setData(ChatMessage message) {

            String textUID = message.getSenderId();

            if(message.getSenderId().equals(DataBasePersonalData.USER_MODEL.getUid())) {
                textUID = message.getReceiverId();
            }

            try {

                Log.i(TAG, "uid - " + textUID);

                UserModel userModel = new DataBaseUsers().findUserById(textUID);

                userName.setText(userModel.getName());
                Glide.with(itemView.getContext()).load(userModel.getPathToImage()).into(profileImg);
                recentMessage.setText(message.getMessage());

                itemView.setOnClickListener(v -> conversationListener.onConversationClicked(userModel));

            } catch (Exception e) {
                Log.i(TAG, "error - " + e.getMessage());
            }

        }
    }
}
