package com.example.aquostvapp.AdManager;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.aquostvapp.R;

@UnstableApi
public class FullscreenAdActivity extends AppCompatActivity {

    private PlayerView fullscreenPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_fullscreen_ad);

        PlayerSingleton.setInFullscreen(true);

        fullscreenPlayerView = findViewById(R.id.fullscreenPlayerView);
        fullscreenPlayerView.setUseController(true);

        // Tell PlayerSingleton we are now using fullscreen player view
        PlayerSingleton.setCurrentPlayerView(fullscreenPlayerView);

        ExoPlayer player = PlayerSingleton.getPlayer();
        if (player != null) {
            fullscreenPlayerView.setPlayer(player);
            player.seekTo(PlayerSingleton.getResumePosition());
            player.setPlayWhenReady(true);

            // ✅ Add listener to detect when playback ends
            player.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == Player.STATE_ENDED) {
                        finish(); // Return to home screen
                    }
                }
            });
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnDetail).setOnClickListener(v ->
                Toast.makeText(this, "Ad Detail clicked!", Toast.LENGTH_SHORT).show()
        );
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (PlayerSingleton.getPlayer() != null) {
            long pos = PlayerSingleton.getPlayer().getCurrentPosition();
            PlayerSingleton.setResumePosition(pos);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fullscreenPlayerView != null) {
            fullscreenPlayerView.setPlayer(null);
        }
        PlayerSingleton.setInFullscreen(false);
    }
}
