package com.geekapps.geekmedia;

import android.os.Handler;
import android.os.Looper;

import com.geekapps.geekmedia.video.VideoPlayer;

public final class ExoPlayerProgressObserverRunnable implements Runnable {

    private static final Handler UI_HANDLER = new Handler(Looper.getMainLooper());

    private final Player mPlayer;
    private final OnProgressChangeListener mListener;

    public ExoPlayerProgressObserverRunnable(Player player, OnProgressChangeListener listener) {
        mPlayer = player;
        mListener = listener;
    }

    @Override
    public void run() {
        if (mPlayer.isPlaying()) {
            UI_HANDLER.post(new TickRunnable());
        }
    }

    private class TickRunnable implements Runnable {

        @Override
        public void run() {
            mListener.onProgressChanged(mPlayer.getCurrentPosition());
        }
    }
}
