package com.example.aquostvapp.AdManager;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.ima.ImaAdsLoader;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.ui.PlayerView;

@UnstableApi
public class PlayerSingleton {

    private static ExoPlayer player;
    private static ImaAdsLoader adsLoader;
    private static long resumePosition = 0;
    private static boolean isInFullscreen = false;
    private static boolean isAdPlaying = false;
    private static boolean adPlayedThisCycle = false;

    // VAST Ad Tag
    private static final String VAST_AD_TAG =
            "https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/single_ad_samples&sz=640x480&cust_params=sample_ct%3Dredirectlinear&ciu_szs=300x250%2C728x90&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&correlator=";

    // Sample Content Video
    private static final Uri CONTENT_URI =
            Uri.parse("https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4");

    private static PlayerView currentPlayerView;

    private static final Player.Listener playbackListener = new Player.Listener() {
        @Override
        public void onPlaybackStateChanged(int playbackState) {
            if (playbackState == Player.STATE_ENDED) {
                Log.d("PlayerSingleton", "Playback ended. isInFullscreen=" + isInFullscreen);

                if (isInFullscreen && !isAdPlaying &&
                        currentPlayerView != null &&
                        currentPlayerView.getContext() instanceof FullscreenAdActivity) {
                    ((FullscreenAdActivity) currentPlayerView.getContext()).finish();
                }

                // --- FIX: This is the corrected logic ---
                // The loop should restart when the playback ends, regardless of whether it's an ad or content.
                if (currentPlayerView != null && !isInFullscreen) {
                    adPlayedThisCycle = false;
                    startAdThenContent(currentPlayerView.getContext());
                }
            }
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            if (player != null) {
                isAdPlaying = player.isPlayingAd();
            }
        }
    };

    public static void initializePlayer(Context context, PlayerView playerView) {
        currentPlayerView = playerView;

        if (adsLoader == null) {
            adsLoader = new ImaAdsLoader.Builder(context).build();
        }

        if (player == null) {
            DefaultDataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(context);
            DefaultMediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(dataSourceFactory)
                    .setLocalAdInsertionComponents(adTagUri -> adsLoader, playerView);

            player = new ExoPlayer.Builder(context)
                    .setMediaSourceFactory(mediaSourceFactory)
                    .build();

            adsLoader.setPlayer(player);
            player.addListener(playbackListener);
        }

        playerView.setPlayer(player);
        playerView.setUseController(false);
    }

    public static void startLoopPlayback(Context context) {
        startAdThenContent(context);
    }

    private static void startAdThenContent(Context context) {
        if (player == null) return;

        if (!adPlayedThisCycle) {
            // First time in the cycle → load ad
            Uri adTagUri = Uri.parse(VAST_AD_TAG + System.currentTimeMillis());
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(CONTENT_URI)
                    .setAdsConfiguration(new MediaItem.AdsConfiguration.Builder(adTagUri).build())
                    .build();
            player.setMediaItem(mediaItem);
            adPlayedThisCycle = true;
        } else {
            // Ad already played → only play content
            player.setMediaItem(MediaItem.fromUri(CONTENT_URI));
        }

        player.prepare();
        player.setPlayWhenReady(true);
    }

    public static ExoPlayer getPlayer() {
        return player;
    }

    public static void setResumePosition(long position) {
        resumePosition = position;
    }

    public static long getResumePosition() {
        return resumePosition;
    }

    public static void setInFullscreen(boolean fullscreen) {
        isInFullscreen = fullscreen;
    }

    // NEW: Switch between mini-player and fullscreen views
    public static void setCurrentPlayerView(PlayerView view) {
        currentPlayerView = view;
    }
}