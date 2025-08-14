package com.example.aquostvapp;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
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

    private static final String VAST_AD_TAG =
            "https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/single_ad_samples&sz=640x480&cust_params=sample_ct%3Dredirectlinear&ciu_szs=300x250%2C728x90&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&correlator=";

    private static PlayerView currentPlayerView;

    public static void initializePlayer(Context context, PlayerView playerView) {
        currentPlayerView = playerView;

        if (adsLoader == null) {
            adsLoader = new ImaAdsLoader.Builder(context).build();
        }

        if (player == null) {
            Log.d("PlayerSingleton", "Initializing player");

            DefaultDataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(context);
            DefaultMediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(dataSourceFactory)
                    .setLocalAdInsertionComponents(
                            adTagUri -> adsLoader,
                            playerView
                    );

            player = new ExoPlayer.Builder(context)
                    .setMediaSourceFactory(mediaSourceFactory)
                    .build();

            adsLoader.setPlayer(player);

            player.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == Player.STATE_ENDED) {
                        Log.d("PlayerSingleton", "Ad finished. Restarting...");
                        new Handler(Looper.getMainLooper()).post(() -> restartAd(context));
                    }
                }
            });
        }

        playerView.setPlayer(player);
        playerView.setUseController(false);

        new Handler(Looper.getMainLooper()).post(() -> startAdPlayback(context));
    }

    private static void startAdPlayback(Context context) {
        // Ad-only playback (no content URL)
        Uri adTagUri = Uri.parse(VAST_AD_TAG + System.currentTimeMillis());

        MediaItem mediaItem = new MediaItem.Builder()
                .setUri("https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4") // Dummy content (required but never played)
                .setAdsConfiguration(new MediaItem.AdsConfiguration.Builder(adTagUri).build())
                .build();

        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);
    }

    private static void restartAd(Context context) {
        Log.d("PlayerSingleton", "Replaying same ad...");
        player.stop();
        startAdPlayback(context);
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

    public static void release() {
        if (adsLoader != null) {
            adsLoader.setPlayer(null);
            adsLoader.release();
            adsLoader = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }

        resumePosition = 0;
    }
}