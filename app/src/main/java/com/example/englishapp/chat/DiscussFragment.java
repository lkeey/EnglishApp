package com.example.englishapp.chat;

import static com.example.englishapp.MVP.DataBase.DATA_FIRESTORE;
import static com.example.englishapp.MVP.DataBase.USER_MODEL;
import static com.example.englishapp.messaging.Constants.KEY_AVAILABILITY;
import static com.example.englishapp.messaging.Constants.KEY_CHOSEN_USER_DATA;
import static com.example.englishapp.messaging.Constants.KEY_COLLECTION_CHAT;
import static com.example.englishapp.messaging.Constants.KEY_COLLECTION_CONVERSATION;
import static com.example.englishapp.messaging.Constants.KEY_COLLECTION_USERS;
import static com.example.englishapp.messaging.Constants.KEY_LAST_MESSAGE;
import static com.example.englishapp.messaging.Constants.KEY_MESSAGE;
import static com.example.englishapp.messaging.Constants.KEY_RECEIVER_ID;
import static com.example.englishapp.messaging.Constants.KEY_SENDER_ID;
import static com.example.englishapp.messaging.Constants.KEY_TIME_STAMP;

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
import com.example.englishapp.MVP.DataBase;
import com.example.englishapp.MVP.FeedActivity;
import com.example.englishapp.MVP.UserModel;
import com.example.englishapp.R;
import com.example.englishapp.messaging.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private String conversationId = null;
    private FrameLayout layoutSend;
    private EditText inputMessage;
    private TextView textStatus;
    private static Boolean isReceiverAvailable = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discuss, container, false);

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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                receivedUser = (UserModel) bundle.getSerializable(KEY_CHOSEN_USER_DATA);
            }

            Toast.makeText(getActivity(), "User - " + receivedUser.getName(), Toast.LENGTH_SHORT).show();

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
        try {
            HashMap<String, Object> message = new HashMap<>();
            message.put(Constants.KEY_SENDER_ID, DataBase.USER_MODEL.getUid());
            message.put(Constants.KEY_RECEIVER_ID, receivedUser.getUid());
            message.put(Constants.KEY_MESSAGE, inputMessage.getText().toString());
            message.put(Constants.KEY_TIME_STAMP, new Date());

            DATA_FIRESTORE.collection(KEY_COLLECTION_CHAT).add(message);

            Log.i(TAG, "Created message from - " + DataBase.USER_MODEL.getUid() + " - to - " + receivedUser.getUid());

            if (conversationId != null) {
                updateConversation(inputMessage.getText().toString());
            } else {
                HashMap<String, Object> conversation = new HashMap<>();
                conversation.put(KEY_SENDER_ID, USER_MODEL.getUid());
                conversation.put(KEY_RECEIVER_ID, receivedUser.getUid());
                conversation.put(KEY_LAST_MESSAGE, inputMessage.getText().toString());
                conversation.put(KEY_TIME_STAMP, new Date());

                addConversation(conversation);
            }

            inputMessage.setText(null);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
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

                if (isReceiverAvailable) {
                    textStatus.setText("Online");
                } else {
                    textStatus.setText("Offline");
                }

            }
        });
    }

    private final com.google.firebase.firestore.EventListener<QuerySnapshot> eventListener = (value, error) -> {
       if (error != null) {
           return ;
       } if (value != null) {
           int count = chatMessages.size();
           for (DocumentChange documentChange: value.getDocumentChanges()) {
               if(documentChange.getType() == DocumentChange.Type.ADDED) {
                   ChatMessage chatMessage = new ChatMessage(
                           documentChange.getDocument().getString(KEY_SENDER_ID),
                           documentChange.getDocument().getString(KEY_RECEIVER_ID),
                           documentChange.getDocument().getString(KEY_MESSAGE),
                           documentChange.getDocument().getDate(KEY_TIME_STAMP)
                   );

                   chatMessages.add(chatMessage);
               }
           }

            Collections.sort(chatMessages, ChatMessage::compareTo);

           if(count == 0) {
                messageAdapter.notifyDataSetChanged();
           } else {
               messageAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
               recyclerMessages.smoothScrollToPosition(chatMessages.size() - 1);
           }
        }

       if (conversationId == null) {
           checkForConversation();
       }
    };

    private void addConversation(HashMap<String, Object> conversation) {
        DATA_FIRESTORE.collection(KEY_COLLECTION_CONVERSATION)
                .add(conversation)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        conversationId = documentReference.getId();
                    }
                });
    }

    private void updateConversation(String message) {
        DocumentReference reference = DATA_FIRESTORE.collection(KEY_COLLECTION_CONVERSATION).document(conversationId);

        reference.update(
                KEY_LAST_MESSAGE, message,
                KEY_TIME_STAMP, new Date()
        );
    }

    private void checkForConversation() {
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
    }

    private void checkForConversationRemotely(String senderId, String receiverId) {
        DATA_FIRESTORE.collection(KEY_COLLECTION_CONVERSATION)
                .whereEqualTo(KEY_SENDER_ID, senderId)
                .whereEqualTo(KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversationOnComplete);
    }

    private final OnCompleteListener<QuerySnapshot> conversationOnComplete = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
            DocumentSnapshot document = task.getResult().getDocuments().get(0);
            conversationId = document.getId();
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
