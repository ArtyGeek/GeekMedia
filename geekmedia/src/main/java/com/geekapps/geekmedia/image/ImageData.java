package com.geekapps.geekmedia.image;

import android.support.annotation.NonNull;

import java.io.Serializable;

public interface ImageData extends Serializable {

    @NonNull
    String getSource();
}
