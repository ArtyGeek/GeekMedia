package com.geekapps.geekmedia.video;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.TextureView;

import com.geekapps.geekmedia.OnPlaybackChangeListener;
import com.geekapps.geekmedia.OnPreparedListener;
import com.geekapps.geekmedia.OnProgressChangeListener;

public interface VideoPlayer {

    interface OnVideoPlayerSizeChangeListener {
        void onVideoSizeChanged(VideoSize videoSize);
    }

    void play(@NonNull String source);

    void play(@NonNull Uri uri);

    void resume();

    void pause();

    void stop();

    void release();

    void setTexture(@NonNull TextureView texture);

    boolean isPlaying();

    int getDuration();

    boolean isReady();

    boolean isLooping();

    void mute();

    void unMute();

    boolean isMuted();

    void setLooping(boolean isLooping);

    int getCurrentPosition();

    void seekTo(int position);

    void addOnPlayerStateChangeListener(@NonNull OnPlaybackChangeListener listener);

    void removeOnPlayerStateChangeListener(@NonNull OnPlaybackChangeListener listener);

    void addOnProgressChangeListener(@NonNull OnProgressChangeListener listener);

    void removeOnProgressChangeListener(@NonNull OnProgressChangeListener listener);

    void addOnPreparedListener(@NonNull OnPreparedListener listener);

    void removeOnPreparedListener(@NonNull OnPreparedListener listener);

    void addOnVideoPlayerSizeChangeListener(@NonNull OnVideoPlayerSizeChangeListener listener);

    void removeOnVideoPlayerSizeChangeListener(@NonNull OnVideoPlayerSizeChangeListener listener);
}
