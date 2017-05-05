package com.geekapps.geekmedia;

import android.content.Context;

public class PicassoImageDownloaderFactory  implements ImageDownloaderFactory {

    @Override
    public ImageDownloader provideImageDownloader(Context context) {
        return PicassoImageDownloader.from(context);
    }
}
