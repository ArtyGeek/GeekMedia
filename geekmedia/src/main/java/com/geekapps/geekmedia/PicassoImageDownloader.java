package com.geekapps.geekmedia;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public final class PicassoImageDownloader implements ImageDownloader {

    private final Picasso mPicasso;

    public static PicassoImageDownloader from(Context context) {
        return new PicassoImageDownloader(context);
    }

    private PicassoImageDownloader(Context context) {
        mPicasso = Picasso.with(context);
    }

    @Override
    public void loadImage(String source, @DrawableRes int placeholderRes, ImageView target) {
        mPicasso.load(source).placeholder(placeholderRes).into(target);
    }
}
