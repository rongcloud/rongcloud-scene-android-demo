package cn.rongcloud.roomkit.ui.room.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.ui.UIStack;

import cn.rongcloud.roomkit.ui.room.AbsRoomActivity;

public class RecyclerViewAtVP2 extends RecyclerView {
    final static String TAG = "RecyclerViewAtVP2";

    public RecyclerViewAtVP2(@NonNull Context context) {
        super(context);
    }

    public RecyclerViewAtVP2(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewAtVP2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int startX, startY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) ev.getX();
                startY = (int) ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                resetEnable(false);
                break;
            case MotionEvent.ACTION_MOVE:
                int endX = (int) ev.getX();
                int endY = (int) ev.getY();
                int disX = Math.abs(endX - startX);
                int disY = Math.abs(endY - startY);
                if (disX > disY) {
                    //如果是纵向滑动，告知父布局不进行时间拦截，交由子布局消费，　requestDisallowInterceptTouchEvent(true)
                    getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(startX - endX));
                } else {
                    getParent().requestDisallowInterceptTouchEvent(canScrollVertically(startY - endY));
//                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                resetEnable(true);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    void resetEnable(boolean enable){
        Activity top = UIStack.getInstance().getTopActivity();
        if (null != top && top instanceof AbsRoomActivity){
            ((AbsRoomActivity)top).setCanSwitch(enable);
        }
    }
}