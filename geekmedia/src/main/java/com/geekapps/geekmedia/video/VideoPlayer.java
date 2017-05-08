package com.geekapps.geekmedia.video;

import android.support.annotation.NonNull;
import android.view.TextureView;

import com.geekapps.geekmedia.OnPlaybackChangeListener;
import com.geekapps.geekmedia.OnPreparedListener;
import com.geekapps.geekmedia.OnProgressChangeListener;
import com.geekapps.geekmedia.Player;

public interface VideoPlayer extends Player {

    interface OnVideoPlayerSizeChangeListener {
        void onVideoSizeChanged(VideoSize videoSize);
    }

    void setTexture(@NonNull TextureView texture);

    void addOnVideoPlayerSizeChangeListener(@NonNull OnVideoPlayerSizeChangeListener listener);

    void removeOnVideoPlayerSizeChangeListener(@NonNull OnVideoPlayerSizeChangeListener listener);
}
