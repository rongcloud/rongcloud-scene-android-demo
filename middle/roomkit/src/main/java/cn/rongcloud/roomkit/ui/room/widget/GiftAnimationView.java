package cn.rongcloud.roomkit.ui.room.widget;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.message.RCChatroomLike;
import cn.rongcloud.roomkit.ui.room.widget.like.FavAnimation;

/**
 * @author gyn
 * @date 2021/10/28
 */
public class GiftAnimationView extends FrameLayout {

    // 动画
    private FavAnimation mFavAnimation;
    private GestureDetector mGestureDetector;
    private OnClickBackgroundListener mOnBottomOptionClickListener;

    public GiftAnimationView(@NonNull Context context) {
        this(context, null);
    }

    public GiftAnimationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GiftAnimationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setOnBottomOptionClickListener(OnClickBackgroundListener mOnBottomOptionClickListener) {
        this.mOnBottomOptionClickListener = mOnBottomOptionClickListener;
    }

    private void initView() {
// 喜欢的动画
        mFavAnimation = new FavAnimation(getContext());
        mFavAnimation.addLikeImages(
                R.drawable.ic_present_0,
                R.drawable.ic_present_1,
                R.drawable.ic_present_2,
                R.drawable.ic_present_3,
                R.drawable.ic_present_4,
                R.drawable.ic_present_5,
                R.drawable.ic_present_6,
                R.drawable.ic_present_7,
                R.drawable.ic_present_8,
                R.drawable.ic_present_9);
        // 手势监听
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                showFov(new Point((int) e.getX(), (int) e.getY()));
                if (mOnBottomOptionClickListener != null) {
                    mOnBottomOptionClickListener.onSendLikeMessage(new RCChatroomLike());
                }
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });
        mGestureDetector.setIsLongpressEnabled(false);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (audioRecordManager.isRecording()) {
//            return super.dispatchTouchEvent(ev);
//        }
//        int safeHeight = mOptionContainer.getTop();
//        if (mInputBar.getVisibility() == VISIBLE) {
//            safeHeight = mInputBar.getTop();
//        }
//        if (ev.getY() > safeHeight) {
//            return super.dispatchTouchEvent(ev);
//        } else {
        mGestureDetector.onTouchEvent(ev);
        return true;
//        }
    }

    /**
     * 显示礼物动画
     *
     * @param from
     */
    public void showFov(Point from) {
        if (from != null) {
            mFavAnimation.addFavor(this, 300, 1500, from, null);
        }
    }

    public interface OnClickBackgroundListener {
        void onSendLikeMessage(RCChatroomLike rcChatroomLike);
    }
}
