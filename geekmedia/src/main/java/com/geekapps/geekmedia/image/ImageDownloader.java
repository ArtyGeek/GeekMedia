package com.geekapps.geekmedia.image;

import android.support.annotation.DrawableRes;
import android.widget.ImageView;

public interface ImageDownloader {

    void loadImage(String source, @DrawableRes int placeholderRes, ImageView target);

}
