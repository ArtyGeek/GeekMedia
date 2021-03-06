package com.geekapps.geekmedia.image;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geekapps.geekmedia.R;
import com.geekapps.geekmedia.video.VideoData;
import com.geekapps.geekmedia.video.VideoPlayerFragment;

public class ImageViewerFragment extends Fragment {

    private static final String TAG = ImageViewerFragment.class.getSimpleName();
    private static final String EXTRA_IMAGE_DATA = "EXTRA_IMAGE_DATA";
    private static final String EXTRA_ACCENT_COLOR = "EXTRA_ACCENT_COLOR";

    private ImageDownloader mImageDownloader;
    private TouchImageView mTouchImage;

    private int mAccentColor;
    private ImageData mImageData;

    public static ImageViewerFragment newInstance(ImageData data, int accentColor) {
        ImageViewerFragment fragment = new ImageViewerFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IMAGE_DATA, data);
        args.putSerializable(EXTRA_ACCENT_COLOR, accentColor);
        fragment.setArguments(args);
        return fragment;
    }

    public static ImageViewerFragment newInstance(ImageData data) {
        ImageViewerFragment fragment = new ImageViewerFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IMAGE_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBeforeCreate();
        Bundle args = getArguments();
        if (args == null) {
            throw new IllegalStateException("ImageViewerFragment must be instantiated via `newInstance` factory method");
        }
        mImageData = (ImageData) args.getSerializable(EXTRA_IMAGE_DATA);
        mAccentColor = args.getInt(EXTRA_ACCENT_COLOR);
        mImageDownloader = getImageDownloaderFactory().provideImageDownloader(getContext());
    }

    //For DI purposes
    protected void onBeforeCreate() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_viewer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mTouchImage = (TouchImageView) view.findViewById(R.id.fragment_image_viewer_touch_image);
        mImageDownloader.loadImage(mImageData.getSource(), R.drawable.placeholder_black, mTouchImage);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && mTouchImage != null) {
            mTouchImage.resetZoom();
        }
    }

    public ImageDownloaderFactory getImageDownloaderFactory() {
        return new PicassoImageDownloaderFactory();
    }
}
