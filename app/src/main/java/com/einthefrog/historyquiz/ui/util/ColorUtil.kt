package com.einthefrog.historyquiz.ui.util

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat

object ColorUtil {
    private const val defaultColor = Color.BLACK

    @ColorInt
    fun colorWithAlphaFromResource(context: Context?, resource: Int, alpha: Float): Int {
        @ColorInt val color = colorFromResource(context, resource)
        return colorWithAlpha(color, alpha)
    }

    @ColorInt
    fun colorFromResource(context: Context?, resource: Int): Int {
        if (context == null) return defaultColor
        return ContextCompat.getColor(context, resource)
    }

    @ColorInt
    fun colorWithAlpha(@ColorInt color: Int, alphaFloat: Float): Int {
        val alpha = (alphaFloat * 255).toInt()
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }
}
