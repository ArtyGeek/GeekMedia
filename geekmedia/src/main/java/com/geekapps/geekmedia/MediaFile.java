package com.geekapps.geekmedia;

import com.geekapps.geekmedia.image.ImageData;
import com.geekapps.geekmedia.video.VideoData;

import java.io.Serializable;

public interface MediaFile extends Serializable {

    boolean isImage();

    boolean isVideo();

    ImageData getImageData();

    VideoData getVideoData();
}
