/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.rongcloud.common.R
import java.util.*


/**
 * @author gusd
 * @Date 2021/07/08
 */
class MusicPlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var columnNum = 4
    private var random = 0
    private var isStart = true
    private var mRandom: Random = Random()


    private var mPaint: Paint = Paint()
    private var mWidth = 0
    private var mHeight = 0
    private var mRectWidth = 0.0
    private var during = 180L


    private val rectList = arrayListOf<RectF>()
    private val randomList = arrayListOf<Int>()


    init {

        val array = context.obtainStyledAttributes(attrs, R.styleable.MusicPlayView)
        mPaint.color =
            array.getColor(R.styleable.MusicPlayView_line_color,
                ContextCompat.getColor(context, android.R.color.black))
        during = array.getInteger(R.styleable.MusicPlayView_refresh_duration, 180).toLong()
        columnNum = array.getInteger(R.styleable.MusicPlayView_line_number, 4)

        array.recycle()

        mPaint.style = Paint.Style.FILL

        for (i in 0 until columnNum) {
            rectList.add(RectF())
        }
    }

    private fun refreshHeight(height: Int? = null) {
        randomList.clear()
        for (i in 0 until columnNum) {
            randomList.add(height ?: mRandom.nextInt(random))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = MeasureSpec.getSize(widthMeasureSpec)
        mHeight = MeasureSpec.getSize(heightMeasureSpec)
        mRectWidth = (mWidth / (2 * columnNum + 1)).toDouble()
        random = mHeight / columnNum
        refreshHeight()
    }

    fun start() {
        isStart = true
        invalidate()
    }

    fun stop() {
        isStart = false
        invalidate()
    }

    fun isStart(): Boolean {
        return isStart
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }

    private val runnable = {
        refreshHeight()
        invalidate()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isStart) {
            postDelayed(runnable, during)
        } else {
            removeCallbacks(runnable)
            refreshHeight(0)
        }

        rectList.forEachIndexed { index, rect ->

            rect.set(
                (mRectWidth * (index * 2 + 1)).toFloat(),
                (randomList[index] * rectList.size).toFloat(),
                (mRectWidth * (index + 1) * 2).toFloat(),
                mHeight.toFloat()
            )
        }

        rectList.forEach {
            canvas.drawRect(it, mPaint)
        }

    }


}