package com.geekapps.geekmedia;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.geekapps.geekmedia.image.ImageViewerFragment;
import com.geekapps.geekmedia.video.VideoPlayerFragment;

import java.util.List;

public final class GalleryAdapter extends FragmentStatePagerAdapter {

    public static final String TAG = GalleryAdapter.class.getSimpleName();

    private final List<MediaFile> mFiles;

    private Fragment mCurrentFragment;
    private AccentColorProvider mAccentColorProvider;

    public static GalleryAdapter from(FragmentManager fragmentManager, List<MediaFile> files) {
        return new GalleryAdapter(fragmentManager, files);
    }

    private GalleryAdapter(FragmentManager fragmentManager, List<MediaFile> files) {
        super(fragmentManager);
        mFiles = files;
    }

    @Override
    public Fragment getItem(int position) {
        MediaFile file = mFiles.get(position);
        if (file.isVideo()) {
            return createVideoPlayerFragment(file);
        } else {
            return createImageViewerFragment(file);
        }
    }

    @Override
    public int getCount() {
        return mFiles.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (mCurrentFragment != object) {
            if (mCurrentFragment != null && mCurrentFragment instanceof VideoPlayerFragment) {
                ((VideoPlayerFragment) mCurrentFragment).pause();
            }
            mCurrentFragment = ((Fragment) object);
            if (mCurrentFragment instanceof VideoPlayerFragment) {
                VideoPlayerFragment videoFragment = (VideoPlayerFragment) mCurrentFragment;
                videoFragment.play();
            }
        }
        super.setPrimaryItem(container, position, object);
    }

    public void setAccentColorProvider(@Nullable AccentColorProvider accentColorProvider) {
        mAccentColorProvider = accentColorProvider;
    }

    private VideoPlayerFragment createVideoPlayerFragment(MediaFile mediaFile) {
        if (mAccentColorProvider != null) {
            return VideoPlayerFragment.newInstance(mediaFile.getVideoData(), mAccentColorProvider.provideAccentColor());
        }
        return VideoPlayerFragment.newInstance(mediaFile.getVideoData());
    }

    private ImageViewerFragment createImageViewerFragment(MediaFile mediaFile) {
        if (mAccentColorProvider != null) {
            return ImageViewerFragment.newInstance(mediaFile.getImageData(), mAccentColorProvider.provideAccentColor());
        }
        return ImageViewerFragment.newInstance(mediaFile.getImageData());
    }
}