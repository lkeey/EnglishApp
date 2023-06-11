package com.example.englishapp.presentation.fragments;

import static android.content.Context.ALARM_SERVICE;
import static com.example.englishapp.data.database.Constants.KEY_SHOW_NOTIFICATION_WORD;
import static com.example.englishapp.data.database.DataBasePersonalData.USER_MODEL;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.englishapp.R;
import com.example.englishapp.data.database.DataBaseBookmarks;
import com.example.englishapp.data.database.DataBaseLearningWords;
import com.example.englishapp.data.database.RoomDataBase;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.domain.receivers.AlarmReceiver;
import com.example.englishapp.presentation.activities.MainActivity;
import com.example.englishapp.presentation.activities.MainAuthenticationActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ProfileFragment extends BaseFragment {
    private static final String TAG = "FragmentProfile";
    private Toolbar toolbar;
    private LinearLayout layoutBookmark, layoutLeaderBord, layoutProfile, layoutLogout;
    private DataBaseLearningWords dataBaseLearningWords;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        dataBaseLearningWords = new DataBaseLearningWords();

        getWords();

        init(view);

        setListeners();

        return view;
    }

    private void setListeners() {

        layoutBookmark.setOnClickListener(v -> {
            if (USER_MODEL.getBookmarksCount() > 0) {
                new DataBaseBookmarks().loadBookmarkIds(new CompleteListener() {
                    @Override
                    public void OnSuccess() {

                        Log.i(TAG, "loaded bookmark ids - " + DataBaseBookmarks.LIST_OF_BOOKMARK_IDS.size());

                        new DataBaseBookmarks().loadBookmarks(new CompleteListener() {
                            @Override
                            public void OnSuccess() {

                                Log.i(TAG, "bookmarks loaded - " + DataBaseBookmarks.LIST_OF_BOOKMARKS.size());

                                ((MainActivity) requireActivity()).setFragment(new BookmarksFragment());

                                Log.i(TAG, "set fragment");
                            }

                            @Override
                            public void OnFailure() {
                                Log.i(TAG, "error occurred");

                                Toast.makeText(getActivity(), "Try Later", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                    @Override
                    public void OnFailure() {

                        Log.i(TAG, "error occurred");

                        Toast.makeText(getActivity(), "Try Later", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "You haven't bookmarks", Toast.LENGTH_SHORT).show();
            }
        });

        layoutLeaderBord.setOnClickListener(v -> ((MainActivity) requireActivity()).setFragment(new LeaderBordFragment()));

        layoutProfile.setOnClickListener(v -> ((MainActivity) requireActivity()).setFragment(new ProfileInfoFragment(), false));

        layoutLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            GoogleSignInClient mClient = GoogleSignIn.getClient(requireActivity(), gso);
            mClient.signOut().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {

                    AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(ALARM_SERVICE);

                    Intent alarm = new Intent(getContext(), AlarmReceiver.class);
                    alarm.putExtra(KEY_SHOW_NOTIFICATION_WORD, true);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 1, alarm, PendingIntent.FLAG_MUTABLE);

                    // cancel previous
                    alarmManager.cancel(pendingIntent);

                    Toast.makeText(getActivity(), "You have successfully logout", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), MainAuthenticationActivity.class);

                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK
                    );

                    startActivity(intent);
                    requireActivity().finish();

                } else {
                    Log.i(TAG, "Error - " + Objects.requireNonNull(task.getException()).getMessage());
                    Toast.makeText(getActivity(), "Can not logout... Please, try later", Toast.LENGTH_SHORT).show();
                }
            });
        });

        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

    }

    private void init(View view) {

        toolbar = view.findViewById(R.id.toolbar);
        ImageView imgUser = view.findViewById(R.id.userImage);
        TextView userPlace = view.findViewById(R.id.userPlace);
        TextView userScore = view.findViewById(R.id.userScore);
        layoutBookmark = view.findViewById(R.id.layoutBookmark);
        layoutLeaderBord = view.findViewById(R.id.layoutLeaderBord);
        layoutProfile = view.findViewById(R.id.layoutProfile);
        layoutLogout = view.findViewById(R.id.layoutLogout);

        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).hide();
        ((MainActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(true);

        // set user's data
        requireActivity().setTitle(USER_MODEL.getName());

        userPlace.setText(String.valueOf(USER_MODEL.getPlace()));
        userScore.setText(String.valueOf(USER_MODEL.getScore()));

        Glide.with(ProfileFragment.this).load(USER_MODEL.getPathToImage()).into(imgUser);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayoutSpeech, new SpeechFragment())
                .commit();

        TextView view1 = view.findViewById(R.id.learning);

        int count = RoomDataBase.
                getDatabase(getContext())
                .roomDao()
                .getRowCount();

        view1.setText(" - " + count + " - " + DataBaseLearningWords.LIST_OF_LEARNING_WORDS.size());

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayoutLearningWords, new LearningWordsFragment())
                .commit();

    }

    private void getWords() {

        dataBaseLearningWords.loadLearningWords(getContext(), new CompleteListener() {
            @Override
            public void OnSuccess() {
                Log.i(TAG, "size - " + DataBaseLearningWords.LIST_OF_LEARNING_WORDS.size());
            }

            @Override
            public void OnFailure() {
                Log.i(TAG, "can not load");
            }
        });

    }

    @Override
    public void onRefresh() {
        ((MainActivity) requireActivity()).setFragment(new ProfileFragment(), true);
    }
}