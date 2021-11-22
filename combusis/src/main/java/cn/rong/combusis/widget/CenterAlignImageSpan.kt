package cn.rong.combusis.widget

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan
import androidx.annotation.IntRange

class CenterAlignImageSpan : ImageSpan {

    constructor(drawable: Drawable) : super(drawable)

    constructor(drawable: Drawable, @IntRange(from = 0, to = 2) verticalAlignment: Int) : super(
        drawable,
        verticalAlignment
    )

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        canvas.save()

        val fm = paint.fontMetricsInt
        val translationY = (y + fm.descent + y + fm.ascent) / 2 - drawable.bounds.bottom / 2
        canvas.translate(x, translationY.toFloat())

        val drawable = drawable
        drawable.draw(canvas)

        canvas.restore()
    }
}