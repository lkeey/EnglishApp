package com.example.englishapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.englishapp.database.DataBase;
import com.example.englishapp.models.UserModel;
import com.example.englishapp.R;
import com.example.englishapp.models.ChatMessage;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "AdapterMessages";
    private static final int VIEW_TYPE_SENT = 0;
    private static final int VIEW_TYPE_RECEIVED = 1;
    private static Context context = null;
    private final List<ChatMessage> messages;
    private static UserModel sender = null;

    public MessageAdapter(Context context, List<ChatMessage> messages, UserModel sender) {
        this.context = context;
        this.messages = messages;
        this.sender = sender;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_SENT) {
            Log.i(TAG, "View - Sent");

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_sent_message, parent, false);

            return new SentMessageViewHolder(view);

        } else {
            Log.i(TAG, "View - Received");

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_received_message, parent, false);

            return new ReceivedMessageViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(messages.get(position));
        } else {
            ((ReceivedMessageViewHolder) holder).setData(messages.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Log.i(TAG, "SENDER - " + sender.getName() + " - " + messages.get(position).getSenderId());

        if(messages.get(position).getSenderId().equals(DataBase.USER_MODEL.getUid())) {
            Log.i(TAG, "User's message - " + DataBase.USER_MODEL.getName() + " - " + DataBase.USER_MODEL.getUid());

            return VIEW_TYPE_SENT;
        } else {
            Log.i(TAG, "Received message - " + sender.getName() + " - " + sender.getUid());

            return VIEW_TYPE_RECEIVED;
        }

    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private TextView textMessage, textDateTime;

        SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);


            textMessage = itemView.findViewById(R.id.textMessageSent);
            textDateTime = itemView.findViewById(R.id.textDateTimeSent);

        }

        private void setData(ChatMessage message) {

            textMessage.setText(message.getMessage());
            textDateTime.setText(message.getBeautyDateTime());

            Log.i(TAG, "MSG - " + message.getMessage());

        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        private TextView textMessage, textDateTime;
        private ImageView imgProfile;

        ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfileReceived);
            textMessage = itemView.findViewById(R.id.textMessageReceived);
            textDateTime = itemView.findViewById(R.id.textDateTimeReceived);

        }

        private void setData(ChatMessage message) {
            Log.i(TAG, "Set outsider message");

            textMessage.setText(message.getMessage());
            textDateTime.setText(message.getBeautyDateTime());
            Glide.with(context).load(sender.getPathToImage()).into(imgProfile);
        }
    }

}
