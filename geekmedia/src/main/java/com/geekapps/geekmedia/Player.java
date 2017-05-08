package com.geekapps.geekmedia;

import android.net.Uri;
import android.support.annotation.NonNull;

public interface Player {

    void play(@NonNull String source);

    void play(@NonNull Uri uri);

    void resume();

    void pause();

    void stop();

    void release();

    boolean isPlaying();

    int getDuration();

    boolean isLooping();

    void setLooping(boolean isLooping);

    void mute();

    void unMute();

    boolean isMuted();

    int getCurrentPosition();

    void seekTo(int position);

    void addOnPlayerStateChangeListener(@NonNull OnPlaybackChangeListener listener);

    void removeOnPlayerStateChangeListener(@NonNull OnPlaybackChangeListener listener);

    void addOnProgressChangeListener(@NonNull OnProgressChangeListener listener);

    void removeOnProgressChangeListener(@NonNull OnProgressChangeListener listener);

    void addOnPreparedListener(@NonNull OnPreparedListener listener);

    void removeOnPreparedListener(@NonNull OnPreparedListener listener);
}
