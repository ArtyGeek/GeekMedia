package com.geekapps.geekmedia;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.geekapps.geekmedia.audio.PlaybackState;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class BasePlayer implements Player, ExoPlayer.EventListener {

    protected static final String DEFAULT_USER_AGENT = "MEDIA_PLAYER";
    protected static final Handler UI_HANDLER = new Handler(Looper.getMainLooper());

    private final ScheduledExecutorService mObserverExecutor = Executors.newSingleThreadScheduledExecutor();

    private final List<WeakReference<OnPlaybackChangeListener>> mOnPlaybackChangeListeners = new ArrayList<>();
    private final List<WeakReference<OnProgressChangeListener>> mOnProgressChangeListeners = new ArrayList<>();
    private final OnProgressChangeListener mOnProgressChangeListenerDelegate = new OnProgressChangeListenerDelegate(mOnProgressChangeListeners);
    private final List<WeakReference<OnPreparedListener>> mOnPreparedListeners = new ArrayList<>();

    private volatile boolean mIsLooping = false;
    private ScheduledFuture<?> mObserverFuture;

    protected abstract SimpleExoPlayer getPlayer();

    protected abstract MediaSource buildMediaSource(Uri uri);

    @Override
    public void play(@NonNull final String source) {
        Uri uri = Uri.parse(source);
        play(uri);
    }

    @Override
    public void play(@NonNull final Uri uri) {
        getPlayer().prepare(buildMediaSource(uri));
        getPlayer().setPlayWhenReady(true);
    }

    @Override
    public void resume() {
        getPlayer().setPlayWhenReady(true);
    }

    @Override
    public void pause() {
        getPlayer().setPlayWhenReady(false);
    }

    @Override
    public void stop() {
        getPlayer().stop();
    }

    @Override
    public void release() {
        getPlayer().release();
    }

    @Override
    public boolean isPlaying() {
        return getPlayer().getPlayWhenReady();
    }

    @Override
    public int getDuration() {
        return (int) getPlayer().getDuration();
    }

    @Override
    public boolean isLooping() {
        return mIsLooping;
    }

    @Override
    public void mute() {
        getPlayer().setVolume(0f);
    }

    @Override
    public void unMute() {
        getPlayer().setVolume(1f);
    }

    @Override
    public boolean isMuted() {
        return getPlayer().getVolume() == 0f;
    }

    @Override
    public void setLooping(boolean isLooping) {
        mIsLooping = isLooping;
    }

    @Override
    public int getCurrentPosition() {
        return (int) getPlayer().getCurrentPosition();
    }

    @Override
    public void seekTo(int position) {
        getPlayer().seekTo(position);
    }

    @Override
    public void addOnPlayerStateChangeListener(@NonNull OnPlaybackChangeListener listener) {
        mOnPlaybackChangeListeners.add(new WeakReference<>(listener));
    }

    @Override
    public void removeOnPlayerStateChangeListener(@NonNull OnPlaybackChangeListener listener) {
        for (WeakReference<OnPlaybackChangeListener> ref : mOnPlaybackChangeListeners) {
            if (ref.get() == listener) {
                mOnPlaybackChangeListeners.remove(ref);
            }
        }
    }

    @Override
    public void addOnProgressChangeListener(@NonNull OnProgressChangeListener listener) {
        mOnProgressChangeListeners.add(new WeakReference<>(listener));
    }

    @Override
    public void removeOnProgressChangeListener(@NonNull OnProgressChangeListener listener) {
        for (WeakReference<OnProgressChangeListener> ref : mOnProgressChangeListeners) {
            if (ref.get() == listener) {
                mOnProgressChangeListeners.remove(ref);
            }
        }
    }

    @Override
    public void addOnPreparedListener(@NonNull OnPreparedListener listener) {
        mOnPreparedListeners.add(new WeakReference<>(listener));
    }

    @Override
    public void removeOnPreparedListener(@NonNull OnPreparedListener listener) {
        for (WeakReference<OnPreparedListener> ref : mOnPreparedListeners) {
            if (ref.get() == listener) {
                mOnPreparedListeners.remove(ref);
            }
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState != ExoPlayer.STATE_ENDED) {
            notifyPlayerStateChanged(playWhenReady ? PlaybackState.PLAY : PlaybackState.PAUSE);
        } else {
            notifyPlayerStateChanged(PlaybackState.STOP);
        }

        if (playWhenReady) {
            if (mObserverFuture == null || mObserverFuture.isDone()) {
                mObserverFuture = mObserverExecutor.scheduleAtFixedRate(buildObserverRunnable(), 50, 50, TimeUnit.MILLISECONDS);
            }
        } else {
            if (mObserverFuture != null && !mObserverFuture.isDone()) {
                mObserverFuture.cancel(false);
                mObserverFuture = null;
            }
        }

        if (playbackState == ExoPlayer.STATE_READY) {
            notifyPlayerPrepared();
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    private Runnable buildObserverRunnable() {
        return new ExoPlayerProgressObserverRunnable(this, mOnProgressChangeListenerDelegate);
    }

    private void notifyPlayerPrepared() {
        for (final WeakReference<OnPreparedListener> ref : mOnPreparedListeners) {
            if (ref.get() != null) {
                UI_HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        ref.get().onPrepared();
                    }
                });
            }
        }
    }

    private void notifyPlayerStateChanged(final PlaybackState state) {
        for (final WeakReference<OnPlaybackChangeListener> ref : mOnPlaybackChangeListeners) {
            if (ref.get() != null) {
                UI_HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        ref.get().onPlaybackChanged(state);
                    }
                });
            }
        }
    }
}
