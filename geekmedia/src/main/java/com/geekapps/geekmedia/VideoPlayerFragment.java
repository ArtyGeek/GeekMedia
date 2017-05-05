package com.geekapps.geekmedia;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;

import java.util.concurrent.atomic.AtomicBoolean;


public class VideoPlayerFragment extends Fragment implements
        VideoPlayer.OnProgressChangeListener,
        VideoPlayer.OnPreparedListener,
        VideoPlayer.OnVideoPlayerSizeChangeListener {

    public static final String TAG = VideoPlayerFragment.class.getName();

    private static final String EXTRA_VIDEO_DATA = "EXTRA_VIDEO_DATA";

    private int mAnimationDuration;

    private FrameLayout mPlaceholderContainer;
    private ImageView mPlaceholderPreview;
    private CircularProgressView mPlaceholderLoader;
    private AspectRatioFrameLayout mTextureHolder;
    private TextureView mTexture;
    private View mController;
    private SeekBar mProgress;
    private View mPlayPause;

    private VideoData mVideoData;

    private ImageDownloader mImageDownloader;
    private VideoPlayer mPlayer;
    private boolean mShouldPlay;
    private final AtomicBoolean mDragFlag = new AtomicBoolean();

    public static VideoPlayerFragment newInstance(VideoData data) {
        VideoPlayerFragment fragment = new VideoPlayerFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_VIDEO_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mVideoData = (VideoData) args.getSerializable(EXTRA_VIDEO_DATA);
        }

        mImageDownloader = getImageDownloaderFactory().provideImageDownloader(getContext());
        mPlayer = getVideoPlayerFactory().provideVideoPlayer(getContext());
        mShouldPlay = true;

        mAnimationDuration = getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_video_player, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        mPlaceholderContainer = (FrameLayout) view.findViewById(R.id.fragment_video_player_placeholder);
        mPlaceholderPreview = (ImageView) view.findViewById(R.id.fragment_video_player_placeholder_image);
        mPlaceholderLoader = (CircularProgressView) view.findViewById(R.id.fragment_video_player_placeholder_loader);
        mPlaceholderLoader.setColor(getControllerColor());

        mTextureHolder = (AspectRatioFrameLayout) view.findViewById(R.id.fragment_video_player_texture_holder);
        mTexture = (TextureView) view.findViewById(R.id.fragment_video_player_texture);
        mController = view.findViewById(R.id.fragment_video_player_controller);
        mProgress = (SeekBar) view.findViewById(R.id.fragment_video_player_controller_progress);
        mPlayPause = view.findViewById(R.id.fragment_video_player_controller_play_pause);

        if (mVideoData.hasPreview()) {
            mImageDownloader.loadImage(mVideoData.getPreviewSource(), getPlaceholderDrawableRes(), mPlaceholderPreview);
        }

        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer.isPlaying()) {
                    pause();
                } else {
                    play();
                }
            }
        });

        mPlayer.setTexture(mTexture);
    }

    private void initSeekBar() {
        SeekBarUtils.applyColor(mProgress, getControllerColor());
        mProgress.setOnSeekBarChangeListener(new VideoPlayerSeekBarChangeListener(mPlayer, mDragFlag));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume, shouldPlay " + mShouldPlay);
        mPlayer.setLooping(true);
        mPlayer.play(mVideoData.getVideoSource());
        if (!mShouldPlay) {
            pause();
        }
        mPlayer.addOnProgressChangeListener(this);
        mPlayer.addOnPreparedListener(this);
        mPlayer.addOnVideoPlayerSizeChangeListener(this);

        initSeekBar();
    }

    @Override
    public void onPause() {
        super.onPause();
        mShouldPlay = mPlayer != null && mPlayer.isPlaying();
        Log.v(TAG, "onPause, shouldPlay " + mShouldPlay);
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.removeOnProgressChangeListener(this);
            mPlayer.removeOnPreparedListener(this);
            mPlayer.removeOnVideoPlayerSizeChangeListener(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
        mPlayer.removeOnProgressChangeListener(this);
        mPlayer.stop();
    }

    @Override
    public void onProgressChanged(int progress) {
        if (!mDragFlag.get()) {
            mProgress.setProgress(progress);
        }
    }

    @Override
    public void onPrepared() {
        mProgress.setMax(mPlayer.getDuration());
        hidePlaceholder();
        slideUpVideoController();
    }

    @Override
    public void onVideoSizeChanged(VideoSize size) {
        mTextureHolder.setAspectRatio(size.getAspectRatio());
    }

    public void play() {
        Log.v(TAG, "play");
        if (mPlayer != null) {
            mPlayer.resume();
            mPlayPause.setBackgroundResource(R.drawable.ic_pause_white_24dp);
        } else {
            mShouldPlay = true;
        }
    }

    public void pause() {
        Log.v(TAG, "pause");
        if (mPlayer != null) {
            mPlayer.pause();
            mPlayPause.setBackgroundResource(R.drawable.ic_play_arrow_white_24dp);
        } else {
            mShouldPlay = false;
        }
    }

    @NonNull
    public VideoPlayerFactory getVideoPlayerFactory() {
        return new ExoPlayerVideoFactory();
    }

    @ColorInt
    public int getPlaceholderLoaderColor() {
        return ContextCompat.getColor(getContext(), R.color.colorAccent);
    }

    @ColorInt
    public int getControllerColor() {
        return ContextCompat.getColor(getContext(), android.R.color.white);
    }

    @DrawableRes
    public int getPlaceholderDrawableRes() {
        return R.drawable.placeholder_black;
    }

    public ImageDownloaderFactory getImageDownloaderFactory() {
        return new PicassoImageDownloaderFactory();
    }

    private void hidePlaceholder() {
        Log.v(TAG, "hidePlaceholder");
        mPlaceholderContainer.animate()
                .alpha(0f)
                .setDuration(mAnimationDuration)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mPlaceholderContainer.setVisibility(View.GONE);
                    }
                });
    }

    private void slideUpVideoController() {
        if (mController.getVisibility() != View.VISIBLE) {
            mController.post(new Runnable() {
                @Override
                public void run() {
                    mController.setTranslationY(mController.getHeight());
                    mController.setVisibility(View.VISIBLE);
                    mController.animate().translationY(0f);
                }
            });
        }
    }

}
