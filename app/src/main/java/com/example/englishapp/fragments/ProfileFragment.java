package com.example.englishapp.fragments;

import static com.example.englishapp.database.DataBasePersonalData.USER_MODEL;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.englishapp.R;
import com.example.englishapp.activities.MainActivity;
import com.example.englishapp.activities.MainAuthenticationActivity;
import com.example.englishapp.database.DataBaseBookmarks;
import com.example.englishapp.database.DataBaseLearningWords;
import com.example.englishapp.database.RoomDataBase;
import com.example.englishapp.interfaces.CompleteListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

// https://stackoverflow.com/questions/41276361/expanding-and-collapsing-toolbar-in-android

public class ProfileFragment extends Fragment {
    private static final String TAG = "FragmentProfile";
    private Toolbar toolbar;
    private LinearLayout layoutBookmark, layoutLeaderBord, layoutProfile, layoutLogout;
    private DataBaseLearningWords dataBaseLearningWords;
    private float avatarAnimateStartPointY = 0F;
    private float avatarCollapseAnimationChangeWeight = 0F;
    private boolean isCalculated = false;
    private float verticalToolbarAvatarMargin = 0F;
    private AppBarLayout appBarLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        dataBaseLearningWords = new DataBaseLearningWords();

        getWords();

        init(view);

        setImg(view);

        setListeners();

        return view;
    }

    private void setImg(View view) {
        try {

            if (!isCalculated) {
                Log.i(TAG, "setImg: 01 - " + appBarLayout.getTotalScrollRange());

                avatarAnimateStartPointY = Math.abs((appBarLayout.getHeight() - (135 + 24)) / appBarLayout.getTotalScrollRange());
                Log.i(TAG, "setImg: 02");

                avatarCollapseAnimationChangeWeight = 1 / (1 - avatarAnimateStartPointY);
                Log.i(TAG, "setImg: 03");

                verticalToolbarAvatarMargin = (toolbar.getHeight() - 45) * 2;
                isCalculated = true;

                Log.i(TAG, "setImg: 04");

            }

            appBarLayout.addOnOffsetChangedListener((AppBarLayout.BaseOnOffsetChangedListener) (appBarLayout1, verticalOffset) -> {

                Log.i(TAG, "setImg: 1 - " + appBarLayout1.getTotalScrollRange());

                float offset = Math.abs(verticalOffset / appBarLayout1.getTotalScrollRange());


                updateViews(view, offset);
            });

        } catch (Exception e) {
            Log.i(TAG, "er - " + e.getMessage());
        }
    }

    private void updateViews(View view, float offset) {

        /* Collapse avatar img*/
        ImageView ivUserAvatar = view.findViewById(R.id.userImage);

        if (offset > avatarAnimateStartPointY) {
            float avatarCollapseAnimateOffset = (offset - avatarAnimateStartPointY) * avatarCollapseAnimationChangeWeight;
            float avatarSize = 135 - (135 - 45) * avatarCollapseAnimateOffset;
            ViewGroup.LayoutParams layoutParams = ivUserAvatar.getLayoutParams();
            layoutParams.height = Math.round(avatarSize);
            layoutParams.width = Math.round(avatarSize);

            TextView invisibleTextViewWorkAround = view.findViewById(R.id.tv_workaround);
            invisibleTextViewWorkAround.setTextSize(TypedValue.COMPLEX_UNIT_PX, offset);

            float translationX = ((appBarLayout.getWidth() - 24 - avatarSize) / 2) * avatarCollapseAnimateOffset;
            float translationY = ((toolbar.getHeight()  - verticalToolbarAvatarMargin - avatarSize ) / 2) * avatarCollapseAnimateOffset;
            ivUserAvatar.setTranslationX(translationX);
            ivUserAvatar.setTranslationY(translationY);
        } else {
            ViewGroup.LayoutParams layoutParams = ivUserAvatar.getLayoutParams();
            if (layoutParams.height != 135) {
                layoutParams.height = 135;
                layoutParams.width = 135;
                ivUserAvatar.setLayoutParams(layoutParams);
            }
            ivUserAvatar.setTranslationX(0f);
        }
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

        layoutProfile.setOnClickListener(v -> ((MainActivity) requireActivity()).setFragment(new ProfileInfoFragment()));

        layoutLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            GoogleSignInClient mClient = GoogleSignIn.getClient(requireActivity(), gso);
            mClient.signOut().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
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
        appBarLayout = view.findViewById(R.id.app_bar_layout);

        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).hide();
        ((MainActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(true);
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_btn_back);
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setHomeButtonEnabled(true);

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



}