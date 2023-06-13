package com.example.englishapp.presentation.fragments;

import static com.example.englishapp.data.database.Constants.KEY_AVAILABILITY;
import static com.example.englishapp.data.database.Constants.KEY_CHOSEN_USER_DATA;
import static com.example.englishapp.data.database.Constants.KEY_COLLECTION_CHAT;
import static com.example.englishapp.data.database.Constants.KEY_COLLECTION_USERS;
import static com.example.englishapp.data.database.Constants.KEY_FCM_TOKEN;
import static com.example.englishapp.data.database.Constants.KEY_MESSAGE;
import static com.example.englishapp.data.database.Constants.KEY_RECEIVER_ID;
import static com.example.englishapp.data.database.Constants.KEY_SENDER_ID;
import static com.example.englishapp.data.database.Constants.KEY_TIME_STAMP;
import static com.example.englishapp.data.database.DataBaseDiscussion.CURRENT_CONVERSATION_ID;
import static com.example.englishapp.data.database.DataBasePersonalData.DATA_FIRESTORE;
import static com.example.englishapp.data.database.DataBasePersonalData.USER_MODEL;
import static com.example.englishapp.domain.repositories.MessageRepository.CHAT_MESSAGES;
import static com.example.englishapp.domain.repositories.MessageRepository.receivedUser;

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
import com.example.englishapp.data.database.DataBasePersonalData;
import com.example.englishapp.data.models.ChatMessage;
import com.example.englishapp.data.models.UserModel;
import com.example.englishapp.domain.repositories.MessageRepository;
import com.example.englishapp.presentation.adapters.MessageAdapter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;

// https://github.com/ThaminduChankana/TrashCoinApp/tree/648db2e90091ecd39c8ae9502b92cf9c8b21b950

public class DiscussFragment extends Fragment {
    private static final String TAG = "FragmentDiscussion";
    private RecyclerView recyclerMessages;
    private MessageAdapter messageAdapter;
    private FrameLayout layoutSend;
    private EditText inputMessage;
    private TextView textStatus, noMessages;
    private static Boolean isReceiverAvailable = false;
    private MessageRepository messageRepository;
    private ListenerRegistration registrationUser, registrationReceiver;

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

    @Override
    public void onResume() {
        super.onResume();

        listenAvailabilityOfReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (registrationUser != null) registrationUser.remove();

        if (registrationReceiver != null) registrationReceiver.remove();
    }

    private void init(View view) {
        recyclerMessages = view.findViewById(R.id.recyclerMessages);
        layoutSend = view.findViewById(R.id.layoutSend);
        inputMessage = view.findViewById(R.id.inputMessage);
        textStatus = view.findViewById(R.id.statusText);
        TextView fcm = view.findViewById(R.id.fcm);
        noMessages = view.findViewById(R.id.noMessages);

        fcm.setText(USER_MODEL.getFcmToken() + "\n - " + receivedUser.getFcmToken());

        requireActivity().setTitle(receivedUser.getName());

        CHAT_MESSAGES.clear();

        messageAdapter = new MessageAdapter(
                CHAT_MESSAGES,
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
            Toast.makeText(getActivity(), getString(R.string.can_not_find_user), Toast.LENGTH_SHORT).show();
        }
    }

    private void setListeners() {
        layoutSend.setOnClickListener(v -> {
            if (inputMessage.getText().toString().trim().length() != 0) {

                messageRepository.sendMessage(inputMessage);

            } else {
                Toast.makeText(getActivity(), getString(R.string.write_text_please), Toast.LENGTH_SHORT).show();
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

            Log.i(TAG, "listen messages - " + DataBasePersonalData.USER_MODEL.getUid() + " - " + receivedUser.getUid());

            registrationUser = DATA_FIRESTORE.collection(KEY_COLLECTION_CHAT)
                .whereEqualTo(KEY_SENDER_ID, DataBasePersonalData.USER_MODEL.getUid())
                .whereEqualTo(KEY_RECEIVER_ID, receivedUser.getUid())
                .addSnapshotListener(eventListener);

            registrationReceiver = DATA_FIRESTORE.collection(KEY_COLLECTION_CHAT)
                .whereEqualTo(KEY_SENDER_ID, receivedUser.getUid())
                .whereEqualTo(KEY_RECEIVER_ID, DataBasePersonalData.USER_MODEL.getUid())
                .addSnapshotListener(eventListener);

        } catch (Exception e) {
            Log.i(TAG, "error - " + e.getMessage());
        }
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        try {

            Log.i(TAG, "Begin Listening");

            if (error != null) {
                Log.i(TAG, "Error - " + error.getMessage());
            }

            if (value != null) {

                Log.i(TAG, "changes - " + value.getDocumentChanges());

                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    Log.i(TAG, "change - " + documentChange.getType() + " text - " + documentChange.getDocument().getString(KEY_MESSAGE));
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        ChatMessage chatMessage = new ChatMessage(
                                documentChange.getDocument().getString(KEY_SENDER_ID),
                                documentChange.getDocument().getString(KEY_RECEIVER_ID),
                                documentChange.getDocument().getString(KEY_MESSAGE),
                                documentChange.getDocument().getDate(KEY_TIME_STAMP)
                        );

                        noMessages.setVisibility(View.GONE);

                        CHAT_MESSAGES.add(chatMessage);

                        Log.i(TAG, "Message added - " + chatMessage.getMessage());
                    }
                }

                Collections.sort(CHAT_MESSAGES, ChatMessage::compareTo);

                if (CHAT_MESSAGES.size() == 0) {
                    messageAdapter.notifyDataSetChanged();
                } else {
                    Log.i(TAG, "range - " + CHAT_MESSAGES.size());

                    messageAdapter.notifyItemRangeInserted(CHAT_MESSAGES.size(), CHAT_MESSAGES.size());
                    recyclerMessages.smoothScrollToPosition(CHAT_MESSAGES.size() - 1);
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
}
