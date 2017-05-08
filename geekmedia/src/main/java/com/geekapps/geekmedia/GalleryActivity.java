package com.geekapps.geekmedia;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class GalleryActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    public static final String TAG = GalleryActivity.class.getName();
    public static final String POSITION_EXTRA = "POSITION_EXTRA";
    private static final String FILES_EXTRA = "FILES_EXTRA";

    private ImageDownloader mImageDownloader;

    private ViewPager mViewPager;
    private ImageView mPhotoContainer;
    private FrameLayout mIndicatorContainer;
    private CircleIndicator mIndicators;

    private int mCurrentItemPosition;

    public static void show(Activity context, List<MediaFile> files) {
        show(context, files, 0);
    }

    public static void show(Activity context,
                            List<MediaFile> files,
                            int position) {

        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(FILES_EXTRA, new ArrayList<>(files));
        intent.putExtra(POSITION_EXTRA, position);
        context.startActivityForResult(intent, 0);
    }

    public static void show(Activity activity,
                            ImageView sharedImage,
                            List<MediaFile> files,
                            int position) {

        Intent intent = new Intent(activity, GalleryActivity.class);
        intent.putExtra(FILES_EXTRA, (Serializable) files);
        intent.putExtra(POSITION_EXTRA, position);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                sharedImage,
                activity.getString(R.string.transition_name_photo));

        activity.startActivity(intent, options.toBundle());
    }

    public static void show(Fragment fragment,
                            int requestCode,
                            ImageView sharedImage,
                            List<MediaFile> files,
                            int position) {

        Activity activity = fragment.getActivity();
        Intent intent = new Intent(activity, GalleryActivity.class);
        intent.putExtra(FILES_EXTRA, (Serializable) files);
        intent.putExtra(POSITION_EXTRA, position);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                sharedImage,
                activity.getString(R.string.transition_name_photo));

        fragment.startActivityForResult(intent, requestCode, options.toBundle());
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition transition = getWindow().getSharedElementEnterTransition();
            boolean shouldShowTransition = savedInstanceState == null
                    && transition != null;

            mPhotoContainer.setVisibility(shouldShowTransition ? View.VISIBLE : View.GONE);
            mViewPager.setVisibility(shouldShowTransition ? View.GONE : View.VISIBLE);

            if (shouldShowTransition) {
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

        Bundle args = getIntent().getExtras();
        List<MediaFile> files = (List<MediaFile>) args.getSerializable(FILES_EXTRA);
        mCurrentItemPosition = args.getInt(POSITION_EXTRA);

        if (files == null || files.isEmpty()) {
            throw new IllegalStateException("There must be at least 1 MediaFile");
        }

        mImageDownloader = getImageDownloaderFactory().provideImageDownloader(this);
        mImageDownloader.loadImage(
                files.get(mCurrentItemPosition).getImageData().getSource(),
                R.drawable.placeholder_black,
                mPhotoContainer);

        GalleryAdapter adapter = GalleryAdapter.from(getSupportFragmentManager(), files);
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
        return ContextCompat.getColor(this, R.color.colorAccent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(POSITION_EXTRA, mCurrentItemPosition);
        setResult(RESULT_OK, intent);
        finish();
    }
}