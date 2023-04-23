package com.example.englishapp.MVP;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.englishapp.R;
import com.example.englishapp.chat.ChatFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class FeedActivity extends AppCompatActivity {

    private static final String TAG = "ActivityFeed";
    private AppBarConfiguration mAppBarConfiguration;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout mainFrame;
    private TextView drawerProfileName, drawerProfileText;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_feed);

            Log.i(TAG, "Success");

            init();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_feed);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bottomNavigationView = findViewById(R.id.bottomNavBar);
        mainFrame = findViewById(R.id.nav_host_fragment_content_feed);

        onNavigationItemSelectedListener = item -> {

            switch (item.getItemId()) {
                case R.id.nav_home_menu:
//                        setFragment(new CategoryFragment());
                    return true;

                case R.id.nav_leader_menu:
                        setFragment(new ChatFragment());
                    return true;

                case R.id.nav_account_menu:
//                        setFragment(new AccountFragment());
                    return true;
            }
            return false;
        };

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        // Find our drawer view
        drawerLayout = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.toolbar), R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );

        // Setup toggle to display hamburger icon with nice animation
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        // Tie DrawerLayout events to the ActionBarToggle

        drawerLayout.addDrawerListener(toggle);

        NavigationView navigationView = findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

        // Set name in navigation bar
        drawerProfileName = navigationView.getHeaderView(0).findViewById(R.id.navDrawerImg);
        drawerProfileText = navigationView.getHeaderView(0).findViewById(R.id.navDrawerTxt);

        String name = DataBase.USER_MODEL.getName();

        drawerProfileText.setText(name);

        drawerProfileName.setText(name.toUpperCase().substring(0, 1));

        navigationView.getMenu().findItem(R.id.nav_home).setOnMenuItemClickListener(item -> {
            Toast.makeText(this, "CHAT", Toast.LENGTH_SHORT).show();
            setFragment(new ChatFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
            drawerLayout.closeDrawers();
            return true;
        });

        navigationView.getMenu().findItem(R.id.nav_leader).setOnMenuItemClickListener(item -> {
//                setFragment(new LeaderBordFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_leader);
            drawerLayout.closeDrawers();
            return true;
        });

        navigationView.getMenu().findItem(R.id.nav_account).setOnMenuItemClickListener(item -> {
//            setFragment(new ChatFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_account);
            drawerLayout.closeDrawers();
            return true;
        });
    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(mainFrame.getId(), fragment)
                .commit();
    }
}