package io.github.noahzu

import android.graphics.Rect
import android.view.MotionEvent


fun MotionEvent.isTouchArea(area: Rect): Boolean {
    return area.contains(x.toInt(), y.toInt())
}
