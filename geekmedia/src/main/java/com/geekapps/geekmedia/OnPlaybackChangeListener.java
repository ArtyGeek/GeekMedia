package com.geekapps.geekmedia;

import com.geekapps.geekmedia.audio.PlaybackState;

public interface OnPlaybackChangeListener {
    void onPlaybackChanged(PlaybackState state);
}
