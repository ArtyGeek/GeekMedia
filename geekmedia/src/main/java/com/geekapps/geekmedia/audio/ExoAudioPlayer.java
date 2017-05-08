package com.geekapps.geekmedia.audio;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.geekapps.geekmedia.BasePlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

public final class ExoAudioPlayer extends BasePlayer {

    private final SimpleExoPlayer mPlayer;
    private final DefaultDataSourceFactory mDataSourceFactory;
    private final ExtractorsFactory mExtractorsFactory;

    public static ExoAudioPlayer from(Context context) {
        return new ExoAudioPlayer(context);
    }

    private ExoAudioPlayer(@NonNull Context context) {
        mPlayer = ExoPlayerFactory.newSimpleInstance(new AudioRenderersFactory(context), new DefaultTrackSelector());
        mPlayer.addListener(this);
        mDataSourceFactory = new DefaultDataSourceFactory(context, DEFAULT_USER_AGENT);
        mExtractorsFactory = new DefaultExtractorsFactory();
    }

    @Override
    protected SimpleExoPlayer getPlayer() {
        return mPlayer;
    }

    @Override
    protected MediaSource buildMediaSource(Uri uri) {
        MediaSource source = new ExtractorMediaSource(uri, mDataSourceFactory, mExtractorsFactory, UI_HANDLER, null);
        if (isLooping()) {
            source = new LoopingMediaSource(source);
        }
        return source;
    }
}
