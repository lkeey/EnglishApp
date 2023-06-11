package com.example.englishapp.domain.repositories;

import static com.example.englishapp.data.database.Constants.KEY_COLLECTION_CONVERSATION;
import static com.example.englishapp.data.database.Constants.KEY_LAST_MESSAGE;
import static com.example.englishapp.data.database.Constants.KEY_RECEIVER_ID;
import static com.example.englishapp.data.database.Constants.KEY_SENDER_ID;
import static com.example.englishapp.data.database.Constants.KEY_TIME_STAMP;
import static com.example.englishapp.data.database.DataBaseDiscussion.CURRENT_CONVERSATION_ID;
import static com.example.englishapp.data.database.DataBasePersonalData.DATA_FIRESTORE;
import static com.example.englishapp.data.database.DataBasePersonalData.USER_MODEL;

import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.englishapp.data.database.Constants;
import com.example.englishapp.data.database.DataBaseDiscussion;
import com.example.englishapp.data.database.DataBasePersonalData;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.domain.interfaces.NotificationService;
import com.example.englishapp.data.models.ChatMessage;
import com.example.englishapp.data.models.DataModel;
import com.example.englishapp.data.models.PushNotification;
import com.example.englishapp.data.models.UserModel;
import com.example.englishapp.domain.services.FCMApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageRepository {

    private static final String TAG = "RepositoryMessage";
    public static List<ChatMessage> CHAT_MESSAGES = new ArrayList<>();
    public static UserModel receivedUser;

    public void sendMessage(EditText inputMessage) {

        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, DataBasePersonalData.USER_MODEL.getUid());
        message.put(Constants.KEY_RECEIVER_ID, receivedUser.getUid());
        message.put(Constants.KEY_MESSAGE, inputMessage.getText().toString());
        message.put(Constants.KEY_TIME_STAMP, new Date());

        DataBaseDiscussion dataBaseDiscussion = new DataBaseDiscussion();

        dataBaseDiscussion.sendMessage(message, new CompleteListener() {
            @Override
            public void OnSuccess() {
                Log.i(TAG, "Created message from - " + DataBasePersonalData.USER_MODEL.getUid() + " - to - " + receivedUser.getUid());

                if (CURRENT_CONVERSATION_ID != null) {
                    updateConversation(inputMessage.getText().toString());
                } else {
                    HashMap<String, Object> conversation = new HashMap<>();
                    conversation.put(KEY_SENDER_ID, USER_MODEL.getUid());
                    conversation.put(KEY_RECEIVER_ID, receivedUser.getUid());
                    conversation.put(KEY_LAST_MESSAGE, inputMessage.getText().toString());
                    conversation.put(KEY_TIME_STAMP, new Date());

                    dataBaseDiscussion.addConversation(conversation, new CompleteListener() {
                        @Override
                        public void OnSuccess() {
                            Log.i(TAG, "Discussion Created");
                        }
                        @Override
                        public void OnFailure() {
                            Log.i(TAG, "Fail To Create Discussion");
                        }
                    });
                }

                // send notification
                sendNotificationToUser(
                        receivedUser.getFcmToken(),
                        "New message from " + USER_MODEL.getName(),
                        inputMessage.getText().toString(),
                        USER_MODEL.getUid()
                );

                inputMessage.setText(null);
            }

            @Override
            public void OnFailure() {
                Log.i(TAG, "Can not send message");
            }
        });
    }

    public void updateConversation(String message) {
        try {
            DocumentReference reference = DATA_FIRESTORE
                .collection(KEY_COLLECTION_CONVERSATION)
                .document(CURRENT_CONVERSATION_ID);

            reference.update(
                KEY_LAST_MESSAGE, message,
                KEY_TIME_STAMP, new Date()
            );

        } catch (Exception e) {
            Log.i(TAG, "updateConversation - " + e.getMessage());
        }
    }

    public void sendNotificationToUser(String token, String title, String body, String senderUID) {

        Log.i(TAG, "sending notification");

        PushNotification notification = new PushNotification(
                token,
                new DataModel(
                        title, body, senderUID
                )
        );

        NotificationService notificationService = FCMApi.getInstance().create(NotificationService.class);

        Call<ResponseBody> call = notificationService.sendNotification(notification);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                Log.i(TAG, "Notification was successfully sent - " + response.isSuccessful());

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.i(TAG, "Can not send notification - " + t.getMessage());
            }
        });
    }


    public void checkForConversation() {
        try {

            Log.i(TAG, "User - " + USER_MODEL.getUid() + " - Receiver - " + receivedUser.getUid());

            if (CHAT_MESSAGES.size() != 0) {
                checkForConversationRemotely(
                        USER_MODEL.getUid(),
                        receivedUser.getUid()
                );

                checkForConversationRemotely(
                        receivedUser.getUid(),
                        USER_MODEL.getUid()
                );
            }
        } catch (Exception e) {
            Log.i(TAG, "checkForConversation error - " + e.getMessage());
        }
    }

    private void checkForConversationRemotely(String senderId, String receiverId) {
        try {

            Log.i(TAG, "sender - " + senderId + " - receiver - " + receiverId);

            DATA_FIRESTORE.collection(KEY_COLLECTION_CONVERSATION)
                .whereEqualTo(KEY_SENDER_ID, senderId)
                .whereEqualTo(KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversationOnComplete);

        } catch (Exception e) {
            Log.i(TAG, "checkForConversationRemotely error - " + e.getMessage());
        }
    }


    private final OnCompleteListener<QuerySnapshot> conversationOnComplete = task -> {

        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {

            DocumentSnapshot document = task
                    .getResult()
                    .getDocuments()
                    .get(0);

            CURRENT_CONVERSATION_ID = document.getId();

            Log.i(TAG, "ConversationID - " + CURRENT_CONVERSATION_ID);

        } else {
            Log.i(TAG, "conversationOnComplete error - " + task.getException());
        }
    };

}
