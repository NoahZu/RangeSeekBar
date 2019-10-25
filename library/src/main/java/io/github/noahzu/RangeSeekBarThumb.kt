package io.github.noahzu

import android.graphics.Rect
import android.graphics.drawable.Drawable

class RangeSeekBarThumb(val position: Rect, val drawable: Drawable) {
    fun updateBounds() {
        drawable.bounds = position
    }
}