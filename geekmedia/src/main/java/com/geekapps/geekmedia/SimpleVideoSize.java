package com.geekapps.geekmedia;

final class SimpleVideoSize implements VideoSize {

    private final int mWidth;
    private final int mHeight;
    private final float mPixelWidthHeightRatio;

    public static SimpleVideoSize from(int width, int height, float pixelWidthHeightRatio) {
        return new SimpleVideoSize(width, height, pixelWidthHeightRatio);
    }

    private SimpleVideoSize(int width, int height, float pixelWidthHeightRatio) {
        mWidth = width;
        mHeight = height;
        mPixelWidthHeightRatio = pixelWidthHeightRatio;
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
    public float getPixelWidthHeightRatio() {
        return mPixelWidthHeightRatio;
    }
}
