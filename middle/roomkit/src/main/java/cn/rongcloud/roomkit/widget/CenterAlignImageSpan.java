package cn.rongcloud.roomkit.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;

/**
 * @author gyn
 * @date 2022/2/15
 */
public class CenterAlignImageSpan extends ImageSpan {
    private Drawable drawable;

    public CenterAlignImageSpan(@NonNull Drawable drawable) {
        super(drawable);
        this.drawable = drawable;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        canvas.save();

        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        int translationY = (y + fm.descent + y + fm.ascent) / 2 - getDrawable().getBounds().bottom / 2;
        canvas.translate(x, translationY);
        drawable.draw(canvas);

        canvas.restore();
    }
}
