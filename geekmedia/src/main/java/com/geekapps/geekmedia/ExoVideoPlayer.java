package com.geekapps.geekmedia;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.TextureView;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ExoVideoPlayer implements SimpleExoPlayer.EventListener, ExtractorMediaSource.EventListener {

    public interface OnPlayerStateChangeListener {
        void onPlayerStateChanged(boolean playWhenReady, int playbackState);
    }

    public interface OnProgressChangeListener {
        void onProgressChanged(int progress);
    }

    public interface OnPreparedListener {
        void onPrepared();
    }

    public interface OnVideoPlayerSizeChangeListener {
        void onVideoSizeChanged(VideoSize videoSize);
    }

    public static final String TAG = ExoVideoPlayer.class.getSimpleName();

    private static final Handler UI_HANDLER = new Handler(Looper.getMainLooper());
    private final ScheduledExecutorService mObserverExecutor = Executors.newSingleThreadScheduledExecutor();
    private final Context mContext;
    private final SimpleExoPlayer mPlayer;
    private final DefaultDataSourceFactory mDataSourceFactory;
    private final ExtractorsFactory mExtractorsFactory;

    private final List<WeakReference<OnPlayerStateChangeListener>> mOnPlayerStateChangeListeners = new ArrayList<>();
    private final List<WeakReference<OnProgressChangeListener>> mOnProgressChangeListeners = new ArrayList<>();
    private final OnProgressChangeListener mOnProgressChangeListenerDelegate = new OnProgressChangeListenerDelegate(mOnProgressChangeListeners);
    private final List<WeakReference<OnPreparedListener>> mOnPreparedListeners = new ArrayList<>();
    private final List<WeakReference<OnVideoPlayerSizeChangeListener>> mOnVideoPlayerSizeChangeListeners = new ArrayList<>();

    private ScheduledFuture<?> mObserverFuture;
    private volatile boolean mIsLooping = false;

    public ExoVideoPlayer(@NonNull Context context) {
        mContext = context;
        mPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(mContext), new DefaultTrackSelector());
        mPlayer.addListener(this);
        mPlayer.setVideoListener(new ExoPlayerVideoListenerDelegate(mOnVideoPlayerSizeChangeListeners));
        mDataSourceFactory = new DefaultDataSourceFactory(mContext, "HelloWorld");
        mExtractorsFactory = new DefaultExtractorsFactory();
    }

    public void play(@NonNull final String source) {
        Uri uri = Uri.parse(source);
        play(uri);
    }

    public void play(@NonNull final Uri uri) {
        mPlayer.prepare(buildMediaSource(uri));
        mPlayer.setPlayWhenReady(true);
    }

    public void resume() {
        mPlayer.setPlayWhenReady(true);
    }

    public void pause() {
        mPlayer.setPlayWhenReady(false);
    }

    public void stop() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
        }
    }

    public void setTexture(@NonNull TextureView texture) {
        mPlayer.setVideoTextureView(texture);
    }

    public boolean isPlaying() {
        return mPlayer.getPlayWhenReady();
    }

    public int getDuration() {
        return (int) mPlayer.getDuration();
    }

    public boolean isReady() {
        return mPlayer != null;
    }

    public boolean isLooping() {
        return mIsLooping;
    }

    public void mute() {
        mPlayer.setVolume(0f);
    }

    public void unMute() {
        mPlayer.setVolume(1f);
    }

    public boolean isMuted() {
        return mPlayer.getVolume() == 0f;
    }

    public void setLooping(boolean isLooping) {
        mIsLooping = isLooping;
    }

    public int getCurrentPosition() {
        return (int) mPlayer.getCurrentPosition();
    }

    public void seekTo(int position) {
        mPlayer.seekTo(position);
    }

    public void addOnPlayerStateChangeListener(@NonNull OnPlayerStateChangeListener listener) {
        mOnPlayerStateChangeListeners.add(new WeakReference<>(listener));
    }

    public void removeOnPlayerStateChangeListener(@NonNull OnPlayerStateChangeListener listener) {
        for (WeakReference<OnPlayerStateChangeListener> ref : mOnPlayerStateChangeListeners) {
            if (ref.get() == listener) {
                mOnPlayerStateChangeListeners.remove(ref);
            }
        }
    }

    public void addOnProgressChangeListener(@NonNull OnProgressChangeListener listener) {
        mOnProgressChangeListeners.add(new WeakReference<>(listener));
    }

    public void removeOnProgressChangeListener(@NonNull OnProgressChangeListener listener) {
        for (WeakReference<OnProgressChangeListener> ref : mOnProgressChangeListeners) {
            if (ref.get() == listener) {
                mOnProgressChangeListeners.remove(ref);
            }
        }
    }

    public void addOnPreparedListener(@NonNull OnPreparedListener listener) {
        mOnPreparedListeners.add(new WeakReference<>(listener));
    }

    public void removeOnPreparedListener(@NonNull OnPreparedListener listener) {
        for (WeakReference<OnPreparedListener> ref : mOnPreparedListeners) {
            if (ref.get() == listener) {
                mOnPreparedListeners.remove(ref);
            }
        }
    }

    public void addOnVideoPlayerSizeChangeListener(@NonNull OnVideoPlayerSizeChangeListener listener) {
        mOnVideoPlayerSizeChangeListeners.add(new WeakReference<>(listener));
    }

    public void removeOnVideoPlayerSizeChangeListener(@NonNull OnVideoPlayerSizeChangeListener listener) {
        for (WeakReference<OnVideoPlayerSizeChangeListener> ref : mOnVideoPlayerSizeChangeListeners) {
            if (ref.get() == listener) {
                mOnVideoPlayerSizeChangeListeners.remove(ref);
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
        notifyPlayerStateChanged(playWhenReady, playbackState);
        if (playWhenReady) {
            if (mObserverFuture == null || mObserverFuture.isDone()) {
                mObserverFuture = mObserverExecutor.schedule(buildObserverRunnable(), 50, TimeUnit.MILLISECONDS);
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

    @Override
    public void onLoadError(IOException error) {
        //TODO:
    }

    private MediaSource buildMediaSource(Uri uri) {
        MediaSource source = new ExtractorMediaSource(uri, mDataSourceFactory, mExtractorsFactory, UI_HANDLER, this);
        if (mIsLooping) {
            source = new LoopingMediaSource(source);
        }
        return source;
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

    private void notifyPlayerStateChanged(final boolean playWhenReady, final int playbackState) {
        for (final WeakReference<OnPlayerStateChangeListener> ref : mOnPlayerStateChangeListeners) {
            if (ref.get() != null) {
                UI_HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        ref.get().onPlayerStateChanged(playWhenReady, playbackState);
                    }
                });
            }
        }
    }

    private static class OnProgressChangeListenerDelegate implements OnProgressChangeListener {

        private final List<WeakReference<OnProgressChangeListener>> mListeners;

        OnProgressChangeListenerDelegate(List<WeakReference<OnProgressChangeListener>> listeners) {
            mListeners = listeners;
        }

        @Override
        public void onProgressChanged(int progress) {
            for (WeakReference<OnProgressChangeListener> ref : mListeners) {
                if (ref.get() != null) {
                    ref.get().onProgressChanged(progress);
                }
            }
        }
    }

    private static class ExoPlayerVideoListenerDelegate implements SimpleExoPlayer.VideoListener {

        private final List<WeakReference<OnVideoPlayerSizeChangeListener>> mOnVideoPlayerSizeChangeListeners;

        ExoPlayerVideoListenerDelegate(List<WeakReference<OnVideoPlayerSizeChangeListener>> onVideoPlayerSizeChangeListeners) {
            mOnVideoPlayerSizeChangeListeners = onVideoPlayerSizeChangeListeners;
        }

        @Override
        public void onVideoSizeChanged(final int width,
                                       final int height,
                                       final int unappliedRotationDegrees,
                                       final float pixelWidthHeightRatio) {

            final VideoSize size = SimpleVideoSize.from(width, height, pixelWidthHeightRatio);
            for (final WeakReference<OnVideoPlayerSizeChangeListener> ref : mOnVideoPlayerSizeChangeListeners) {
                if (ref.get() != null) {
                    UI_HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            ref.get().onVideoSizeChanged(size);
                        }
                    });
                }
            }
        }

        @Override
        public void onRenderedFirstFrame() {

        }
    }

    private static class ExoPlayerProgressObserverRunnable implements Runnable {

        private final ExoVideoPlayer mPlayer;
        private final ExoVideoPlayer.OnProgressChangeListener mListener;

        ExoPlayerProgressObserverRunnable(ExoVideoPlayer exoPlayer, ExoVideoPlayer.OnProgressChangeListener listener) {
            mPlayer = exoPlayer;
            mListener = listener;
        }

        @Override
        public void run() {
            if (mPlayer.isPlaying()) {
                UI_HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onProgressChanged(mPlayer.getCurrentPosition());
                    }
                });
            }
        }
    }
}
