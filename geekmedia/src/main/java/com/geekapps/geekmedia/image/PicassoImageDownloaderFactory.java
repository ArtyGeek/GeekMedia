package com.geekapps.geekmedia.image;

import android.content.Context;

public final class PicassoImageDownloaderFactory  implements ImageDownloaderFactory {

    @Override
    public ImageDownloader provideImageDownloader(Context context) {
        return PicassoImageDownloader.from(context);
    }
}
