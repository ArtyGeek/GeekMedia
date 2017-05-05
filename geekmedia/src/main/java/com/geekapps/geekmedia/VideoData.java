package com.geekapps.geekmedia;

import java.io.Serializable;

public interface VideoData extends Serializable {

    boolean hasPreview();

    String getPreviewSource();

    String getVideoSource();
}
