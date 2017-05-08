package com.geekapps.geekmedia.audio;

import android.content.Context;
import android.os.Handler;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.metadata.MetadataRenderer;
import com.google.android.exoplayer2.text.TextRenderer;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.util.ArrayList;

public final class AudioRenderersFactory extends DefaultRenderersFactory {

    private final Context mContext;
    private final DrmSessionManager<FrameworkMediaCrypto> mDrmSessionManager;
    private final @ExtensionRendererMode int mExtensionRendererMode;

    public AudioRenderersFactory(Context context) {
        this(context, null);
    }

    public AudioRenderersFactory(Context context,
                                 DrmSessionManager<FrameworkMediaCrypto> drmSessionManager) {

        this(context, drmSessionManager, EXTENSION_RENDERER_MODE_OFF);
    }

    public AudioRenderersFactory(Context context,
                                 DrmSessionManager<FrameworkMediaCrypto> drmSessionManager,
                                 @ExtensionRendererMode int extensionRendererMode) {

        this(context, drmSessionManager, extensionRendererMode, DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    public AudioRenderersFactory(Context context,
                                 DrmSessionManager<FrameworkMediaCrypto> drmSessionManager,
                                 @ExtensionRendererMode int extensionRendererMode,
                                 long allowedVideoJoiningTimeMs) {

        super(context, drmSessionManager, extensionRendererMode, allowedVideoJoiningTimeMs);
        mContext = context;
        mDrmSessionManager = drmSessionManager;
        mExtensionRendererMode = extensionRendererMode;
    }

    @Override
    public Renderer[] createRenderers(Handler eventHandler,
                                      VideoRendererEventListener videoRendererEventListener,
                                      AudioRendererEventListener audioRendererEventListener,
                                      TextRenderer.Output textRendererOutput,
                                      MetadataRenderer.Output metadataRendererOutput) {

        ArrayList<Renderer> renderersList = new ArrayList<>();

        buildAudioRenderers(mContext,
                            mDrmSessionManager,
                            buildAudioProcessors(),
                            eventHandler, audioRendererEventListener,
                            mExtensionRendererMode,
                            renderersList);

        return renderersList.toArray(new Renderer[renderersList.size()]);
    }
}
