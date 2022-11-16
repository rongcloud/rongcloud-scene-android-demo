/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.roomkit.ui.room.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.ColorInt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.rongcloud.roomkit.R;


public class WaveView extends View {
    private static final String TAG = "WaveView";

    private float mInitialRadius;   // 初始波纹半径
    private float mMaxRadius = 0;   // 最大波纹半径
    private long mDuration = 3200; // 一个波纹从创建到消失的持续时间
    private int mSpeed = 800;   // 波纹的创建速度，每500ms创建一个
    private float mMaxRadiusRate = 1.0f;
    private boolean mMaxRadiusSet;

    private boolean mIsRunning;
    private long mLastCreateTime;
    private final List<Circle> mCircleList = new ArrayList<>((int) (mDuration / mSpeed + 1));

    private final Runnable mCreateCircle = new Runnable() {
        @Override
        public void run() {
            if (mIsRunning) {
                newCircle();
                postDelayed(mCreateCircle, mSpeed);
            }
        }
    };

    private Interpolator mInterpolator = new LinearInterpolator();

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public void setStyle(Paint.Style style) {
        mPaint.setStyle(style);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        @SuppressLint({"CustomViewStyleable"}) TypedArray obtainStyledAttributes =
                getContext().obtainStyledAttributes(attrs, R.styleable.wave_view);
        mInitialRadius = obtainStyledAttributes.getDimension(R.styleable.wave_view_init_radius, 0);

        int maxRadius = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.wave_view_max_radius, 0);
        if (maxRadius != 0) {
            mMaxRadius = maxRadius;
            mMaxRadiusSet = true;
        }
        mDuration = obtainStyledAttributes.getInteger(R.styleable.wave_view_wave_duration, 1500);
        mSpeed = obtainStyledAttributes.getInteger(R.styleable.wave_view_wave_speed, 600);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(obtainStyledAttributes.getColor(R.styleable.wave_view_wave_color, getResources().getColor(R.color.colorAccent)));
        obtainStyledAttributes.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (!mMaxRadiusSet) {
            mMaxRadius = Math.min(w, h) * mMaxRadiusRate / 2.0f;
        }
    }

    public void setMaxRadiusRate(float maxRadiusRate) {
        mMaxRadiusRate = maxRadiusRate;
    }

    public void setColor(@ColorInt int color) {
        mPaint.setColor(color);
    }

    /**
     * 开始
     */
    public void start() {
        if (!mIsRunning) {
            mIsRunning = true;
            mCreateCircle.run();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clear();
    }

    private void clear() {
        removeCallbacks(mCreateCircle);
        mCircleList.clear();
    }

    /**
     * 缓慢停止
     */
    public void stop() {
        mIsRunning = false;
    }

    /**
     * 立即停止
     */
    public void stopImmediately() {
        mIsRunning = false;
        mCircleList.clear();
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        Iterator<Circle> iterator = mCircleList.iterator();
        while (iterator.hasNext()) {
            Circle circle = iterator.next();
            float radius = circle.getCurrentRadius();
            if (System.currentTimeMillis() - circle.mCreateTime < mDuration) {
                mPaint.setAlpha(circle.getAlpha());
                canvas.drawCircle(getWidth() >> 1, getHeight() >> 1, radius, mPaint);
            } else {
                iterator.remove();
            }
        }
        if (mCircleList.size() > 0) {
            postInvalidateDelayed(10);
        }
    }

    public void setInitialRadius(float radius) {
        mInitialRadius = radius;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public void setMaxRadius(float maxRadius) {
        mMaxRadius = maxRadius;
        mMaxRadiusSet = true;
    }

    public void setSpeed(int speed) {
        mSpeed = speed;
    }


    private void newCircle() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastCreateTime < mSpeed) {
            return;
        }
        Circle circle = new Circle();
        mCircleList.add(circle);
        invalidate();
        mLastCreateTime = currentTime;
    }

    private class Circle {
        private final long mCreateTime;

        Circle() {
            mCreateTime = System.currentTimeMillis();
        }

        int getAlpha() {

            float percent = (getCurrentRadius() - mInitialRadius) / (mMaxRadius - mInitialRadius);
            return (int) (255 - mInterpolator.getInterpolation(percent) * 255);
        }

        float getCurrentRadius() {
            float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
            return mInitialRadius + mInterpolator.getInterpolation(percent) * (mMaxRadius - mInitialRadius);
        }
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
        if (mInterpolator == null) {
            mInterpolator = new LinearInterpolator();
        }
    }
}
