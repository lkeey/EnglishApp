package com.example.englishapp.chat;

import static com.example.englishapp.MVP.DataBase.CURRENT_CONVERSATION_ID;
import static com.example.englishapp.MVP.DataBase.DATA_FIRESTORE;
import static com.example.englishapp.MVP.DataBase.USER_MODEL;
import static com.example.englishapp.messaging.Constants.KEY_AVAILABILITY;
import static com.example.englishapp.messaging.Constants.KEY_CHOSEN_USER_DATA;
import static com.example.englishapp.messaging.Constants.KEY_COLLECTION_CHAT;
import static com.example.englishapp.messaging.Constants.KEY_COLLECTION_CONVERSATION;
import static com.example.englishapp.messaging.Constants.KEY_COLLECTION_USERS;
import static com.example.englishapp.messaging.Constants.KEY_FCM_TOKEN;
import static com.example.englishapp.messaging.Constants.KEY_LAST_MESSAGE;
import static com.example.englishapp.messaging.Constants.KEY_MESSAGE;
import static com.example.englishapp.messaging.Constants.KEY_NAME;
import static com.example.englishapp.messaging.Constants.KEY_RECEIVER_ID;
import static com.example.englishapp.messaging.Constants.KEY_SENDER_ID;
import static com.example.englishapp.messaging.Constants.KEY_TIME_STAMP;
import static com.example.englishapp.messaging.Constants.KEY_USER_UID;
import static com.example.englishapp.messaging.Constants.REMOTE_MESSAGE_DATA;
import static com.example.englishapp.messaging.Constants.REMOTE_MESSAGE_REGISTRATION_IDS;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.Authentication.CategoryFragment;
import com.example.englishapp.MVP.CompleteListener;
import com.example.englishapp.MVP.DataBase;
import com.example.englishapp.MVP.FeedActivity;
import com.example.englishapp.MVP.UserModel;
import com.example.englishapp.R;
import com.example.englishapp.messaging.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class DiscussFragment extends Fragment {
    private static final String TAG = "FragmentDiscussion";
    private UserModel receivedUser;
    private RecyclerView recyclerMessages;
    private ArrayList chatMessages;
    private MessageAdapter messageAdapter;
    private FrameLayout layoutSend;
    private EditText inputMessage;
    private TextView textStatus;
    private static Boolean isReceiverAvailable = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discuss, container, false);

        CURRENT_CONVERSATION_ID = null;

        receiveData();

        init(view);

        setListeners();

        listenMessages();

        return view;
    }

    private void init(View view) {

        recyclerMessages = view.findViewById(R.id.recyclerMessages);
        layoutSend = view.findViewById(R.id.layoutSend);
        inputMessage = view.findViewById(R.id.inputMessage);
        textStatus = view.findViewById(R.id.statusText);

        ((FeedActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((FeedActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((FeedActivity) getActivity()).getSupportActionBar().setTitle(receivedUser.getName());
        ((FeedActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((FeedActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_btn_back);

        chatMessages = new ArrayList<>();

        messageAdapter = new MessageAdapter(
                getContext(),
                chatMessages,
                receivedUser
        );

        recyclerMessages.setAdapter(messageAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerMessages.setLayoutManager(manager);

    }

    private void receiveData() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            Log.i(TAG, "Data");
            Log.i(TAG, "SDK - " + Build.VERSION.SDK_INT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                receivedUser = (UserModel) bundle.getSerializable(KEY_CHOSEN_USER_DATA);
            }

        } else {
            Toast.makeText(getActivity(), "Can not find user", Toast.LENGTH_SHORT).show();
        }
    }

    private void setListeners() {
        layoutSend.setOnClickListener(v -> {
            if (!inputMessage.getText().toString().trim().equals(null)) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {

            HashMap<String, Object> message = new HashMap<>();
            message.put(Constants.KEY_SENDER_ID, DataBase.USER_MODEL.getUid());
            message.put(Constants.KEY_RECEIVER_ID, receivedUser.getUid());
            message.put(Constants.KEY_MESSAGE, inputMessage.getText().toString());
            message.put(Constants.KEY_TIME_STAMP, new Date());

            DataBase.sendMessage(message, new CompleteListener() {
                @Override
                public void OnSuccess() {
                    Log.i(TAG, "Created message from - " + DataBase.USER_MODEL.getUid() + " - to - " + receivedUser.getUid());

                    if (CURRENT_CONVERSATION_ID != null) {
                        updateConversation(inputMessage.getText().toString());
                    } else {
                        HashMap<String, Object> conversation = new HashMap<>();
                        conversation.put(KEY_SENDER_ID, USER_MODEL.getUid());
                        conversation.put(KEY_RECEIVER_ID, receivedUser.getUid());
                        conversation.put(KEY_LAST_MESSAGE, inputMessage.getText().toString());
                        conversation.put(KEY_TIME_STAMP, new Date());

                        DataBase.addConversation(conversation, new CompleteListener() {
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

                    try {
                        JSONObject data = new JSONObject();
                        data.put(KEY_USER_UID, USER_MODEL.getUid());
                        data.put(KEY_NAME, USER_MODEL.getName());
                        data.put(KEY_FCM_TOKEN, USER_MODEL.getFcmToken());
                        data.put(KEY_MESSAGE, inputMessage.getText().toString());

                        JSONArray tokens = new JSONArray();
                        tokens.put(receivedUser.getFcmToken());

                        JSONObject body = new JSONObject();
                        body.put(REMOTE_MESSAGE_DATA, data);
                        body.put(REMOTE_MESSAGE_REGISTRATION_IDS, tokens);

                        DataBase.sendNotification(body.toString(), new CompleteListener() {
                            @Override
                            public void OnSuccess() {
                                Log.i(TAG, "Notification successfully sent");
                            }

                            @Override
                            public void OnFailure() {
                                Log.i(TAG, "Can not send notification");

                            }
                        });

                    } catch (Exception e) {
                        Log.i(TAG, "Exception - " + e.getMessage());
                    }


                    inputMessage.setText(null);
                }

                @Override
                public void OnFailure() {
                    Log.i(TAG, "Can not send message");
                }
            });
    }

    private void listenMessages() {
        try {
            DATA_FIRESTORE.collection(KEY_COLLECTION_CHAT)
                    .whereEqualTo(KEY_SENDER_ID, DataBase.USER_MODEL.getUid())
                    .whereEqualTo(KEY_RECEIVER_ID, receivedUser.getUid())
                    .addSnapshotListener(eventListener);

            DATA_FIRESTORE.collection(KEY_COLLECTION_CHAT)
                    .whereEqualTo(KEY_SENDER_ID, receivedUser.getUid())
                    .whereEqualTo(KEY_RECEIVER_ID, DataBase.USER_MODEL.getUid())
                    .addSnapshotListener(eventListener);

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void listenAvailabilityOfReceiver() {
        DATA_FIRESTORE.collection(KEY_COLLECTION_USERS).document(
            receivedUser.getUid()
        ).addSnapshotListener((value, error) -> {
            if (value != null) {
                isReceiverAvailable = value.getBoolean(KEY_AVAILABILITY);
//                receivedUser.getFcmToken() = value.getString(KEY_FCM_TOKEN);

                if (isReceiverAvailable) {
                    textStatus.setText("Online");
                } else {
                    textStatus.setText("Offline");
                }
            }
        });
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
       try {

           Log.i(TAG, "Begin Listening");

           if (error != null) {
               Log.i(TAG, "Error - " + error.getMessage());
           }
           if (value != null) {
               int count = chatMessages.size();
               for (DocumentChange documentChange : value.getDocumentChanges()) {
                   if (documentChange.getType() == DocumentChange.Type.ADDED) {
                       ChatMessage chatMessage = new ChatMessage(
                               documentChange.getDocument().getString(KEY_SENDER_ID),
                               documentChange.getDocument().getString(KEY_RECEIVER_ID),
                               documentChange.getDocument().getString(KEY_MESSAGE),
                               documentChange.getDocument().getDate(KEY_TIME_STAMP)
                       );

                       chatMessages.add(chatMessage);
                       Log.i(TAG, "Message added");
                   }
               }

               Collections.sort(chatMessages, ChatMessage::compareTo);

               if (count == 0) {
                   messageAdapter.notifyDataSetChanged();
               } else {
                   messageAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                   recyclerMessages.smoothScrollToPosition(chatMessages.size() - 1);
               }
           }

           if (CURRENT_CONVERSATION_ID == null) {
               Log.i(TAG, "Conversation is null");
               checkForConversation();
           } else {
               Log.i(TAG, "Conversation is not null");
           }

       } catch (Exception e) {
           Log.i(TAG, "eventListener - " + e.getMessage());
       }
    };

    private void updateConversation(String message) {
        try {
            DocumentReference reference = DATA_FIRESTORE.collection(KEY_COLLECTION_CONVERSATION).document(CURRENT_CONVERSATION_ID);

            reference.update(
                    KEY_LAST_MESSAGE, message,
                    KEY_TIME_STAMP, new Date()
            );
        } catch (Exception e) {
            Log.i(TAG, "updateConversation - " + e.getMessage());
        }
    }

    private void checkForConversation() {
        try {
            if (chatMessages.size() != 0) {
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
            DocumentSnapshot document = task.getResult().getDocuments().get(0);
            CURRENT_CONVERSATION_ID = document.getId();

            Log.i(TAG, "ConversationID - " + CURRENT_CONVERSATION_ID);
        } else {
            Log.i(TAG, "conversationOnComplete error - " + task.getException());
        }
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            ((FeedActivity) getActivity()).setFragment(new CategoryFragment());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        listenAvailabilityOfReceiver();
    }
}
