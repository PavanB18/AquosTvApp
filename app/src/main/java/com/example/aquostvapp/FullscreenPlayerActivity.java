package com.example.aquostvapp;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.PlayerView;

public class FullscreenPlayerActivity extends AppCompatActivity {

    private PlayerView fullScreenPlayerView;
    private Button backButton, detailsButton;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_player);

        fullScreenPlayerView = findViewById(R.id.fullscreen_player_view);
        backButton = findViewById(R.id.back_button);
        detailsButton = findViewById(R.id.details_button);

        fullScreenPlayerView.setPlayer(PlayerSingleton.getPlayer());

        PlayerSingleton.getPlayer().play();

        backButton.setOnClickListener(v -> finish()); // Return to MainActivity
        detailsButton.setOnClickListener(v -> {
            // TODO: Launch detail screen if needed
        });
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onPause() {
        super.onPause();
        PlayerSingleton.setResumePosition(PlayerSingleton.getPlayer().getCurrentPosition());
        PlayerSingleton.getPlayer().pause();
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onResume() {
        super.onResume();
        PlayerSingleton.getPlayer().seekTo(PlayerSingleton.getResumePosition());
        PlayerSingleton.getPlayer().play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Do not release player here — let MainActivity handle it
    }
}

