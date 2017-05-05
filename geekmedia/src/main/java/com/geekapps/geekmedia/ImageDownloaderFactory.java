package com.geekapps.geekmedia;

import android.content.Context;

public interface ImageDownloaderFactory {

    ImageDownloader provideImageDownloader(Context context);
}
