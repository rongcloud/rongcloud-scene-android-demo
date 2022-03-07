/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.roomkit.ui.room.widget.like;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cn.rongcloud.roomkit.ui.room.widget.like.evaluator.CurveEvaluatorRecord;


/**
 * @author baicq 飘心View
 * @Date 2020/07/08
 */
public class FavAnimation {
    private final static String TAG = "FavAnimation";
    private final static int MAX_IN = 200;
    private final static int MAX_SIZE = 150;
    protected final Random mRandom = new Random();
    private final List<Integer> mLikeRes = new ArrayList<>();
    private final Context context;
    protected CurveEvaluatorRecord mEvaluatorRecord;
    protected List<AnimatorSet> mAnimatorSets = new ArrayList<>();

    public FavAnimation(Context context) {
        this.context = context.getApplicationContext();
        mEvaluatorRecord = new CurveEvaluatorRecord();
    }

    public void release() {
        // 取消动画 释放资源
        for (AnimatorSet animatorSet : mAnimatorSets) {
            // 初始化回调方法
            animatorSet.getListeners().clear();
            // 取消动画
            animatorSet.cancel();
        }
        // 释放集合资源
        this.mAnimatorSets.clear();
        this.mEvaluatorRecord.destroy();
    }

    /**
     * 添加 资源文件组
     *
     * @param resIds
     */
    public void addLikeImages(Integer... resIds) {
        mLikeRes.addAll(Arrays.asList(resIds));
    }

    /**
     * 生成 配置参数
     */
    public int[] getPictureInfo(@DrawableRes int resId) {
        // 获取图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 只读取图片，不加载到内存中
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);

        return new int[]{Math.min(options.outWidth, MAX_SIZE), Math.min(options.outHeight, MAX_SIZE)};
    }

    public void addFavor(ViewGroup parent, int enterDuration, int curDuration, @NonNull Point from, @Nullable Point to) {
        // 非空验证
        if (mLikeRes.isEmpty()) {
            Log.e(TAG, "请添加资源文件！");
            return;
        }
        // 随机获取一个资源
        int favorRes = Math.abs(mLikeRes.get(mRandom.nextInt(mLikeRes.size())));
        // 生成 配置参数
        int[] pos = getPictureInfo(favorRes);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(pos[0], pos[1]);
        // 创建一个资源View
        AppCompatImageView favorView = new AppCompatImageView(context);
        favorView.setImageResource(favorRes);
        // 开始执行动画
        this.start(favorView, parent, layoutParams, enterDuration, curDuration, from, to);
    }

    /**
     * 开始执行动画
     *
     * @param child        child
     * @param parent       parent
     * @param layoutParams layoutParams
     */
    private void start(View child, ViewGroup parent, ViewGroup.LayoutParams layoutParams, int enterDuration, int curDuration, @NonNull Point from, @Nullable Point to) {
        // 设置进入动画
        AnimatorSet enterAnimator = generateEnterAnimation(child, enterDuration);
        // 设置路径动画
        ValueAnimator curveAnimator = generateCurveAnimation(child, curDuration, from, to);
        // 执行动画集合
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(curveAnimator, enterAnimator);
        animatorSet.addListener(new AnimationEndListener(child, parent, animatorSet));
        animatorSet.start();
        // add父布局
        parent.addView(child, layoutParams);
    }

    /**
     * 进入动画
     *
     * @return 动画集合
     */
    private AnimatorSet generateEnterAnimation(View child, int mEnterDuration) {
        AnimatorSet enterAnimation = new AnimatorSet();
        enterAnimation.playTogether(
                ObjectAnimator.ofFloat(child, View.ALPHA, 0.2f, 1f),
                ObjectAnimator.ofFloat(child, View.SCALE_X, 0.2f, 1f),
                ObjectAnimator.ofFloat(child, View.SCALE_Y, 0.2f, 1f));
        // 加一些动画差值器
        enterAnimation.setInterpolator(new LinearInterpolator());
        return enterAnimation.setDuration(mEnterDuration);
    }

    private ValueAnimator generateCurveAnimation(View child, int duration, @NonNull Point from, @Nullable Point to) {
        if (null == to) {
            int dx = ((mRandom.nextBoolean() ? 1 : -1) * mRandom.nextInt(MAX_IN));
            int dy = ((mRandom.nextBoolean() ? 1 : -1) * mRandom.nextInt(MAX_IN));
            to = new Point(from.x + dx, from.y + dy);
        }
        // 起点 坐标
        PointF pointStart = new PointF(from);
        PointF pointEnd = new PointF(to);
        // 属性动画
        PointF pointF1 = getTogglePoint(1, from, to);
        PointF pointF2 = getTogglePoint(2, from, to);
        ValueAnimator curveAnimator = ValueAnimator.ofObject(mEvaluatorRecord.getCurrentPath(pointF1, pointF2), pointStart, pointEnd);
        curveAnimator.addUpdateListener(new AnimationLayout.CurveUpdateLister(child));
        curveAnimator.setInterpolator(new LinearInterpolator());
        return curveAnimator.setDuration(duration);
    }

    /**
     * 控制轨迹在触点为中心边长200的矩形中
     *
     * @param scale
     * @param from  控制点
     * @param to    控制点
     * @return
     */
    private PointF getTogglePoint(int scale, Point from, Point to) {
        PointF pointf = new PointF();
        Point c = new Point((from.x + to.x) / 2, (from.y + to.y) / 2);
        int dx = Math.abs(from.x - to.x);
        int dy = Math.abs(from.y - to.y);
        if (dx < 1) dx = 1;
        if (dy < 1) dy = 1;
        pointf.x = ((mRandom.nextBoolean() ? 1 : -1) * mRandom.nextInt(dx)) + c.x;
        // 再Y轴上 为了确保第二个控制点 在第一个点之上,我把Y分成了上下两半
//        pointf.y = (float) mRandom.nextInt((control.y + 100)) / scale;
        pointf.y = (float) (((mRandom.nextBoolean() ? 1 : -1) * mRandom.nextInt(dy)) + c.y) / scale;
        return pointf;
    }

    /**
     * 动画结束监听器,用于释放无用的资源
     */
    protected class AnimationEndListener extends AnimatorListenerAdapter {
        private final View mChild;
        private final ViewGroup mParent;
        private final AnimatorSet mAnimatorSet;

        protected AnimationEndListener(View child, ViewGroup parent, AnimatorSet animatorSet) {
            this.mChild = child;
            this.mParent = parent;
            this.mAnimatorSet = animatorSet;
            // 缓存
            mAnimatorSets.add(mAnimatorSet);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            // 动画结束 移除View
            this.mParent.removeView(mChild);
            // 从集合中移除
            mAnimatorSets.remove(mAnimatorSet);
        }
    }
}
