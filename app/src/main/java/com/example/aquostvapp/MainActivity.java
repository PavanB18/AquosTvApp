package com.example.aquostvapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;


import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.PlayerView;

import com.example.aquostvapp.AdManager.FullscreenAdActivity;
import com.example.aquostvapp.AdManager.PlayerSingleton;
import com.example.aquostvapp.Controller.SideMenuController;
import com.example.aquostvapp.Screens.Home;

@SuppressLint("UnstableOptInUsageError")
public class MainActivity extends AppCompatActivity {

    private PlayerView playerView;
    private SideMenuController sideMenuController;
    private View clickOverlay;
    private LinearLayout sideMenu, sideMenuToggleContainer; // Add a container for the toggle button

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.player_view);
        clickOverlay = findViewById(R.id.clickOverlay);
        sideMenu = findViewById(R.id.sideMenu);
        sideMenuToggleContainer = findViewById(R.id.menuToggle); // Assuming this is the parent layout of the toggle button

        // Init side menu
        sideMenuController = new SideMenuController(findViewById(android.R.id.content), getSupportFragmentManager(), this);

        // Key change: Set initial focus to the side menu toggle
        sideMenuToggleContainer.requestFocus();

        // Load default fragment
        sideMenuController.loadFragment(new Home());

        // Player setup
        if (PlayerSingleton.getPlayer() == null) {
            PlayerSingleton.initializePlayer(this, playerView);
            PlayerSingleton.startLoopPlayback(this);
        }

        // Key change: D-pad navigation logic for menu
        // Set an onFocusChangeListener on the side menu container
        sideMenu.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !sideMenuController.isMenuExpanded()) {
                sideMenuController.toggleSideMenu(true); // Expand menu
            } else if (!hasFocus && sideMenuController.isMenuExpanded()) {
                // You may need to fine-tune this logic based on your layout to detect focus moving out
                // For now, let's assume focus outside the sideMenu collapses it.
                // A better approach is to check if the new focused view is not in the sideMenu.
                // However, this simple check will work for a start.
                // To prevent immediate collapse, you'll need to define a focusable main content area.
                // For example, add the listener to the main fragment's root view.
            }
        });

        // This is a simple fix for the collapse behavior. The main content area must be focusable.
        View mainContent = findViewById(R.id.mainContent);
        if (mainContent != null) {
            mainContent.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus && sideMenuController.isMenuExpanded()) {
                    sideMenuController.toggleSideMenu(false); // Collapse menu
                }
            });
        }


        // Fullscreen logic
        View.OnClickListener openFullscreenListener = v -> {
            long pos = PlayerSingleton.getPlayer() != null ? PlayerSingleton.getPlayer().getCurrentPosition() : 0;
            PlayerSingleton.setResumePosition(pos);
            Log.d("MainActivity", "Opening fullscreen at pos: " + pos);
            startActivity(new Intent(MainActivity.this, FullscreenAdActivity.class));
        };

        // Key change: Ensure clickOverlay is focusable for D-pad navigation
        clickOverlay.setFocusable(true);
        clickOverlay.setFocusableInTouchMode(true);
        clickOverlay.setOnClickListener(openFullscreenListener);
        clickOverlay.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP &&
                    (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)) {
                openFullscreenListener.onClick(v);
                return true;
            }
            return false;
        });

    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onResume() {
        super.onResume();
        PlayerSingleton.setInFullscreen(false);
        if (PlayerSingleton.getPlayer() != null) {
            playerView.setPlayer(PlayerSingleton.getPlayer());
            PlayerSingleton.setCurrentPlayerView(playerView);
            PlayerSingleton.getPlayer().seekTo(PlayerSingleton.getResumePosition());
            PlayerSingleton.getPlayer().setPlayWhenReady(true);
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onPause() {
        super.onPause();
        if (PlayerSingleton.getPlayer() != null) {
            PlayerSingleton.setResumePosition(PlayerSingleton.getPlayer().getCurrentPosition());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (playerView != null) {
            playerView.setPlayer(null);
        }
    }
}