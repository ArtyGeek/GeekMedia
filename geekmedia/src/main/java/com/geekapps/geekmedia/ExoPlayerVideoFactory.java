package com.geekapps.geekmedia;

import android.content.Context;

public class ExoPlayerVideoFactory implements VideoPlayerFactory {

    @Override
    public VideoPlayer provideVideoPlayer(Context context) {
        return new ExoVideoPlayer(context);
    }
}
