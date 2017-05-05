package com.geekapps.geekmedia;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorInt;
import android.widget.SeekBar;

public final class SeekBarUtils {

    public static void applyColor(SeekBar seekBar, @ColorInt int color) {
        LayerDrawable ld = (LayerDrawable) seekBar.getProgressDrawable();
        Drawable d1 = ld.findDrawableByLayerId(android.R.id.progress);
        d1.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        seekBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

}
