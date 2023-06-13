package com.example.englishapp.presentation.fragments;

import static com.example.englishapp.data.database.Constants.KEY_CHOSEN_USER_DATA;
import static com.example.englishapp.data.database.Constants.KEY_COLLECTION_CONVERSATION;
import static com.example.englishapp.data.database.Constants.KEY_LAST_MESSAGE;
import static com.example.englishapp.data.database.Constants.KEY_RECEIVER_ID;
import static com.example.englishapp.data.database.Constants.KEY_SENDER_ID;
import static com.example.englishapp.data.database.Constants.KEY_TIME_STAMP;
import static com.example.englishapp.data.database.DataBasePersonalData.DATA_FIRESTORE;
import static com.example.englishapp.data.database.DataBasePersonalData.USER_MODEL;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.data.models.ChatMessage;
import com.example.englishapp.data.models.UserModel;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.domain.interfaces.ConversationListener;
import com.example.englishapp.domain.repositories.LoginRepository;
import com.example.englishapp.presentation.activities.MainActivity;
import com.example.englishapp.presentation.adapters.RecentConversationAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;

public class ChatFragment extends BaseFragment implements ConversationListener {
    private static final String TAG = "FragmentChat";
    private RecyclerView recyclerRecentlyChats;
    private RecentConversationAdapter conversationAdapter;
    private final ArrayList recentChats = new ArrayList<>();
    private FloatingActionButton fab;
    private TextView noChats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        init(view);

        setListeners();

        listenConversations();

        return view;
    }

    private void init(View view) {
        requireActivity().setTitle(R.string.nameChats);

        recyclerRecentlyChats = view.findViewById(R.id.recyclerRecentlyChats);
        fab = view.findViewById(R.id.fab);
        noChats = view.findViewById(R.id.noChats);

        recentChats.clear();

        conversationAdapter = new RecentConversationAdapter(
                recentChats,
                ChatFragment.this
        );

        recyclerRecentlyChats.setAdapter(conversationAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerRecentlyChats.setLayoutManager(manager);

        new LoginRepository().getToken(new CompleteListener() {
            @Override
            public void OnSuccess() {
                Log.i(TAG, "Token successfully got " + USER_MODEL.getFcmToken());
            }

            @Override
            public void OnFailure() {
                Log.i(TAG, "Can not get token");

            }
        });
    }

    private void setListeners() {
        fab.setOnClickListener(v -> ((MainActivity) requireActivity()).setFragment(new MapUsersFragment(), false));
    }

    private void listenConversations() {
        DATA_FIRESTORE.collection(KEY_COLLECTION_CONVERSATION)
            .whereEqualTo(KEY_SENDER_ID, USER_MODEL.getUid())
            .addSnapshotListener(eventListener);

        DATA_FIRESTORE.collection(KEY_COLLECTION_CONVERSATION)
                .whereEqualTo(KEY_RECEIVER_ID, USER_MODEL.getUid())
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = new EventListener<>() {

        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            try {

                if (error != null) {
                    Log.i(TAG, "Error is - " + error.getMessage());
                }

                if (value != null) {
                    for (DocumentChange document : value.getDocumentChanges()) {
                        if (document.getType() == DocumentChange.Type.ADDED) {
                            ChatMessage chatMessage = new ChatMessage(
                                document.getDocument().getString(KEY_SENDER_ID),
                                document.getDocument().getString(KEY_RECEIVER_ID),
                                document.getDocument().getString(KEY_LAST_MESSAGE),
                                document.getDocument().getDate(KEY_TIME_STAMP)
                            );

                            noChats.setVisibility(View.GONE);

                            recentChats.add(chatMessage);

                        } else if (document.getType() == DocumentChange.Type.MODIFIED) {
                            for (int i = 0; i < recentChats.size(); i++) {
                                if (((ChatMessage) recentChats.get(i)).getSenderId().equals(document.getDocument().getString(KEY_SENDER_ID)) && ((ChatMessage) recentChats.get(i)).getReceiverId().equals(document.getDocument().getString(KEY_RECEIVER_ID))) {
                                    ((ChatMessage) recentChats.get(i)).message = document.getDocument().getString(KEY_LAST_MESSAGE);
                                    ((ChatMessage) recentChats.get(i)).dateTime = document.getDocument().getDate(KEY_TIME_STAMP);

                                    Log.i(TAG, "message was found");

                                }
                            }
                        }
                    }

//                Collections.sort(recentChats, ChatMessage::compareTo);
                recentChats.sort((Comparator<ChatMessage>) (o1, o2) -> o2.dateTime.compareTo(o1.dateTime));

                    conversationAdapter.notifyDataSetChanged();
                    recyclerRecentlyChats.smoothScrollToPosition(0);

                    Log.i(TAG, "All okey");

                }
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    };

    @Override
    public void onConversationClicked(UserModel userModel) {
        Log.i(TAG, "USER - " + userModel.getName());

        if (!userModel.getUid().equals(USER_MODEL.getUid())) {

            Bundle bundle = new Bundle();
            bundle.putSerializable(KEY_CHOSEN_USER_DATA, userModel);
            DiscussFragment fragment = new DiscussFragment();
            fragment.setArguments(bundle);

            ((MainActivity) requireActivity()).setFragment(fragment, false);

        } else {
            Toast.makeText(getActivity(), getString(R.string.it_s_you), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefresh() {
        ((MainActivity) requireActivity()).setFragment(new ChatFragment(), true);
    }
}
