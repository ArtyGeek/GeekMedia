package com.geekapps.geekmedia;

import android.content.Context;

public interface VideoPlayerFactory {

    VideoPlayer provideVideoPlayer(Context context);
}
