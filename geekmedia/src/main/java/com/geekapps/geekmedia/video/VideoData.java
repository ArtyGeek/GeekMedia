package com.geekapps.geekmedia.video;

import android.support.annotation.NonNull;

import java.io.Serializable;

public interface VideoData extends Serializable {

    boolean hasPreview();

    String getPreviewSource();

    @NonNull
    String getVideoSource();
}
