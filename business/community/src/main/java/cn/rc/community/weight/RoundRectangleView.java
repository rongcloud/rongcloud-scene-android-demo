package cn.rc.community.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.basis.utils.UIKit;
import com.basis.utils.UiUtils;

import cn.rc.community.R;

/**
 * 圆角形状的视图
 */
public class RoundRectangleView extends CoordinatorLayout {

    public RoundRectangleView(Context context) {
        super(context);
    }

    public RoundRectangleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, R.attr.coordinatorLayoutStyle);
    }

    public RoundRectangleView(@NonNull Context context, @Nullable AttributeSet attrs,
                              @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int topLeftRadius = dp2px(4);
    private int topRightRadius = dp2px(4);
    private int bottomRightRadius = dp2px(4);
    private int bottomLeftRadius = dp2px(4);

    /**
     * 设置圆角的radius 单位dp
     *
     * @param topLeftRadius
     * @param topRightRadius
     * @param bottomRightRadius
     * @param bottomLeftRadius
     */
    public void setRoundRadius(int topLeftRadius, int topRightRadius, int bottomRightRadius, int bottomLeftRadius) {
        this.topLeftRadius = dp2px(topLeftRadius);
        this.topRightRadius = dp2px(topRightRadius);
        this.bottomRightRadius = dp2px(bottomRightRadius);
        this.bottomLeftRadius = dp2px(bottomLeftRadius);
        postInvalidate();
    }

    /**
     * 裁剪画布
     *
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        Path path = new Path();
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        path.moveTo(0, topLeftRadius);
        // top left
        RectF tl = new RectF(0,
                0,
                2 * topLeftRadius,
                2 * topLeftRadius);
        path.arcTo(tl, 180, 90);
        // top right
        path.lineTo(width - topRightRadius, 0);
        RectF tr = new RectF(width - 2 * topRightRadius,
                0,
                width,
                2 * topRightRadius);
        path.arcTo(tr, 270, 90);
        // bottom right
        path.lineTo(width, height * bottomRightRadius);
        RectF br = new RectF(width - 2 * topRightRadius,
                height - 2 * bottomRightRadius,
                width,
                height);
        path.arcTo(br, 0, 90);
        // bottom left
        path.lineTo(width - bottomRightRadius, height);
        RectF bl = new RectF(0,
                height - 2 * bottomLeftRadius,
                2 * bottomLeftRadius,
                height);
        path.arcTo(bl, 90, 90);
        path.close();
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
    }

    public static int dp2px(float dp) {
        float density = UIKit.getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
}
