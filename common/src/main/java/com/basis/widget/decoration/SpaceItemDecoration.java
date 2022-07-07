package com.basis.widget.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final boolean mShowTop;
    private final boolean mShowBottom;
    private int mLeft;
    private int mTop;
    private int mRight;
    private int mBottom;
    private int mStartSpace;
    private int mEndSpace;
    private int[] mSkipPositions;

    public SpaceItemDecoration(@Dimension int left, @Dimension int top, @Dimension int right, @Dimension int bottom, int[] skipPositions, boolean showTop, boolean showBottom) {
        this(left, top, right, bottom, 0, 0, skipPositions, showTop, showBottom);
    }

    public SpaceItemDecoration(@Dimension int left, @Dimension int top, @Dimension int right, @Dimension int bottom, @Dimension int startSpace, @Dimension int endSpace, int[] skipPositions, boolean showTop, boolean showBottom) {
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;
        mSkipPositions = skipPositions == null ? new int[]{-1} : skipPositions;
        mShowTop = showTop;
        mShowBottom = showBottom;
        mStartSpace = startSpace;
        mEndSpace = endSpace;

        Arrays.sort(mSkipPositions);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getAbsoluteAdapterPosition();

        // first holder
        if (itemPosition == 0) {
            outRect.set(mLeft + mStartSpace, mShowTop && mTop > 0 ? mTop : 0, mRight, mBottom);
        }

        // last holder
        else if (parent.getAdapter() != null && itemPosition == parent.getAdapter().getItemCount() - 1) {
            outRect.set(mLeft, mTop, mRight + mEndSpace, mShowBottom && mBottom > 0 ? mBottom : 0);
        }

        // skip
        else if (Arrays.binarySearch(mSkipPositions, itemPosition) >= 0) {
        }

        // middle position
        else if (itemPosition > 0 && parent.getAdapter() != null && itemPosition < parent.getAdapter().getItemCount() - 1) {
            outRect.set(mLeft, mTop, mRight, mBottom);
        }
    }

    public static class Builder {

        private int mLeft;
        private int mTop;
        private int mRight;
        private int mBottom;
        private int[] mSkipPositions;
        private boolean mShowTop;
        private boolean mShowBottom;
        private int mStartSpace;
        private int mEndSpace;

        public static Builder start() {
            return new Builder();
        }

        public Builder setLeft(@Dimension int left) {
            mLeft = left;
            return this;
        }

        public Builder setTop(@Dimension int top) {
            mTop = top;
            return this;
        }

        public Builder setRight(@Dimension int right) {
            mRight = right;
            return this;
        }

        public Builder setBottom(@Dimension int bottom) {
            mBottom = bottom;
            return this;
        }

        public Builder setStartSpace(@Dimension int startSpace) {
            mStartSpace = startSpace;
            return this;
        }

        public Builder setEndSpace(@Dimension int endSpace) {
            mEndSpace = endSpace;
            return this;
        }

        public Builder setSkipPosition(int... positions) {
            mSkipPositions = positions;
            return this;
        }

        public Builder setShowFirstHolderTopDecoration() {
            mShowTop = true;
            return this;
        }

        public Builder setShowLastHolderBottomDecoration() {
            mShowBottom = true;
            return this;
        }

        public RecyclerView.ItemDecoration build() {
            return new SpaceItemDecoration(mLeft, mTop, mRight, mBottom, mStartSpace, mEndSpace, mSkipPositions, mShowTop, mShowBottom);
        }
    }
}
