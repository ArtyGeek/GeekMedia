package com.geekapps.geekmedia;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.geekapps.geekmedia.image.ImageDownloader;
import com.geekapps.geekmedia.image.ImageDownloaderFactory;
import com.geekapps.geekmedia.image.PicassoImageDownloaderFactory;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, AccentColorProvider {

    public static final String TAG = GalleryActivity.class.getName();

    private static final String POSITION_EXTRA = "POSITION_EXTRA";
    private static final String FILES_EXTRA = "FILES_EXTRA";
    private static final String ACCENT_COLOR_EXTRA = "ACCENT_COLOR_EXTRA";
    private static final String SHOW_TRANSITION_EXTRA = "SHOW_TRANSITION_EXTRA";
    private static final int NO_COLOR = -1;

    private ImageDownloader mImageDownloader;

    private ViewPager mViewPager;
    private ImageView mPhotoContainer;
    private FrameLayout mIndicatorContainer;
    private CircleIndicator mIndicators;

    private int mAccentColor = NO_COLOR;
    private int mCurrentItemPosition;

    public static class Builder {

        private final Activity mActivity;
        private final List<MediaFile> mFiles;
        private final Intent mIntent;
        private int mRequestCode;
        private ImageView mSharedImage;

        public Builder(@NonNull Activity activity, @NonNull List<MediaFile> files) {
            mActivity = activity;
            mFiles = files;
            mIntent = new Intent(activity, GalleryActivity.class);
            mIntent.putExtra(FILES_EXTRA, new ArrayList<>(mFiles));
        }

        public Builder(@NonNull Fragment fragment, @NonNull List<MediaFile> files) {
            this(fragment.getActivity(), files);
        }

        public Builder setInitialPosition(int position) {
            mIntent.putExtra(POSITION_EXTRA, position);
            return this;
        }

        public Builder setRequestCode(int requestCode) {
            mRequestCode = requestCode;
            return this;
        }

        public Builder setSharedImage(ImageView imageView) {
            mSharedImage = imageView;
            mIntent.putExtra(SHOW_TRANSITION_EXTRA, true);
            return this;
        }

        public Builder setAccentColor(@ColorInt int accentColor) {
            mIntent.putExtra(ACCENT_COLOR_EXTRA, accentColor);
            return this;
        }

        public void show() {
            Bundle options = null;
            if (mSharedImage != null) {
                options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        mActivity,
                        mSharedImage,
                        mActivity.getString(R.string.transition_name_photo)).toBundle();
            }
            mActivity.startActivityForResult(mIntent, mRequestCode, options);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.FullscreenTheme);
        onBeforeCreate();
        setContentView(R.layout.activity_gallery);

        mViewPager = (ViewPager) findViewById(R.id.activity_gallery_view_pager);
        mPhotoContainer = (ImageView) findViewById(R.id.activity_gallery_photo_container);
        mIndicatorContainer = (FrameLayout) findViewById(R.id.activity_gallery_indicator_container);
        mIndicators = (CircleIndicator) findViewById(R.id.activity_gallery_indicators);

        Bundle args = getIntent().getExtras();
        if (args == null) {
            throw new IllegalStateException("Activity must be instantiated via Builder");
        }

        List<MediaFile> files = (List<MediaFile>) args.getSerializable(FILES_EXTRA);
        if (files == null || files.isEmpty()) {
            throw new IllegalStateException("There must be at least 1 MediaFile");
        }

        mCurrentItemPosition = args.getInt(POSITION_EXTRA, 0);
        mAccentColor = args.getInt(ACCENT_COLOR_EXTRA, ContextCompat.getColor(this, R.color.colorAccent));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            boolean shouldShowTransition = args.getBoolean(SHOW_TRANSITION_EXTRA, false);
            mPhotoContainer.setVisibility(shouldShowTransition ? View.VISIBLE : View.GONE);
            mViewPager.setVisibility(shouldShowTransition ? View.GONE : View.VISIBLE);
            if (shouldShowTransition) {
                Transition transition = getWindow().getSharedElementEnterTransition();
                transition.addListener(new SimpleTransitionListener() {
                    @Override
                    public void onTransitionEnd(Transition transition) {
                        mPhotoContainer.post(new Runnable() {
                            @Override
                            public void run() {
                                mPhotoContainer.setVisibility(View.GONE);
                            }
                        });
                        mViewPager.post(new Runnable() {
                            @Override
                            public void run() {
                                mViewPager.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
            }
        } else {
            mPhotoContainer.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);
        }

        mImageDownloader = getImageDownloaderFactory().provideImageDownloader(this);
        mImageDownloader.loadImage(
                files.get(mCurrentItemPosition).getImageData().getSource(),
                R.drawable.placeholder_black,
                mPhotoContainer);

        GalleryAdapter adapter = GalleryAdapter.from(getSupportFragmentManager(), files);
        adapter.setAccentColorProvider(this);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setCurrentItem(mCurrentItemPosition);

        mIndicatorContainer.setVisibility(View.VISIBLE);
        GradientDrawable indicatorDrawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.shape_indicator_white);
        indicatorDrawable.setColor(getIndicatorColor());
        mIndicators.setSelectedIndicatorDrawable(indicatorDrawable);
        mIndicators.setViewPager(mViewPager);
    }

    //For DI purposes
    protected void onBeforeCreate() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.removeOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //do nothing
    }

    @Override
    public void onPageSelected(int position) {
        Log.v(TAG, "onPageSelected, position " + position);
        mCurrentItemPosition = position;
        if (mViewPager != null) {
            mViewPager.setCurrentItem(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //do nothing
    }

    public ImageDownloaderFactory getImageDownloaderFactory() {
        return new PicassoImageDownloaderFactory();
    }

    @ColorInt
    public int getIndicatorColor() {
        return provideAccentColor();
    }

    @Override
    public int provideAccentColor() {
        return mAccentColor;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(POSITION_EXTRA, mCurrentItemPosition);
        setResult(RESULT_OK, intent);
        finish();
    }
}