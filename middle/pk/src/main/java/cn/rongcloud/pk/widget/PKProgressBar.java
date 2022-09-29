package cn.rongcloud.pk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import androidx.annotation.DrawableRes;

import com.basis.utils.Logger;

import cn.rongcloud.pk.R;


/**
 * 自定义pk进度条
 */
public class PKProgressBar extends ProgressBar {
    private static final String TAG = "PKProgressBar";
    /**
     * 设置各种默认值
     */
    private static final int DEFAULT_TEXT_SIZE = 10;
    private static final int DEFAULT_TEXT_COLOR = 0XFFFC00D1;
    private static final int DEFAULT_COLOR_UNREACHED_COLOR = 0xFFd3d6da;
    private static final int DEFAULT_HEIGHT_REACHED_PROGRESS_BAR = 2;
    private static final int DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR = 2;
    private static final int DEFAULT_SIZE_TEXT_OFFSET = 10;


    /**
     * painter of all drawing things  所有画图所用的画笔
     */
    protected Paint mPaint = new Paint();
    /**
     * color of progress number  进度号码的颜色
     */
    protected int mTextColor = DEFAULT_TEXT_COLOR;
    /**
     * size of text (sp)  文本的大小
     */
    protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);

    /**
     * offset of draw progress  进度条文本补偿宽度
     */
    protected int mTextOffset = dp2px(DEFAULT_SIZE_TEXT_OFFSET);

    /**
     * height of reached progress bar  进度条高度
     */
    protected int mReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_REACHED_PROGRESS_BAR);

    /**
     * color of reached bar   成功的文本颜色
     */
    protected int mReachedBarColor = DEFAULT_TEXT_COLOR;
    /**
     * color of unreached bar 未完成的bar颜色
     */
    protected int mUnReachedBarColor = DEFAULT_COLOR_UNREACHED_COLOR;
    /**
     * height of unreached progress bar  未覆盖的进度条高度
     */
    protected int mUnReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR);
    protected int mRealWidth;
    protected int mCornerRadius = 0;
    protected static final int VISIBLE = 0;

    public PKProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PKProgressBar(Context context, AttributeSet attrs,
                         int defStyle) {
        super(context, attrs, defStyle);
        obtainStyledAttributes(attrs);//初始化参数
        mPaint.setTextSize(mTextSize);//文本大小
        mPaint.setColor(mTextColor);//文本颜色
        setPKValue(0, 0);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
                                          int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);//高度
        setMeasuredDimension(width, height);//必须调用该方法来存储View经过测量的到的宽度和高度

        mRealWidth = getMeasuredWidth() - getPaddingRight() - getPaddingLeft();//真正的宽度值是减去左右padding
    }


    /**
     * EXACTLY：父控件告诉我们子控件了一个确定的大小，你就按这个大小来布局。比如我们指定了确定的dp值和macth_parent的情况。
     * AT_MOST：当前控件不能超过一个固定的最大值，一般是wrap_content的情况。
     * UNSPECIFIED:当前控件没有限制，要多大就有多大，这种情况很少出现。
     *
     * @param measureSpec
     * @return 视图的高度
     */
    private int measureHeight(int measureSpec) {

        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);//父布局告诉我们控件的类型
        int specSize = MeasureSpec.getSize(measureSpec);//父布局传过来的视图大小
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            /**
             * mPaint.descent() 最高点的高度
             * mPaint.ascent() 最低点的高度
             */
            float textHeight = (mPaint.descent() - mPaint.ascent());// 设置文本的高度
            /**
             * Math.abs() 返回绝对值
             *  Math.max 返回最大值
             *  Math.min 返回最小值
             */
            result = (int) (getPaddingTop() + getPaddingBottom() + Math.max(
                    Math.max(mReachedProgressBarHeight,
                            mUnReachedProgressBarHeight), Math.abs(textHeight)));
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int barRes;

    /**
     * 设置滑块资源
     */
    public void setBarResource(@DrawableRes int barRes) {
        this.barRes = barRes;
        postInvalidate();
    }

    @Deprecated
    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
    }

    private int leftValue = 0;
    private int rightValue = 0;

    public void setPKValue(int leftValue, int rightValue) {
        this.leftValue = leftValue;
        this.rightValue = rightValue;
        postInvalidate();
    }

    /**
     * get the styled attributes  获取属性的样式
     *
     * @param attrs
     */
    private void obtainStyledAttributes(AttributeSet attrs) {
        // init values from custom attributes
        final TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.PKProgressbar);
        mTextColor = attributes.getColor(R.styleable.PKProgressbar_progress_text_color, DEFAULT_TEXT_COLOR);
        mTextSize = (int) attributes.getDimension(R.styleable.PKProgressbar_progress_text_size, mTextSize);

        mReachedBarColor = attributes.getColor(R.styleable.PKProgressbar_progress_reached_color, mTextColor);
        mUnReachedBarColor = attributes.getColor(R.styleable.PKProgressbar_progress_unreached_color, DEFAULT_COLOR_UNREACHED_COLOR);
        mReachedProgressBarHeight = (int) attributes.getDimension(R.styleable.PKProgressbar_progress_reached_height, mReachedProgressBarHeight);
        mUnReachedProgressBarHeight = (int) attributes.getDimension(R.styleable.PKProgressbar_progress_unreached_height, mUnReachedProgressBarHeight);
        mCornerRadius = (int) attributes.getDimension(R.styleable.PKProgressbar_progress_corner_radius, mCornerRadius);
        mTextOffset = (int) attributes.getDimension(R.styleable.PKProgressbar_progress_text_padding, mTextOffset);
        attributes.recycle();
    }


    /**
     * 开始画
     */
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        int total = leftValue + rightValue;
        float radio;//设置进度
        if (total == 0) {
            radio = 0.5f;
        } else {
            radio = 1.0f * leftValue / total;
        }
        Logger.e(TAG, "radio = " + radio);
        float progressPosX = (int) (mRealWidth * radio);//设置当前进度的宽度
        int mRealheigth = Math.max(mUnReachedProgressBarHeight, mReachedProgressBarHeight);
        // 控制progressPosX
        if (progressPosX < mRealheigth * 2f) {
            progressPosX = mRealheigth * 2f;
        } else if (progressPosX > mRealWidth - mRealheigth * 2f) {
            progressPosX = mRealWidth - mRealheigth * 2f;
        }
        // draw start circle
        float r = Math.min(mRealheigth / 2.0f, mCornerRadius);
        //左半圆
        RectF cl = new RectF();
        cl.top = 0;
        cl.bottom = mRealheigth;
        cl.left = 0;
        cl.right = mRealheigth;
        mPaint.setColor(mReachedBarColor);
        canvas.drawRoundRect(cl, r, r, mPaint);
        //右半圆
        RectF rl = new RectF();
        rl.top = 0;
        rl.bottom = mRealheigth;
        rl.left = mRealWidth - mRealheigth;
        rl.right = mRealWidth;
        mPaint.setColor(mUnReachedBarColor);
        canvas.drawRoundRect(rl, r, r, mPaint);
        // draw left
        RectF lf = new RectF();
        lf.top = 0;
        lf.bottom = mRealheigth;
        lf.left = r;
        lf.right = progressPosX;
        mPaint.setColor(mReachedBarColor);
        canvas.drawRoundRect(lf, 0, 0, mPaint);
        // draw rigth
        RectF rf = new RectF();
        rf.top = 0;
        rf.bottom = mRealheigth;
        rf.left = progressPosX;
        rf.right = mRealWidth - r;
        mPaint.setColor(mUnReachedBarColor);
        canvas.drawRoundRect(rf, 0, 0, mPaint);
        // draw progress bar
        if (barRes != 0) {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), barRes);
            if (null != bmp) {
                Rect src = new Rect();
                RectF des = new RectF();
                int bH = bmp.getHeight();
                int st = (int) progressPosX - bmp.getWidth() / 2;
                int end = (int) progressPosX + bmp.getWidth() / 2;
                if (st < mRealheigth / 2) {
                    st = mRealheigth / 2;
                    end = st + bmp.getWidth();
                } else if (end > mRealWidth - mRealheigth / 2) {
                    end = mRealWidth - mRealheigth / 2;
                    st = end - bmp.getWidth();
                }
                Logger.e(TAG, "mRealheigth = " + mRealheigth + " btmH = " + bH);
                if (bH < mRealheigth) {//图片全绘制，且居中
                    src.top = 0;
                    src.bottom = bmp.getHeight();
                    src.left = 0;
                    src.right = bmp.getWidth();
                    int dv = (mRealheigth - bH) / 2;
                    des.top = dv;
                    des.bottom = mRealheigth - dv;
                    des.left = st;
                    des.right = end;
                } else {//图片要值绘制中间部分
                    int dv = (bH - mRealheigth) / 2;
                    src.left = 0;
                    src.top = dv;
                    src.bottom = bH - dv;
                    src.right = bmp.getWidth();
                    des.top = 0;
                    des.bottom = mRealheigth;
                    des.left = st;
                    des.right = end;
                }
                canvas.drawBitmap(bmp, src, des, mPaint);
            }
        }
        //draw left text
        String text = "我方 " + leftValue;//设置文本
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(sp2px(11));
        float leftWidth = mPaint.measureText(text);//返回文本的宽度
        float textHeight = (mPaint.descent() + mPaint.ascent()) / 2;//设置文本的高度
        canvas.drawText(text, mRealheigth / 5, mRealheigth / 2 - textHeight, mPaint);
        //draw right text
        text = "对方 " + rightValue;//设置文本
        float rightWidth = mPaint.measureText(text);//返回文本的宽度
        canvas.drawText(text, mRealWidth - rightWidth - mRealheigth / 5, mRealheigth / 2 - textHeight, mPaint);
        canvas.restore();
    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    /**
     * sp 2 px
     *
     * @param spVal
     * @return
     */
    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRealWidth = w - getPaddingRight() - getPaddingLeft();
    }

}
