package com.meihu.beauty.bean;

import com.meihu.beauty.R;
import com.meihu.beautylibrary.bean.MHCommonBean;

public class MeiYanOneKeyBean extends MHCommonBean {

    private int mName;
    private int mThumb;
    private boolean mChecked;
    private MeiYanOneKeyValue mValue;
    private MeiYanValueBean mResultValue;
    private int mProgress = 50;

    public MeiYanOneKeyBean(int name, int thumb, String key) {
        mName = name;
        mThumb = thumb;
        mKey = key;
        createOneKeyValue();
        if (mValue != null) {
            mResultValue = new MeiYanValueBean();
        }
    }

    public MeiYanOneKeyBean(int name, int thumb, boolean checked, String key) {
        this(name, thumb, key);
        mChecked = checked;
    }

    public int getName() {
        return mName;
    }

    public int getThumb() {
        return mThumb;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        mProgress = progress;
    }


    public MeiYanValueBean calculateValue(float rate) {
        if (mValue != null && mResultValue != null) {
            mValue.calculateValue(mResultValue, rate);
        }
        return mResultValue;
    }


    private void createOneKeyValue() {
        if (mName == R.string.beauty_mh_biaozhun) {
            mValue = new MeiYanOneKeyValue(
                    new MeiYanOneKeyValue.Value(3, 3),
                    new MeiYanOneKeyValue.Value(3, 3),
                    new MeiYanOneKeyValue.Value(2, 2),
                    new MeiYanOneKeyValue.Value(37, 44),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(68, 84),
                    new MeiYanOneKeyValue.Value(46, 56),
                    new MeiYanOneKeyValue.Value(49, 58),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(35, 41),
                    new MeiYanOneKeyValue.Value(0, 0),
                    new MeiYanOneKeyValue.Value(50, 55)
            );
        } else if (mName == R.string.beauty_mh_youya) {
            mValue = new MeiYanOneKeyValue(
                    new MeiYanOneKeyValue.Value(2, 2),
                    new MeiYanOneKeyValue.Value(6, 6),
                    new MeiYanOneKeyValue.Value(5, 5),
                    new MeiYanOneKeyValue.Value(22, 32),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(46, 58),
                    new MeiYanOneKeyValue.Value(28, 44),
                    new MeiYanOneKeyValue.Value(50, 67),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(23, 30),
                    new MeiYanOneKeyValue.Value(77, 86),
                    new MeiYanOneKeyValue.Value(13, 27),
                    new MeiYanOneKeyValue.Value(0, 0),
                    new MeiYanOneKeyValue.Value(74, 85)
            );
        } else if (mName == R.string.beauty_mh_jingzhi) {
            mValue = new MeiYanOneKeyValue(
                    new MeiYanOneKeyValue.Value(8, 8),
                    new MeiYanOneKeyValue.Value(4, 4),
                    new MeiYanOneKeyValue.Value(1, 1),
                    new MeiYanOneKeyValue.Value(56, 67),
                    new MeiYanOneKeyValue.Value(20, 30),
                    new MeiYanOneKeyValue.Value(61, 73),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(48, 58),
                    new MeiYanOneKeyValue.Value(65, 78),
                    new MeiYanOneKeyValue.Value(64, 74),
                    new MeiYanOneKeyValue.Value(32, 42),
                    new MeiYanOneKeyValue.Value(66, 73),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(0, 25),
                    new MeiYanOneKeyValue.Value(20, 29)
            );
        } else if (mName == R.string.beauty_mh_keai) {
            mValue = new MeiYanOneKeyValue(
                    new MeiYanOneKeyValue.Value(5, 5),
                    new MeiYanOneKeyValue.Value(5, 5),
                    new MeiYanOneKeyValue.Value(0, 0),
                    new MeiYanOneKeyValue.Value(66, 73),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(55, 68),
                    new MeiYanOneKeyValue.Value(50, 60),
                    new MeiYanOneKeyValue.Value(74, 84),
                    new MeiYanOneKeyValue.Value(80, 100),
                    new MeiYanOneKeyValue.Value(85, 100),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(37, 47),
                    new MeiYanOneKeyValue.Value(0, 15)
            );
        } else if (mName == R.string.beauty_mh_ziran) {
            mValue = new MeiYanOneKeyValue(
                    new MeiYanOneKeyValue.Value(3, 3),
                    new MeiYanOneKeyValue.Value(4, 4),
                    new MeiYanOneKeyValue.Value(3, 3),
                    new MeiYanOneKeyValue.Value(10, 28),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(20, 37),
                    new MeiYanOneKeyValue.Value(50, 55),
                    new MeiYanOneKeyValue.Value(55, 65),
                    new MeiYanOneKeyValue.Value(48, 55),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(30, 40),
                    new MeiYanOneKeyValue.Value(15, 30),
                    new MeiYanOneKeyValue.Value(10, 20)
            );
        } else if (mName == R.string.beauty_mh_wanghong) {
            mValue = new MeiYanOneKeyValue(
                    new MeiYanOneKeyValue.Value(9, 9),
                    new MeiYanOneKeyValue.Value(5, 5),
                    new MeiYanOneKeyValue.Value(7, 7),
                    new MeiYanOneKeyValue.Value(65, 80),
                    new MeiYanOneKeyValue.Value(52, 62),
                    new MeiYanOneKeyValue.Value(53, 73),
                    new MeiYanOneKeyValue.Value(63, 81),
                    new MeiYanOneKeyValue.Value(78, 90),
                    new MeiYanOneKeyValue.Value(73, 83),
                    new MeiYanOneKeyValue.Value(83, 100),
                    new MeiYanOneKeyValue.Value(65, 78),
                    new MeiYanOneKeyValue.Value(71, 80),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(22, 48),
                    new MeiYanOneKeyValue.Value(48, 58)
            );
        } else if (mName == R.string.beauty_mh_tuosu) {
            mValue = new MeiYanOneKeyValue(
                    new MeiYanOneKeyValue.Value(6, 6),
                    new MeiYanOneKeyValue.Value(4, 4),
                    new MeiYanOneKeyValue.Value(0, 0),
                    new MeiYanOneKeyValue.Value(35, 45),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(37, 47),
                    new MeiYanOneKeyValue.Value(63, 81),
                    new MeiYanOneKeyValue.Value(36, 46),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(50, 58),
                    new MeiYanOneKeyValue.Value(29, 39),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(50, 60),
                    new MeiYanOneKeyValue.Value(50, 62)
            );
        } else if (mName == R.string.beauty_mh_gaoya) {
            mValue = new MeiYanOneKeyValue(
                    new MeiYanOneKeyValue.Value(4, 4),
                    new MeiYanOneKeyValue.Value(6, 6),
                    new MeiYanOneKeyValue.Value(4, 4),
                    new MeiYanOneKeyValue.Value(22, 32),
                    new MeiYanOneKeyValue.Value(0, 27),
                    new MeiYanOneKeyValue.Value(50, 50),
                    new MeiYanOneKeyValue.Value(18, 28),
                    new MeiYanOneKeyValue.Value(12, 22),
                    new MeiYanOneKeyValue.Value(50, 63),
                    new MeiYanOneKeyValue.Value(60, 75),
                    new MeiYanOneKeyValue.Value(37, 54),
                    new MeiYanOneKeyValue.Value(50, 61),
                    new MeiYanOneKeyValue.Value(0, 10),
                    new MeiYanOneKeyValue.Value(56, 70),
                    new MeiYanOneKeyValue.Value(23, 34)
            );
        }
    }


}
