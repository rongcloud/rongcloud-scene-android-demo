/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.meihu.beauty.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class MeiYanValueBean {
    private int mMeiBai;
    private int mMoPi;
    private int mHongRun;
    private int mLiangDu;
    private int mDaYan;
    private int mMeiMao;
    private int mYanJu;
    private int mYanJiao;
    private int mShouLian;
    private int mZuiXing;
    private int mShouBi;
    private int mXiaBa;
    private int mETou;
    private int mChangBi;
    private int mXueLian;
    private int mKaiYanJiao;

    public static MeiYanValueBean copy(MeiYanValueBean meiYanValue) {
        MeiYanValueBean meiYanValueBean = new MeiYanValueBean();
        meiYanValueBean.setMeiBai(meiYanValue.getMeiBai());
        meiYanValueBean.setMoPi(meiYanValue.getMoPi());
        meiYanValueBean.setHongRun(meiYanValue.getHongRun());
        meiYanValueBean.setLiangDu(meiYanValue.getLiangDu());

        meiYanValueBean.setDaYan(meiYanValue.getDaYan());
        meiYanValueBean.setMeiMao(meiYanValue.getMeiMao());
        meiYanValueBean.setYanJu(meiYanValue.getYanJu());
        meiYanValueBean.setYanJiao(meiYanValue.getYanJiao());
        meiYanValueBean.setShouLian(meiYanValue.getShouLian());
        meiYanValueBean.setZuiXing(meiYanValue.getZuiXing());
        meiYanValueBean.setShouBi(meiYanValue.getShouBi());
        meiYanValueBean.setXiaBa(meiYanValue.getXiaBa());
        meiYanValueBean.setETou(meiYanValue.getETou());
        meiYanValueBean.setChangBi(meiYanValue.getChangBi());
        meiYanValueBean.setXueLian(meiYanValue.getXueLian());
        meiYanValueBean.setKaiYanJiao(meiYanValue.getKaiYanJiao());
        return meiYanValueBean;
    }

    @JSONField(name = "skin_whiting")
    public int getMeiBai() {
        return mMeiBai;
    }

    @JSONField(name = "skin_whiting")
    public void setMeiBai(int meiBai) {
        mMeiBai = meiBai;
    }

    @JSONField(name = "skin_smooth")
    public int getMoPi() {
        return mMoPi;
    }

    @JSONField(name = "skin_smooth")
    public void setMoPi(int moPi) {
        mMoPi = moPi;
    }

    @JSONField(name = "skin_tenderness")
    public int getHongRun() {
        return mHongRun;
    }

    @JSONField(name = "skin_tenderness")
    public void setHongRun(int hongRun) {
        mHongRun = hongRun;
    }

    @JSONField(name = "brightness")
    public int getLiangDu() {
        return mLiangDu;
    }

    @JSONField(name = "brightness")
    public void setLiangDu(int liangDu) {
        mLiangDu = liangDu;
    }

    @JSONField(name = "big_eye")
    public int getDaYan() {
        return mDaYan;
    }

    @JSONField(name = "big_eye")
    public void setDaYan(int daYan) {
        mDaYan = daYan;
    }

    @JSONField(name = "eye_brow")
    public int getMeiMao() {
        return mMeiMao;
    }

    @JSONField(name = "eye_brow")
    public void setMeiMao(int meiMao) {
        mMeiMao = meiMao;
    }

    @JSONField(name = "eye_length")
    public int getYanJu() {
        return mYanJu;
    }

    @JSONField(name = "eye_length")
    public void setYanJu(int yanJu) {
        mYanJu = yanJu;
    }

    @JSONField(name = "eye_corner")
    public int getYanJiao() {
        return mYanJiao;
    }

    @JSONField(name = "eye_corner")
    public void setYanJiao(int yanJiao) {
        mYanJiao = yanJiao;
    }

    @JSONField(name = "face_lift")
    public int getShouLian() {
        return mShouLian;
    }

    @JSONField(name = "face_lift")
    public void setShouLian(int shouLian) {
        mShouLian = shouLian;
    }

    @JSONField(name = "mouse_lift")
    public int getZuiXing() {
        return mZuiXing;
    }

    @JSONField(name = "mouse_lift")
    public void setZuiXing(int zuiXing) {
        mZuiXing = zuiXing;
    }

    @JSONField(name = "nose_lift")
    public int getShouBi() {
        return mShouBi;
    }

    @JSONField(name = "nose_lift")
    public void setShouBi(int shouBi) {
        mShouBi = shouBi;
    }

    @JSONField(name = "chin_lift")
    public int getXiaBa() {
        return mXiaBa;
    }

    @JSONField(name = "chin_lift")
    public void setXiaBa(int xiaBa) {
        mXiaBa = xiaBa;
    }

    @JSONField(name = "forehead_lift")
    public int getETou() {
        return mETou;
    }

    @JSONField(name = "forehead_lift")
    public void setETou(int ETou) {
        mETou = ETou;
    }

    @JSONField(name = "lengthen_noseLift")
    public int getChangBi() {
        return mChangBi;
    }

    @JSONField(name = "lengthen_noseLift")
    public void setChangBi(int changBi) {
        mChangBi = changBi;
    }

    @JSONField(name = "face_shave")
    public int getXueLian() {
        return mXueLian;
    }

    @JSONField(name = "face_shave")
    public void setXueLian(int xueLian) {
        mXueLian = xueLian;
    }

    @JSONField(name = "eye_alat")
    public int getKaiYanJiao() {
        return mKaiYanJiao;
    }

    @JSONField(name = "eye_alat")
    public void setKaiYanJiao(int kaiYanJiao) {
        mKaiYanJiao = kaiYanJiao;
    }

    @Override
    public String toString() {
        return "MeiYanValueBean{" +
                "mMeiBai=" + mMeiBai +
                ", mMoPi=" + mMoPi +
                ", mHongRun=" + mHongRun +
                ", mLiangDu=" + mLiangDu +
                ", mDaYan=" + mDaYan +
                ", mMeiMao=" + mMeiMao +
                ", mYanJu=" + mYanJu +
                ", mYanJiao=" + mYanJiao +
                ", mShouLian=" + mShouLian +
                ", mZuiXing=" + mZuiXing +
                ", mShouBi=" + mShouBi +
                ", mXiaBa=" + mXiaBa +
                ", mETou=" + mETou +
                ", mChangBi=" + mChangBi +
                ", mXueLian=" + mXueLian +
                ", mKaiYanJiao=" + mKaiYanJiao +
                '}';
    }
}
