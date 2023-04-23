package com.example.englishapp.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.MVP.UserModel;
import com.example.englishapp.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "AdapterMessages";
    private static final int VIEW_TYPE_SENT = 0;
    private static final int VIEW_TYPE_RECEIVED = 1;
    private final Context context;
    private final List<ChatMessage> messages;
    private final UserModel sender;

    public MessageAdapter(Context context, List<ChatMessage> messages, UserModel sender) {
        this.context = context;
        this.messages = messages;
        this.sender = sender;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_sent_message, parent, false);

            return new SentMessageViewHolder(view);

        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_received_message, parent, false);

            return new SentMessageViewHolder(view);

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

        if(messages.get(position).getSenderId().equals(sender.getUid())) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }

    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private TextView textMessage, textDateTime;

        SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            textMessage = itemView.findViewById(R.id.textMessage);
            textDateTime = itemView.findViewById(R.id.textDateTime);

        }

        private void setData(ChatMessage message) {
            textMessage.setText(message.getMessage());
            textDateTime.setText(message.getDateTime());

        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        private TextView textMessage, textDateTime;
        private ImageView imgProfile;

        ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            textMessage = itemView.findViewById(R.id.textMessage);
            textDateTime = itemView.findViewById(R.id.textDateTime);

        }

        private void setData(ChatMessage message) {
            textMessage.setText(message.getMessage());
            textDateTime.setText(message.getDateTime());
//            Glide.with(context).load()
        }
    }

}
