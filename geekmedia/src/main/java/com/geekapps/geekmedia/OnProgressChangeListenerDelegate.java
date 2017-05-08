package com.geekapps.geekmedia;

import java.lang.ref.WeakReference;
import java.util.List;

public class OnProgressChangeListenerDelegate implements OnProgressChangeListener {

    private final List<WeakReference<OnProgressChangeListener>> mListeners;

    public OnProgressChangeListenerDelegate(List<WeakReference<OnProgressChangeListener>> listeners) {
        mListeners = listeners;
    }

    @Override
    public void onProgressChanged(int progress) {
        for (WeakReference<OnProgressChangeListener> ref : mListeners) {
            if (ref.get() != null) {
                ref.get().onProgressChanged(progress);
            }
        }
    }
}
