package com.geekapps.geekmedia.image;

import android.content.Context;

public interface ImageDownloaderFactory {

    ImageDownloader provideImageDownloader(Context context);
}
