package com.geekapps.geekmedia.video;

final class SimpleVideoSize implements VideoSize {

    private final int mWidth;
    private final int mHeight;
    private final float mAspectRatio;

    public static SimpleVideoSize fromWidthHeightRatio(int width, int height, float pixelWidthHeightRatio) {
        float aspectRatio = height == 0 ? 1 : (width * pixelWidthHeightRatio) / height;
        return new SimpleVideoSize(width, height, aspectRatio);
    }

    private SimpleVideoSize(int width, int height, float aspectRatio) {
        mWidth = width;
        mHeight = height;
        mAspectRatio = aspectRatio;
    }

    @Override
    public int getWidth() {
        return mWidth;
    }

    @Override
    public int getHeight() {
        return mHeight;
    }

    @Override
    public float getAspectRatio() {
        return mAspectRatio;
    }
}
