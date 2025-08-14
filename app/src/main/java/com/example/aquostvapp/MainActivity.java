// MainActivity.java
package com.example.aquostvapp;

import static android.os.Build.VERSION.SDK_INT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.media3.common.Player;
import androidx.media3.ui.PlayerView;

@SuppressLint("UnsafeOptInUsageError")
public class MainActivity extends AppCompatActivity {

    private PlayerView playerView;
    private FrameLayout adContainer;

    // Side Menu UI elements
    private LinearLayout sideMenu, menuToggle, menuHome, menuSettings, menuFavorite, menuWatchList;
    private TextView labelHome, labelSettings, labelFavorite, labelWatchList, toggleLabel;
    private boolean isMenuExpanded = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "onCreate called");
        adContainer = findViewById(R.id.adContainer);
        playerView = findViewById(R.id.player_view);

        // All UI initialization happens here
        initViews();
        setupMenuClicks();
        setupExpandCollapseLogic();

        loadFragment(new HomeFragment()); // Default screen
        playerView.setOnClickListener(v -> {
            PlayerSingleton.setResumePosition(PlayerSingleton.getPlayer().getCurrentPosition());
            Intent intent = new Intent(MainActivity.this, FullscreenPlayerActivity.class);
            startActivity(intent);
        });



    }

    private void initViews() {
        playerView = findViewById(R.id.player_view);
        playerView.setClickable(true);
        playerView.setFocusable(true);
        playerView.setFocusableInTouchMode(true);
        playerView.bringToFront(); // Just in case overlapping views are intercepting clicks

        playerView.setUseController(false);

        sideMenu = findViewById(R.id.sideMenu);
        menuToggle = findViewById(R.id.menuToggle);

        menuHome = findViewById(R.id.menuHome);
        menuSettings = findViewById(R.id.menuSettings);
        menuFavorite = findViewById(R.id.menuFavorite);
        menuWatchList = findViewById(R.id.menuWatchList);

        toggleLabel = findViewById(R.id.toggleLabel);
        labelHome = findViewById(R.id.labelHome);
        labelSettings = findViewById(R.id.labelSettings);
        labelFavorite = findViewById(R.id.labelFavorite);
        labelWatchList = findViewById(R.id.labelWatchList);
    }

    private void setupMenuClicks() {
        menuHome.setOnClickListener(v -> loadFragment(new HomeFragment()));
        menuSettings.setOnClickListener(v -> loadFragment(new SettingsFragment()));
        menuFavorite.setOnClickListener(v -> loadFragment(new FavoriteFragment()));
        menuWatchList.setOnClickListener(v -> loadFragment(new WatchListFragment()));
    }

    private void setupExpandCollapseLogic() {
        menuToggle.setOnClickListener(v -> toggleSideMenu());
    }

    private void toggleSideMenu() {
        isMenuExpanded = !isMenuExpanded;
        int newWidth = isMenuExpanded ? dpToPx(200) : dpToPx(64);
        ViewGroup.LayoutParams params = sideMenu.getLayoutParams();
        params.width = newWidth;
        sideMenu.setLayoutParams(params);

        toggleLabel.setVisibility(isMenuExpanded ? View.VISIBLE : View.GONE);
        labelHome.setVisibility(isMenuExpanded ? View.VISIBLE : View.GONE);
        labelSettings.setVisibility(isMenuExpanded ? View.VISIBLE : View.GONE);
        labelFavorite.setVisibility(isMenuExpanded ? View.VISIBLE : View.GONE);
        labelWatchList.setVisibility(isMenuExpanded ? View.VISIBLE : View.GONE);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainContent, fragment);
        transaction.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("MainActivity", "onStart called");
        if (SDK_INT > 23) {
            PlayerSingleton.initializePlayer(this, playerView); // Pass both context and playerView
            playerView.setPlayer(PlayerSingleton.getPlayer());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("MainActivity", "onResume called");
        if (SDK_INT <= 23 || PlayerSingleton.getPlayer() == null) {
            PlayerSingleton.initializePlayer(this, playerView);
        }
        if (PlayerSingleton.getPlayer() != null) {
            playerView.setPlayer(PlayerSingleton.getPlayer());
            PlayerSingleton.getPlayer().seekTo(PlayerSingleton.getResumePosition());
            PlayerSingleton.getPlayer().play();  // <- Ensure resume always calls play()
            Log.d("MainActivity", "Player resumed");
        } else {
            Log.d("MainActivity", "Player is null on resume");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MainActivity", "onPause called");
        if (PlayerSingleton.getPlayer() != null) {
            PlayerSingleton.setResumePosition(PlayerSingleton.getPlayer().getCurrentPosition());
            PlayerSingleton.getPlayer().pause();
            Log.d("MainActivity", "Player paused");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("MainActivity", "onStop called");
        if (playerView != null) {
            playerView.setPlayer(null); // Detach the player from the view
        }
        if (SDK_INT > 23) {
            PlayerSingleton.release();
            Log.d("MainActivity", "Player released on stop");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (SDK_INT <= 23) {
            PlayerSingleton.release();
        }
    }
}