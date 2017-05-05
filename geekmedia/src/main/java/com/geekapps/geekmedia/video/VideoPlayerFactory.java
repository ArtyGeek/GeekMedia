package com.geekapps.geekmedia.video;

import android.content.Context;

public interface VideoPlayerFactory {

    VideoPlayer provideVideoPlayer(Context context);
}
