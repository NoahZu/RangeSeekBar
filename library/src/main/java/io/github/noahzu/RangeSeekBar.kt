package io.github.noahzu

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.library.R

/**
 * 可以选择范围的seekbar
 */
class RangeSeekBar : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private lateinit var leftThumb: RangeSeekBarThumb
    private lateinit var rightThumb: RangeSeekBarThumb
    private lateinit var bottomLineRect: Rect
    private lateinit var topLineRect: Rect
    private lateinit var contentRect: Rect

    private val topLineColor = Color.parseColor("#3A78E5")
    private val bottomLineColor = topLineColor
    private val contentColor = Color.parseColor("#000000")

    private val thumbDefaultWidth = 45
    private val lineHeight = 13
    private val paint: Paint

    private var latestPositionX = 0f
    private var selectedThumb: RangeSeekBarThumb? = null

    var onSlide: ((cuttedProgress: Float) -> Unit)? = null

    init {
        paint = Paint()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initThumb()
    }

    private fun initThumb() {
        val leftDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_range_left, null)
        val rightDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_range_right, null)

        val leftBounds = Rect(0, 0, thumbDefaultWidth, height)
        val rightBounds = Rect(width - thumbDefaultWidth, 0, width, height)
        leftDrawable?.setBounds(leftBounds)
        rightDrawable?.setBounds(rightBounds)

        leftThumb = RangeSeekBarThumb(leftBounds, leftDrawable!!)
        rightThumb = RangeSeekBarThumb(rightBounds, rightDrawable!!)
        bottomLineRect = Rect()
        topLineRect = Rect()
        contentRect = Rect()

        calculateAllRect()
    }

    private fun calculateAllRect() {
        bottomLineRect.set(leftThumb.position.right, 0, rightThumb.position.left, lineHeight)
        topLineRect.set(leftThumb.position.right, height - lineHeight, rightThumb.position.left, height)
        contentRect.set(leftThumb.position.right, lineHeight, rightThumb.position.left, height - lineHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawThumb(canvas)
        drawLineAndContent(canvas)
    }

    private fun drawLineAndContent(canvas: Canvas?) {
        canvas?.run {
            paint.setARGB(0xFF, 0x3A, 0x78, 0xE5)
            paint.alpha = 0xFF
            canvas.drawRect(topLineRect, paint)
            canvas.drawRect(bottomLineRect, paint)

            paint.setARGB(0x66, 0x00, 0x00, 0x00)
            paint.alpha = 0x66
            canvas.drawRect(contentRect, paint)
        }
    }

    private fun drawThumb(canvas: Canvas?) {
        canvas?.run {
            leftThumb.updateBounds()
            leftThumb.drawable.draw(this)
            rightThumb.updateBounds()
            rightThumb.drawable.draw(this)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.isTouchArea(leftThumb.position)) {
                    latestPositionX = event.x
                    selectedThumb = leftThumb
                    return true
                }

                if (event.isTouchArea(rightThumb.position)) {
                    latestPositionX = event.x
                    selectedThumb = rightThumb
                    return true
                }

                return super.onTouchEvent(event)
            }
            MotionEvent.ACTION_MOVE -> {
                selectedThumb?.let {
                    moveThumb(it, event.x - latestPositionX)
                    latestPositionX = event.x
                    onSlide?.invoke(getRightPosition() - getLeftPosition())
                }
            }
            MotionEvent.ACTION_UP -> {
                latestPositionX = 0f
                selectedThumb = null
            }
        }
        return super.onTouchEvent(event)
    }

    private fun moveThumb(thumb: RangeSeekBarThumb, x: Float) {
        thumb.position.offset(x.toInt(), 0)
        calculateAllRect()
        invalidate()
    }

    fun getLeftPosition(): Float {
        return (contentRect.left - left).toFloat() / width
    }

    fun getRightPosition(): Float {
        return (contentRect.right - left).toFloat() / width
    }

    fun reset() {
        val leftBounds = Rect(0, 0, thumbDefaultWidth, height)
        val rightBounds = Rect(width - thumbDefaultWidth, 0, width, height)
        leftThumb.position.set(leftBounds)
        rightThumb.position.set(rightBounds)
        calculateAllRect()
    }
}

