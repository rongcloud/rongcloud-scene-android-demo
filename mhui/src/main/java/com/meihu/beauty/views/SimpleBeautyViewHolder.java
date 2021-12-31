package com.meihu.beauty.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.SeekBar;
import android.widget.TextView;

import com.meihu.beauty.R;
import com.meihu.beauty.interfaces.IBeautyViewHolder;
import com.meihu.beauty.utils.SimpleDataManager;

public class SimpleBeautyViewHolder extends AbsViewHolder implements IBeautyViewHolder, View.OnClickListener {

    private TextView mTvMeiBai;
    private TextView mTvMoPi;
    private TextView mTvHongRun;
    private boolean mShowed;
    private VisibleListener mVisibleListener;

    public SimpleBeautyViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_beauty_simple;
    }

    @Override
    public void init() {
        findViewById(R.id.btn_hide).setOnClickListener(this);
        SeekBar seekBarMeiBai = findViewById(R.id.seek_bar_meibai);
        SeekBar seekBarMoPi = findViewById(R.id.seek_bar_mopi);
        SeekBar seekBarHongRun = findViewById(R.id.seek_bar_hongrun);
        mTvMeiBai = findViewById(R.id.text_meibai);
        mTvMoPi = findViewById(R.id.text_mopi);
        mTvHongRun = findViewById(R.id.text_hongrun);
        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int id = seekBar.getId();
                if (id == R.id.seek_bar_meibai) {
                    if (mTvMeiBai != null) {
                        mTvMeiBai.setText(String.valueOf(progress));
                    }
                    SimpleDataManager.getInstance().setMeiBai(progress);
                } else if (id == R.id.seek_bar_mopi) {
                    if (mTvMoPi != null) {
                        mTvMoPi.setText(String.valueOf(progress));
                    }
                    SimpleDataManager.getInstance().setMoPi(progress);
                } else if (id == R.id.seek_bar_hongrun) {
                    if (mTvHongRun != null) {
                        mTvHongRun.setText(String.valueOf(progress));
                    }
                    SimpleDataManager.getInstance().setHongRun(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
        seekBarMeiBai.setOnSeekBarChangeListener(listener);
        seekBarMoPi.setOnSeekBarChangeListener(listener);
        seekBarHongRun.setOnSeekBarChangeListener(listener);

        seekBarMeiBai.setProgress(SimpleDataManager.getInstance().getMeiBai());
        seekBarMoPi.setProgress(SimpleDataManager.getInstance().getMoPi());
        seekBarHongRun.setProgress(SimpleDataManager.getInstance().getHongRun());

    }

    @Override
    public void show() {
        if (mVisibleListener != null) {
            mVisibleListener.onVisibleChanged(true);
        }
        if (mParentView != null && mContentView != null) {
            ViewParent parent = mContentView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mContentView);
            }
            mParentView.addView(mContentView);
        }
        mShowed = true;
    }

    @Override
    public void hide() {
        removeFromParent();
        if (mVisibleListener != null) {
            mVisibleListener.onVisibleChanged(false);
        }
        mShowed = false;
        SimpleDataManager.getInstance().saveBeautyValue();
    }

    @Override
    public boolean isShowed() {
        return mShowed;
    }

    @Override
    public void setVisibleListener(VisibleListener visibleListener) {
        mVisibleListener = visibleListener;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_hide) {
            hide();
        }
    }


}
