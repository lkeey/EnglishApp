package com.example.englishapp.chat;

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
import com.example.englishapp.MVP.DataBase;
import com.example.englishapp.MVP.UserModel;
import com.example.englishapp.R;

import java.util.List;

public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.ViewHolder> {

    private final static String TAG = "AdapterRecentConversation";
    private static Context context;
    private final List<ChatMessage> chatMessages;
    private static ConversationListener conversationListener = null;

    public RecentConversationAdapter(Context context, List<ChatMessage> chatMessages, ConversationListener conversationListener) {
        this.context = context;
        this.chatMessages = chatMessages;
        this.conversationListener = conversationListener;
    }

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

    static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView profileImg;
        private TextView userName, recentMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImg = itemView.findViewById(R.id.imgProfile);
            userName = itemView.findViewById(R.id.userName);
            recentMessage = itemView.findViewById(R.id.recentMessage);
        }

        private void setData(ChatMessage message) {

            String textUID = message.getSenderId();

            if(message.getSenderId().equals(DataBase.USER_MODEL.getUid())) {
                textUID = message.getReceiverId();
            }

            try {

                Log.i(TAG, "uid - " + textUID);

                UserModel userModel = DataBase.findUserById(textUID);

                userName.setText(userModel.getName());
                Glide.with(context).load(userModel.getPathToImage()).into(profileImg);
                recentMessage.setText(message.getMessage());

                itemView.setOnClickListener(v -> conversationListener.onConversationClicked(userModel));

            } catch (Exception e) {
                Log.i(TAG, "error - " + e.getMessage());
            }

//            DATA_FIRESTORE.collection(KEY_COLLECTION_USERS).document(textUID)
//                .get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    try {
//
//                        userName.setText(documentSnapshot.getString(KEY_NAME));
//                        Glide.with(context).load(documentSnapshot.getString(KEY_PROFILE_IMG)).into(profileImg);
//                        recentMessage.setText(message.getMessage());
//
//                        itemView.setOnClickListener(v -> {
////                            UserModel userModel = new UserModel(
////                                    documentSnapshot.getString(KEY_USER_UID),
////                                    documentSnapshot.getString(KEY_NAME),
////                                    documentSnapshot.getString(KEY_EMAIL),
////                                    documentSnapshot.getString(KEY_GENDER),
////                                    documentSnapshot.getString(KEY_MOBILE),
////                                    documentSnapshot.getString(KEY_PROFILE_IMG),
////                                    documentSnapshot.getString(KEY_DOB),
////                                    documentSnapshot.getString(KEY_FCM_TOKEN),
////                                    documentSnapshot.getLong(KEY_SCORE).intValue(),
////                                    documentSnapshot.getLong(KEY_BOOKMARKS).intValue(),
////                                    documentSnapshot.getLong(KEY_LATITUDE).intValue(),
////                                    documentSnapshot.getLong(KEY_LONGITUDE).intValue()
////                            );
//
//                            UserModel userModel = new UserModel();
//
//                            userModel.setUid(documentSnapshot.getString(KEY_USER_UID));
//                            userModel.setName(documentSnapshot.getString(KEY_NAME));
//                            userModel.setEmail(documentSnapshot.getString(KEY_EMAIL));
//                            userModel.setGender(documentSnapshot.getString(KEY_GENDER));
//                            userModel.setMobile(documentSnapshot.getString(KEY_MOBILE));
//                            userModel.setPathToImage(documentSnapshot.getString(KEY_PROFILE_IMG));
//                            userModel.setDateOfBirth(documentSnapshot.getString(KEY_DOB));
//                            userModel.setFcmToken(documentSnapshot.getString(KEY_FCM_TOKEN));
//                            userModel.setScore(documentSnapshot.getLong(KEY_SCORE).intValue());
//                            userModel.setBookmarksCount(documentSnapshot.getLong(KEY_BOOKMARKS).intValue());
//                            userModel.setLatitude(documentSnapshot.getGeoPoint(KEY_LOCATION).getLatitude());
//                            userModel.setLongitude(documentSnapshot.getGeoPoint(KEY_LOCATION).getLongitude());
//
//
//                            conversationListener.onConversationClicked(userModel);
//                        });
//
//                    } catch (Exception e) {
//                        Log.i(TAG, e.getMessage());
//                    }
//                })
//                .addOnFailureListener(e -> Log.i(TAG, e.getMessage()));
        }
    }
}
