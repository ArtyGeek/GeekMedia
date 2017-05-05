package com.geekapps.geekmedia.video;

import android.content.Context;

public final class ExoPlayerVideoFactory implements VideoPlayerFactory {

    @Override
    public VideoPlayer provideVideoPlayer(Context context) {
        return new ExoVideoPlayer(context);
    }
}
