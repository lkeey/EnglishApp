package com.example.englishapp.fragments;

import static com.example.englishapp.database.Constants.KEY_AVAILABILITY;
import static com.example.englishapp.database.Constants.KEY_CHOSEN_USER_DATA;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_CHAT;
import static com.example.englishapp.database.Constants.KEY_COLLECTION_USERS;
import static com.example.englishapp.database.Constants.KEY_FCM_TOKEN;
import static com.example.englishapp.database.Constants.KEY_MESSAGE;
import static com.example.englishapp.database.Constants.KEY_RECEIVER_ID;
import static com.example.englishapp.database.Constants.KEY_SENDER_ID;
import static com.example.englishapp.database.Constants.KEY_TIME_STAMP;
import static com.example.englishapp.database.DataBaseDiscussion.CURRENT_CONVERSATION_ID;
import static com.example.englishapp.database.DataBasePersonalData.DATA_FIRESTORE;
import static com.example.englishapp.repositories.MessageRepository.chatMessages;
import static com.example.englishapp.repositories.MessageRepository.receivedUser;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.adapters.MessageAdapter;
import com.example.englishapp.database.DataBasePersonalData;
import com.example.englishapp.models.ChatMessage;
import com.example.englishapp.models.UserModel;
import com.example.englishapp.repositories.MessageRepository;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

// https://github.com/ThaminduChankana/TrashCoinApp/tree/648db2e90091ecd39c8ae9502b92cf9c8b21b950

public class DiscussFragment extends Fragment {
    private static final String TAG = "FragmentDiscussion";
    private RecyclerView recyclerMessages;
    private MessageAdapter messageAdapter;
    private FrameLayout layoutSend;
    private EditText inputMessage;
    private TextView textStatus;
    private static Boolean isReceiverAvailable = false;
    private MessageRepository messageRepository;

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

        requireActivity().setTitle(receivedUser.getName());

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

        CURRENT_CONVERSATION_ID = null;

        Bundle bundle = getArguments();

        messageRepository = new MessageRepository();

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
            Log.i(TAG, String.valueOf(inputMessage.getText().toString().trim().length()));

            if (inputMessage.getText().toString().trim().length() != 0) {

                messageRepository.sendMessage(inputMessage);

            } else {
                Toast.makeText(getActivity(), "Write text, please", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listenAvailabilityOfReceiver() {
        DATA_FIRESTORE.collection(KEY_COLLECTION_USERS).document(
            receivedUser.getUid()
        ).addSnapshotListener((value, error) -> {
            if (value != null) {
                isReceiverAvailable = value.getBoolean(KEY_AVAILABILITY);
                receivedUser.setFcmToken(value.getString(KEY_FCM_TOKEN));

                if (isReceiverAvailable) {
                    textStatus.setText(R.string.online);
                } else {
                    textStatus.setText(R.string.offline);
                }
            }
        });
    }

    private void listenMessages() {
        try {
            DATA_FIRESTORE.collection(KEY_COLLECTION_CHAT)
                    .whereEqualTo(KEY_SENDER_ID, DataBasePersonalData.USER_MODEL.getUid())
                    .whereEqualTo(KEY_RECEIVER_ID, receivedUser.getUid())
                    .addSnapshotListener(eventListener);

            DATA_FIRESTORE.collection(KEY_COLLECTION_CHAT)
                    .whereEqualTo(KEY_SENDER_ID, receivedUser.getUid())
                    .whereEqualTo(KEY_RECEIVER_ID, DataBasePersonalData.USER_MODEL.getUid())
                    .addSnapshotListener(eventListener);

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
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
                        Log.i(TAG, "Message added - " + chatMessage.getMessage());
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

                messageRepository.checkForConversation();

            } else {
                Log.i(TAG, "Conversation is not null");
            }

        } catch (Exception e) {
            Log.i(TAG, "eventListener - " + e.getMessage());
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        listenAvailabilityOfReceiver();
    }

}
