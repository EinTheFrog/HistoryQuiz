package com.einthefrog.historyquiz.ui.util;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;

public class ColorUtil {
    @ColorInt
    public static int colorWithAlphaFromResource(Context context, int resource, float alpha) {
        @ColorInt int color = colorFromResource(context, resource);
        return colorWithAlpha(color, alpha);
    }

    @ColorInt
    public static int colorFromResource(Context context, int resource) {
        @ColorInt int color = ContextCompat.getColor(context, resource);
        return color;
    }

    @ColorInt
    public static int colorWithAlpha(@ColorInt int color, float alphaFloat) {
        int alpha = (int) (alphaFloat * 255);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }
}
