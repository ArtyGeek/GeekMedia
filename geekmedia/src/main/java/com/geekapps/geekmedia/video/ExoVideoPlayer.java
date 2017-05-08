package com.geekapps.geekmedia.video;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.TextureView;

import com.geekapps.geekmedia.BasePlayer;
import com.geekapps.geekmedia.ExoPlayerProgressObserverRunnable;
import com.geekapps.geekmedia.OnPlaybackChangeListener;
import com.geekapps.geekmedia.OnPreparedListener;
import com.geekapps.geekmedia.OnProgressChangeListener;
import com.geekapps.geekmedia.OnProgressChangeListenerDelegate;
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

public final class ExoVideoPlayer extends BasePlayer implements VideoPlayer {

    public static final String TAG = ExoVideoPlayer.class.getSimpleName();

    private static final Handler UI_HANDLER = new Handler(Looper.getMainLooper());

    private final Context mContext;
    private final SimpleExoPlayer mPlayer;
    private final DefaultDataSourceFactory mDataSourceFactory;
    private final ExtractorsFactory mExtractorsFactory;

    private final List<WeakReference<VideoPlayer.OnVideoPlayerSizeChangeListener>> mOnVideoPlayerSizeChangeListeners = new ArrayList<>();

    public static ExoVideoPlayer from(Context context) {
        return new ExoVideoPlayer(context);
    }

    private ExoVideoPlayer(@NonNull Context context) {
        mContext = context;
        mPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(mContext), new DefaultTrackSelector());
        mPlayer.addListener(this);
        mPlayer.setVideoListener(new ExoPlayerVideoListenerDelegate(mOnVideoPlayerSizeChangeListeners));
        mDataSourceFactory = new DefaultDataSourceFactory(mContext, DEFAULT_USER_AGENT);
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

    @Override
    public void setTexture(@NonNull TextureView texture) {
        mPlayer.setVideoTextureView(texture);
    }


    @Override
    public void addOnVideoPlayerSizeChangeListener(@NonNull VideoPlayer.OnVideoPlayerSizeChangeListener listener) {
        mOnVideoPlayerSizeChangeListeners.add(new WeakReference<>(listener));
    }

    @Override
    public void removeOnVideoPlayerSizeChangeListener(@NonNull VideoPlayer.OnVideoPlayerSizeChangeListener listener) {
        for (WeakReference<VideoPlayer.OnVideoPlayerSizeChangeListener> ref : mOnVideoPlayerSizeChangeListeners) {
            if (ref.get() == listener) {
                mOnVideoPlayerSizeChangeListeners.remove(ref);
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

            final VideoSize size = SimpleVideoSize.fromWidthHeightRatio(width, height, pixelWidthHeightRatio);
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

}
