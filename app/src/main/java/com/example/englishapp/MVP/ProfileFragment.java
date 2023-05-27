package com.example.englishapp.MVP;

import static com.example.englishapp.MVP.DataBase.USER_MODEL;

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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.englishapp.Authentication.MainAuthenticationActivity;
import com.example.englishapp.Authentication.ProfileInfoFragment;
import com.example.englishapp.R;
import com.example.englishapp.testsAndWords.BookmarksFragment;
import com.example.englishapp.testsAndWords.LeaderBordFragment;
import com.example.englishapp.testsAndWords.RoomDataBase;
import com.example.englishapp.testsAndWords.SpeechFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {
    private static final String TAG = "FragmentProfile";
    private Toolbar toolbar;
    private ImageView imgUser;
    private TextView userPlace, userScore;
    private LinearLayout layoutBookmark, layoutLeaderBord, layoutProfile, layoutLogout;
    private List<WordModel> words = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        getWords();

        init(view);

        setListeners();

        return view;
    }

    private void setListeners() {

        layoutBookmark.setOnClickListener(v -> {
            if (USER_MODEL.getBookmarksCount() > 0) {
                DataBase.loadBookmarkIds(new CompleteListener() {
                    @Override
                    public void OnSuccess() {

                        Log.i(TAG, "loaded bookmark ids - " + DataBase.LIST_OF_BOOKMARK_IDS.size());

                        DataBase.loadBookmarks(new CompleteListener() {
                            @Override
                            public void OnSuccess() {

                                Log.i(TAG, "bookmarks loaded - " + DataBase.LIST_OF_BOOKMARKS.size());

                                ((MainActivity) getActivity()).setFragment(new BookmarksFragment());

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

        layoutLeaderBord.setOnClickListener(v -> ((MainActivity) getActivity()).setFragment(new LeaderBordFragment()));

        layoutProfile.setOnClickListener(v -> ((MainActivity) getActivity()).setFragment(new ProfileInfoFragment()));

        layoutLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            GoogleSignInClient mClient = GoogleSignIn.getClient(getActivity(), gso);
            mClient.signOut().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "You have successfully logout", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), MainAuthenticationActivity.class);

                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK
                    );

                    startActivity(intent);
                    getActivity().finish();

                } else {
                    Log.i(TAG, "Error - " + task.getException().getMessage());
                    Toast.makeText(getActivity(), "Can not logout... Please, try later", Toast.LENGTH_SHORT).show();
                }
            });
        });

        toolbar.setNavigationOnClickListener(v -> {

            getActivity().onBackPressed();

        });

    }

    private void init(View view) {

        toolbar = view.findViewById(R.id.toolbar);
        imgUser = view.findViewById(R.id.userImage);
        userPlace = view.findViewById(R.id.userPlace);
        userScore = view.findViewById(R.id.userScore);
        layoutBookmark = view.findViewById(R.id.layoutBookmark);
        layoutLeaderBord = view.findViewById(R.id.layoutLeaderBord);
        layoutProfile = view.findViewById(R.id.layoutProfile);
        layoutLogout = view.findViewById(R.id.layoutLogout);

        ((MainActivity) getActivity()).getSupportActionBar().hide();
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_btn_back);
        ((MainActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);

        // set user's data
        ((MainActivity) getActivity()).setTitle(USER_MODEL.getName());

        userPlace.setText("" + USER_MODEL.getPlace());
        userScore.setText("" + USER_MODEL.getScore());

        Glide.with(ProfileFragment.this).load(USER_MODEL.getPathToImage()).into(imgUser);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, new SpeechFragment())
                .commit();

        TextView view1 = view.findViewById(R.id.learning);

        int count = RoomDataBase.
                getDatabase(getContext())
                .roomDao()
                .getRowCount();

        view1.setText(" - " + count);

        Log.i(TAG, "beginning");

        loadEn();

    }

    private void loadEn() {

        Log.i(TAG, "begin");

        // Create an English-German translator:
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.GERMAN)
                        .build();

        Log.i(TAG, "begin 2");

        final Translator englishGermanTranslator =
                Translation.getClient(options);

        Log.i(TAG, "begin 3");

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        Log.i(TAG, "begin 4");

        englishGermanTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener(unused -> {
                Log.i(TAG, "begin 5");

                englishGermanTranslator.translate("hello").addOnSuccessListener(s -> Log.i(TAG, "translated - " + s))
                .addOnFailureListener(e -> Log.i(TAG, "can not translate - " + e.getMessage()));

            })
            .addOnFailureListener(e -> Log.i(TAG, "can not install - " + e.getMessage()));
    }

    private void getWords() {

        words = RoomDataBase.
                getDatabase(getContext())
                .roomDao()
                .getAllWords();
    }

}