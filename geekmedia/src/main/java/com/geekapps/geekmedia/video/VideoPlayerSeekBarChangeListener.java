package com.geekapps.geekmedia.video;

import android.widget.SeekBar;

import java.util.concurrent.atomic.AtomicBoolean;

public final class VideoPlayerSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

    private final VideoPlayer mPlayer;
    private final AtomicBoolean mDraggingFlag;

    public VideoPlayerSeekBarChangeListener(VideoPlayer player, AtomicBoolean draggingFlag) {
        mPlayer = player;
        mDraggingFlag = draggingFlag;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mDraggingFlag.set(true);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mDraggingFlag.set(false);
        mPlayer.seekTo(seekBar.getProgress());
    }
}
