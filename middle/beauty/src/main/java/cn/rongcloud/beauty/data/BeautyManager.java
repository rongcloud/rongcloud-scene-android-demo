package cn.rongcloud.beauty.data;

import static cn.rongcloud.beauty.data.BeautyDataManager.getIntensity;

import android.text.TextUtils;

import com.faceunity.core.controller.facebeauty.FaceBeautyParam;
import com.faceunity.core.model.facebeauty.FaceBeautyFilterEnum;

import cn.rongcloud.beauty.entity.BeautyBean;
import cn.rongcloud.beauty.entity.BeautyCategory;
import cn.rongcloud.fubeautifier.RCRTCFUBeautifierEngine;

/**
 * @author gyn
 * @date 2022/9/20
 */
public class BeautyManager {
    // 注册美颜
    public static int register(byte[] v3data, byte[] authData) {
        return RCRTCFUBeautifierEngine.getInstance().register(null, authData);
    }

    // 是否开启美颜
    public static void setEnable(boolean enable) {
        RCRTCFUBeautifierEngine.getInstance().setBeautyEnable(enable);
    }

    // 由于崩溃或者中途加入，恢复之前的美颜参数
    public static void recoveryHasSet() {
        if (BeautyDataManager.getCurrentBeautyData() != null) {
            for (BeautyCategory currentBeautyDatum : BeautyDataManager.getCurrentBeautyData()) {
                if (TextUtils.equals(currentBeautyDatum.getName(), "滤镜")) {
                    setBeautyIntensity(currentBeautyDatum.getDefaultSelected());
                } else {
                    for (BeautyBean beautyBean : currentBeautyDatum.getBeautyBeanList()) {
                        setBeautyIntensity(beautyBean);
                    }
                }
            }
        }
    }


    // 美颜默认参数
    public static void setDefaultBeautyParams() {
        // 设置滤镜
        RCRTCFUBeautifierEngine.getInstance().setFilter(FaceBeautyFilterEnum.ZIRAN_2, getIntensity(FaceBeautyFilterEnum.ZIRAN_2));
        //
        /*美肤*/
        // 锐化强度
        RCRTCFUBeautifierEngine.getInstance().setSharpenIntensity(getIntensity(FaceBeautyParam.SHARPEN_INTENSITY));
        // 美白强度
        RCRTCFUBeautifierEngine.getInstance().setColorIntensity(getIntensity(FaceBeautyParam.COLOR_INTENSITY));
        // 红润强度
        RCRTCFUBeautifierEngine.getInstance().setRedIntensity(getIntensity(FaceBeautyParam.RED_INTENSITY));
        // 磨皮强度
        RCRTCFUBeautifierEngine.getInstance().setBlurIntensity(getIntensity(FaceBeautyParam.BLUR_INTENSITY));

        /*美型*/
        // 变型强度
        RCRTCFUBeautifierEngine.getInstance().setFaceShapeLevel(getIntensity(FaceBeautyParam.FACE_SHAPE));
        // 设置大眼程度
        RCRTCFUBeautifierEngine.getInstance().setEyeEnlargingIntensity(getIntensity(FaceBeautyParam.EYE_ENLARGING_INTENSITY));
        // 设置V脸程度
        RCRTCFUBeautifierEngine.getInstance().setCheekVIntensity(getIntensity(FaceBeautyParam.CHEEK_V_INTENSITY));
        // 设置瘦鼻程度
        RCRTCFUBeautifierEngine.getInstance().setNoseIntensity(getIntensity(FaceBeautyParam.NOSE_INTENSITY));
        // 设置额头调整程度
        RCRTCFUBeautifierEngine.getInstance().setForeheadIntensity(getIntensity(FaceBeautyParam.FOREHEAD_INTENSITY));
        // 设置嘴巴调整程度
        RCRTCFUBeautifierEngine.getInstance().setMouthIntensity(getIntensity(FaceBeautyParam.MOUTH_INTENSITY));
        // 设置下巴调整程度
        RCRTCFUBeautifierEngine.getInstance().setChinIntensity(getIntensity(FaceBeautyParam.CHIN_INTENSITY));
    }

    public static void setBeautyIntensity(BeautyBean beautyBean) {
        if (beautyBean == null) {
            return;
        }
        String key = beautyBean.getKey();
        float intensity = beautyBean.getIntensity();
        switch (key) {
            /* 美肤相关 */
            case FaceBeautyParam.BLUR_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setBlurIntensity(intensity);
                break;
            case FaceBeautyParam.COLOR_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setColorIntensity(intensity);
                break;
            case FaceBeautyParam.RED_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setRedIntensity(intensity);
                break;
            case FaceBeautyParam.SHARPEN_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setSharpenIntensity(intensity);
                break;
            case FaceBeautyParam.EYE_BRIGHT_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setEyeBrightIntensity(intensity);
                break;
            case FaceBeautyParam.TOOTH_WHITEN_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setToothIntensity(intensity);
                break;
            case FaceBeautyParam.REMOVE_POUCH_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setRemovePouchIntensity(intensity);
                break;
            case FaceBeautyParam.REMOVE_NASOLABIAL_FOLDS_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setRemoveLawPatternIntensity(intensity);
                break;

            /* 美型相关 */
            case FaceBeautyParam.CHEEK_THINNING_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setCheekThinningIntensity(intensity);
                break;
            case FaceBeautyParam.CHEEK_V_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setCheekVIntensity(intensity);
                break;
            case FaceBeautyParam.CHEEK_NARROW_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setCheekNarrowIntensity(intensity);
                break;
            // case FaceBeautyParam.CHEEK_SHORT_INTENSITY:
            //     break;
            case FaceBeautyParam.CHEEK_SMALL_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setCheekSmallIntensity(intensity);
                break;
            case FaceBeautyParam.INTENSITY_CHEEKBONES_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setCheekBonesIntensity(intensity);
                break;
            case FaceBeautyParam.INTENSITY_LOW_JAW_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setLowerJawIntensity(intensity);
                break;
            case FaceBeautyParam.EYE_ENLARGING_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setEyeEnlargingIntensity(intensity);
                break;
            case FaceBeautyParam.EYE_CIRCLE_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setEyeCircleIntensity(intensity);
                break;
            case FaceBeautyParam.CHIN_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setChinIntensity(intensity);
                break;
            case FaceBeautyParam.FOREHEAD_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setForeheadIntensity(intensity);
                break;
            case FaceBeautyParam.NOSE_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setNoseIntensity(intensity);
                break;
            case FaceBeautyParam.MOUTH_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setMouthIntensity(intensity);
                break;
            case FaceBeautyParam.CANTHUS_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setCanthusIntensity(intensity);
                break;
            case FaceBeautyParam.EYE_SPACE_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setEyeSpaceIntensity(intensity);
                break;
            case FaceBeautyParam.EYE_ROTATE_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setEyeRotateIntensity(intensity);
                break;
            case FaceBeautyParam.LONG_NOSE_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setLongNoseIntensity(intensity);
                break;
            case FaceBeautyParam.PHILTRUM_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setPhiltrumIntensity(intensity);
                break;
            case FaceBeautyParam.SMILE_INTENSITY:
                RCRTCFUBeautifierEngine.getInstance().setSmileIntensity(intensity);
                break;

            /* 其余的是滤镜 */
            default:
                RCRTCFUBeautifierEngine.getInstance().setFilter(key, intensity);
                break;
        }
    }
}
