package cn.rongcloud.beauty.data;

import com.faceunity.core.controller.facebeauty.FaceBeautyParam;
import com.faceunity.core.model.facebeauty.FaceBeautyFilterEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rongcloud.beauty.R;
import cn.rongcloud.beauty.entity.BeautyBean;
import cn.rongcloud.beauty.entity.BeautyCategory;

/**
 * @author gyn
 * @date 2022/9/16
 */
public class BeautyDataManager {
    // 设置过的美颜参数，默认取default
    // 这里在内存里持有，也可以根据业务需求写入SharePreference
    public static List<BeautyCategory> beautyCategories;

    public static final Map<String, Float> beautyDefaultParams = new HashMap<String, Float>() {
        {
            // 滤镜 自然2
            put(FaceBeautyFilterEnum.ZIRAN_2, 0.4f);

            // 磨皮强度
            put(FaceBeautyParam.BLUR_INTENSITY, 4.2f);
            // 美白强度
            put(FaceBeautyParam.COLOR_INTENSITY, 0.3f);
            // 红润强度
            put(FaceBeautyParam.RED_INTENSITY, 0.3f);
            // 锐化强度
            put(FaceBeautyParam.SHARPEN_INTENSITY, 0.2f);


            put(FaceBeautyParam.FACE_SHAPE, 1.0f);
            // 设置大眼程度
            put(FaceBeautyParam.EYE_ENLARGING_INTENSITY, 0.4f);
            // 设置V脸程度
            put(FaceBeautyParam.CHEEK_V_INTENSITY, 0.5f);
            // 设置瘦鼻程度
            put(FaceBeautyParam.NOSE_INTENSITY, 0.5f);
            // 设置额头调整程度
            put(FaceBeautyParam.FOREHEAD_INTENSITY, 0.3f);
            // 设置嘴巴调整程度
            put(FaceBeautyParam.MOUTH_INTENSITY, 0.4f);
            // 设置下巴调整程度
            put(FaceBeautyParam.CHIN_INTENSITY, 0.3f);


        }
    };

    public static float getIntensity(String key) {
        if (beautyDefaultParams.containsKey(key)) {
            return beautyDefaultParams.get(key);
        }
        return 0f;
    }


    public static List<BeautyCategory> getDefaultBeautyData() {
        List<BeautyCategory> beautyCategories = new ArrayList<>();
        beautyCategories.add(buildBeautySkinData());
        beautyCategories.add(buildBeautyShapeData());
        beautyCategories.add(buildBeautyFilterData());
        return beautyCategories;
    }

    public static List<BeautyCategory> getCurrentBeautyData() {
        if (beautyCategories != null && beautyCategories.size() > 0) {
            return beautyCategories;
        }
        beautyCategories = getDefaultBeautyData();
        return beautyCategories;
    }

    /**
     * 初始化美肤参数
     *
     * @return ArrayList<BeautyBean>
     */
    public static BeautyCategory buildBeautySkinData() {

        List<BeautyBean> params = new ArrayList<>();
        params.add(
                new BeautyBean(
                        FaceBeautyParam.BLUR_INTENSITY, R.string.beauty_box_heavy_blur_fine,
                        R.drawable.icon_beauty_skin_buffing_close_selector,
                        R.drawable.icon_beauty_skin_buffing_open_selector, getIntensity(FaceBeautyParam.BLUR_INTENSITY), 6f, 0f, 0f));
        params.add(
                new BeautyBean(
                        FaceBeautyParam.COLOR_INTENSITY, R.string.beauty_box_color_level,
                        R.drawable.icon_beauty_skin_color_close_selector,
                        R.drawable.icon_beauty_skin_color_open_selector, getIntensity(FaceBeautyParam.COLOR_INTENSITY)));
        params.add(
                new BeautyBean(
                        FaceBeautyParam.RED_INTENSITY, R.string.beauty_box_red_level,
                        R.drawable.icon_beauty_skin_red_close_selector,
                        R.drawable.icon_beauty_skin_red_open_selector, getIntensity(FaceBeautyParam.RED_INTENSITY)));
        params.add(
                new BeautyBean(
                        FaceBeautyParam.SHARPEN_INTENSITY, R.string.beauty_box_sharpen,
                        R.drawable.icon_beauty_skin_sharpen_close_selector,
                        R.drawable.icon_beauty_skin_sharpen_open_selector, getIntensity(FaceBeautyParam.SHARPEN_INTENSITY)));
        params.add(
                new BeautyBean(
                        FaceBeautyParam.EYE_BRIGHT_INTENSITY, R.string.beauty_box_eye_bright,
                        R.drawable.icon_beauty_skin_eyes_bright_close_selector,
                        R.drawable.icon_beauty_skin_eyes_bright_open_selector));
        params.add(
                new BeautyBean(
                        FaceBeautyParam.TOOTH_WHITEN_INTENSITY, R.string.beauty_box_tooth_whiten,
                        R.drawable.icon_beauty_skin_teeth_close_selector,
                        R.drawable.icon_beauty_skin_teeth_open_selector));
        params.add(
                new BeautyBean(
                        FaceBeautyParam.REMOVE_POUCH_INTENSITY, R.string.beauty_micro_pouch,
                        R.drawable.icon_beauty_skin_dark_circles_close_selector,
                        R.drawable.icon_beauty_skin_dark_circles_open_selector));
        params.add(
                new BeautyBean(
                        FaceBeautyParam.REMOVE_NASOLABIAL_FOLDS_INTENSITY,
                        R.string.beauty_micro_nasolabial,
                        R.drawable.icon_beauty_skin_wrinkle_close_selector,
                        R.drawable.icon_beauty_skin_wrinkle_open_selector));

        return new BeautyCategory("美肤", params, true);
    }

    public static BeautyCategory buildBeautyShapeData() {
        List<BeautyBean> params = new ArrayList<>();
//        params.add(
//                new FaceBeautyBean(
//                        "", R.string.avatar_face_face,
//                        R.drawable.icon_beauty_shape_face_shape_close_selector, R.drawable.icon_beauty_shape_face_shape_open_selector, FaceBeautyBean.ButtonType.SUB_ITEM_BUTTON
//                )
//        );

        //瘦脸
        params.add(
                new BeautyBean(
                        FaceBeautyParam.CHEEK_THINNING_INTENSITY, R.string.beauty_box_cheek_thinning,
                        R.drawable.icon_beauty_shape_face_cheekthin_close_selector, R.drawable.icon_beauty_shape_face_cheekthin_open_selector
                )
        );

        //V脸
        params.add(
                new BeautyBean(
                        FaceBeautyParam.CHEEK_V_INTENSITY, R.string.beauty_box_cheek_v,
                        R.drawable.icon_beauty_shape_face_v_close_selector, R.drawable.icon_beauty_shape_face_v_open_selector, getIntensity(FaceBeautyParam.CHEEK_V_INTENSITY)
                )
        );

        //窄脸
        params.add(
                new BeautyBean(
                        FaceBeautyParam.CHEEK_NARROW_INTENSITY, R.string.beauty_box_cheek_narrow,
                        R.drawable.icon_beauty_shape_face_narrow_close_selector, R.drawable.icon_beauty_shape_face_narrow_open_selector
                )
        );

        //小脸 -> 短脸  --使用的参数是以前小脸的
        // params.add(
        //         new BeautyBean(
        //                 FaceBeautyParam.CHEEK_SHORT_INTENSITY, R.string.beauty_box_cheek_short,
        //                 R.drawable.icon_beauty_shape_face_short_close_selector, R.drawable.icon_beauty_shape_face_short_open_selector
        //         )
        // );

        //小脸 -> 新增
        params.add(
                new BeautyBean(
                        FaceBeautyParam.CHEEK_SMALL_INTENSITY, R.string.beauty_box_cheek_small,
                        R.drawable.icon_beauty_shape_face_little_close_selector, R.drawable.icon_beauty_shape_face_little_open_selector
                )
        );
        // 瘦颧骨
        params.add(
                new BeautyBean(
                        FaceBeautyParam.INTENSITY_CHEEKBONES_INTENSITY, R.string.beauty_box_cheekbones,
                        R.drawable.icon_beauty_shape_cheek_bones_close_selector, R.drawable.icon_beauty_shape_cheek_bones_open_selector
                )
        );
        // 瘦下颌骨
        params.add(
                new BeautyBean(
                        FaceBeautyParam.INTENSITY_LOW_JAW_INTENSITY, R.string.beauty_box_lower_jaw,
                        R.drawable.icon_beauty_shape_lower_jaw_close_selector, R.drawable.icon_beauty_shape_lower_jaw_open_selector
                )
        );
        // 大眼
        params.add(
                new BeautyBean(
                        FaceBeautyParam.EYE_ENLARGING_INTENSITY, R.string.beauty_box_eye_enlarge,
                        R.drawable.icon_beauty_shape_enlarge_eye_close_selector, R.drawable.icon_beauty_shape_enlarge_eye_open_selector, getIntensity(FaceBeautyParam.EYE_ENLARGING_INTENSITY)
                )
        );
        // 圆眼
        params.add(
                new BeautyBean(
                        FaceBeautyParam.EYE_CIRCLE_INTENSITY, R.string.beauty_box_eye_circle,
                        R.drawable.icon_beauty_shape_round_eye_close_selector, R.drawable.icon_beauty_shape_round_eye_open_selector
                )
        );
        // 下巴
        params.add(
                new BeautyBean(
                        FaceBeautyParam.CHIN_INTENSITY, R.string.beauty_box_intensity_chin,
                        R.drawable.icon_beauty_shape_chin_close_selector, R.drawable.icon_beauty_shape_chin_open_selector, getIntensity(FaceBeautyParam.CHIN_INTENSITY), 1f, 0f, 0.5f
                )
        );
        // 额头
        params.add(
                new BeautyBean(
                        FaceBeautyParam.FOREHEAD_INTENSITY, R.string.beauty_box_intensity_forehead,
                        R.drawable.icon_beauty_shape_forehead_close_selector, R.drawable.icon_beauty_shape_forehead_open_selector, getIntensity(FaceBeautyParam.FOREHEAD_INTENSITY), 1f, 0f, 0.5f
                )
        );
        // 瘦鼻
        params.add(
                new BeautyBean(
                        FaceBeautyParam.NOSE_INTENSITY, R.string.beauty_box_intensity_nose,
                        R.drawable.icon_beauty_shape_thin_nose_close_selector, R.drawable.icon_beauty_shape_thin_nose_open_selector, getIntensity(FaceBeautyParam.NOSE_INTENSITY)
                )
        );
        // 嘴型
        params.add(
                new BeautyBean(
                        FaceBeautyParam.MOUTH_INTENSITY, R.string.beauty_box_intensity_mouth,
                        R.drawable.icon_beauty_shape_mouth_close_selector, R.drawable.icon_beauty_shape_mouth_open_selector, getIntensity(FaceBeautyParam.MOUTH_INTENSITY), 1f, 0f, 0.5f
                )
        );
        // 开眼角
        params.add(
                new BeautyBean(
                        FaceBeautyParam.CANTHUS_INTENSITY, R.string.beauty_micro_canthus,
                        R.drawable.icon_beauty_shape_open_eyes_close_selector, R.drawable.icon_beauty_shape_open_eyes_open_selector
                )
        );
        // 眼距
        params.add(
                new BeautyBean(
                        FaceBeautyParam.EYE_SPACE_INTENSITY, R.string.beauty_micro_eye_space,
                        R.drawable.icon_beauty_shape_distance_close_selector, R.drawable.icon_beauty_shape_distance_open_selector, 0.5f, 1f, 0f, 0.5f
                )
        );

        // 眼睛角度
        params.add(
                new BeautyBean(
                        FaceBeautyParam.EYE_ROTATE_INTENSITY, R.string.beauty_micro_eye_rotate,
                        R.drawable.icon_beauty_shape_angle_close_selector, R.drawable.icon_beauty_shape_angle_open_selector, 0.5f, 1f, 0f, 0.5f
                )
        );
        // 长鼻
        params.add(
                new BeautyBean(
                        FaceBeautyParam.LONG_NOSE_INTENSITY, R.string.beauty_micro_long_nose,
                        R.drawable.icon_beauty_shape_proboscis_close_selector, R.drawable.icon_beauty_shape_proboscis_open_selector, 0.5f, 1f, 0f, 0.5f
                )
        );
        // 缩人中
        params.add(
                new BeautyBean(
                        FaceBeautyParam.PHILTRUM_INTENSITY, R.string.beauty_micro_philtrum,
                        R.drawable.icon_beauty_shape_shrinking_close_selector, R.drawable.icon_beauty_shape_shrinking_open_selector, 0.5f, 1f, 0f, 0.5f
                )
        );
        // 微笑嘴角
        params.add(
                new BeautyBean(
                        FaceBeautyParam.SMILE_INTENSITY, R.string.beauty_micro_smile,
                        R.drawable.icon_beauty_shape_smile_close_selector, R.drawable.icon_beauty_shape_smile_open_selector
                )
        );
        // params.add(
        //         new BeautyBean(
        //                 FaceBeautyParam.BROW_HEIGHT_INTENSITY, R.string.beauty_brow_height,
        //                 R.drawable.icon_beauty_shape_brow_height_close_selector, R.drawable.icon_beauty_shape_brow_height_open_selector,
        //                 R.string.brow_height_tips, DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_MID
        //         )
        // );
        // params.add(
        //         new BeautyBean(
        //                 FaceBeautyParam.BROW_SPACE_INTENSITY, R.string.beauty_brow_space,
        //                 R.drawable.icon_beauty_shape_brow_space_close_selector, R.drawable.icon_beauty_shape_brow_space_open_selector,
        //                 R.string.brow_space_tips, DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_MID
        //         )
        // );
        return new BeautyCategory("美型", params, true);
    }

    /**
     * 初始化滤镜参数
     *
     * @return ArrayList<BeautyBean>
     */
    public static BeautyCategory buildBeautyFilterData() {
        List<BeautyBean> filters = new ArrayList<>();

        filters.add(new BeautyBean(FaceBeautyFilterEnum.ORIGIN, R.string.origin, R.mipmap.icon_beauty_filter_cancel, R.mipmap.icon_beauty_filter_cancel, 0, 0, 0));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.ZIRAN_1, R.string.ziran_1, R.drawable.icon_beauty_filter_natural_1_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.ZIRAN_2, R.string.ziran_2, R.drawable.icon_beauty_filter_natural_2_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.ZIRAN_3, R.string.ziran_3, R.drawable.icon_beauty_filter_natural_3_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.ZIRAN_4, R.string.ziran_4, R.drawable.icon_beauty_filter_natural_4_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.ZIRAN_5, R.string.ziran_5, R.drawable.icon_beauty_filter_natural_5_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.ZIRAN_6, R.string.ziran_6, R.drawable.icon_beauty_filter_natural_6_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.ZIRAN_7, R.string.ziran_7, R.drawable.icon_beauty_filter_natural_7_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.ZIRAN_8, R.string.ziran_8, R.drawable.icon_beauty_filter_natural_8_selector, 0.4f));

        filters.add(new BeautyBean(FaceBeautyFilterEnum.ZHIGANHUI_1, R.string.zhiganhui_1, R.drawable.icon_beauty_filter_texture_gray_1_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.ZHIGANHUI_2, R.string.zhiganhui_2, R.drawable.icon_beauty_filter_texture_gray_2_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.ZHIGANHUI_3, R.string.zhiganhui_3, R.drawable.icon_beauty_filter_texture_gray_3_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.ZHIGANHUI_4, R.string.zhiganhui_4, R.drawable.icon_beauty_filter_texture_gray_4_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.ZHIGANHUI_5, R.string.zhiganhui_5, R.drawable.icon_beauty_filter_texture_gray_5_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.ZHIGANHUI_6, R.string.zhiganhui_6, R.drawable.icon_beauty_filter_texture_gray_6_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.ZHIGANHUI_7, R.string.zhiganhui_7, R.drawable.icon_beauty_filter_texture_gray_7_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.ZHIGANHUI_8, R.string.zhiganhui_8, R.drawable.icon_beauty_filter_texture_gray_8_selector, 0.4f));

        filters.add(new BeautyBean(FaceBeautyFilterEnum.MITAO_1, R.string.mitao_1, R.drawable.icon_beauty_filter_peach_1_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.MITAO_2, R.string.mitao_2, R.drawable.icon_beauty_filter_peach_2_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.MITAO_3, R.string.mitao_3, R.drawable.icon_beauty_filter_peach_3_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.MITAO_4, R.string.mitao_4, R.drawable.icon_beauty_filter_peach_4_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.MITAO_5, R.string.mitao_5, R.drawable.icon_beauty_filter_peach_5_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.MITAO_6, R.string.mitao_6, R.drawable.icon_beauty_filter_peach_6_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.MITAO_7, R.string.mitao_7, R.drawable.icon_beauty_filter_peach_7_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.MITAO_8, R.string.mitao_8, R.drawable.icon_beauty_filter_peach_8_selector, 0.4f));

        filters.add(new BeautyBean(FaceBeautyFilterEnum.BAILIANG_1, R.string.bailiang_1, R.drawable.icon_beauty_filter_bailiang_1_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.BAILIANG_2, R.string.bailiang_2, R.drawable.icon_beauty_filter_bailiang_2_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.BAILIANG_3, R.string.bailiang_3, R.drawable.icon_beauty_filter_bailiang_3_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.BAILIANG_4, R.string.bailiang_4, R.drawable.icon_beauty_filter_bailiang_4_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.BAILIANG_5, R.string.bailiang_5, R.drawable.icon_beauty_filter_bailiang_5_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.BAILIANG_6, R.string.bailiang_6, R.drawable.icon_beauty_filter_bailiang_6_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.BAILIANG_7, R.string.bailiang_7, R.drawable.icon_beauty_filter_bailiang_7_selector, 0.4f));

        filters.add(new BeautyBean(FaceBeautyFilterEnum.FENNEN_1, R.string.fennen_1, R.drawable.icon_beauty_filter_fennen_1_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.FENNEN_2, R.string.fennen_2, R.drawable.icon_beauty_filter_fennen_2_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.FENNEN_3, R.string.fennen_3, R.drawable.icon_beauty_filter_fennen_3_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.FENNEN_4, R.string.fennen_4, R.drawable.icon_beauty_filter_fennen_4_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.FENNEN_5, R.string.fennen_5, R.drawable.icon_beauty_filter_fennen_5_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.FENNEN_6, R.string.fennen_6, R.drawable.icon_beauty_filter_fennen_6_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.FENNEN_7, R.string.fennen_7, R.drawable.icon_beauty_filter_fennen_7_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.FENNEN_8, R.string.fennen_8, R.drawable.icon_beauty_filter_fennen_8_selector, 0.4f));

        filters.add(new BeautyBean(FaceBeautyFilterEnum.LENGSEDIAO_1, R.string.lengsediao_1, R.drawable.icon_beauty_filter_lengsediao_1_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.LENGSEDIAO_2, R.string.lengsediao_2, R.drawable.icon_beauty_filter_lengsediao_2_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.LENGSEDIAO_3, R.string.lengsediao_3, R.drawable.icon_beauty_filter_lengsediao_3_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.LENGSEDIAO_4, R.string.lengsediao_4, R.drawable.icon_beauty_filter_lengsediao_4_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.LENGSEDIAO_5, R.string.lengsediao_5, R.drawable.icon_beauty_filter_lengsediao_5_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.LENGSEDIAO_6, R.string.lengsediao_6, R.drawable.icon_beauty_filter_lengsediao_6_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.LENGSEDIAO_7, R.string.lengsediao_7, R.drawable.icon_beauty_filter_lengsediao_7_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.LENGSEDIAO_8, R.string.lengsediao_8, R.drawable.icon_beauty_filter_lengsediao_8_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.LENGSEDIAO_9, R.string.lengsediao_9, R.drawable.icon_beauty_filter_lengsediao_9_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.LENGSEDIAO_10, R.string.lengsediao_10, R.drawable.icon_beauty_filter_lengsediao_10_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.LENGSEDIAO_11, R.string.lengsediao_11, R.drawable.icon_beauty_filter_lengsediao_11_selector, 0.4f));

        filters.add(new BeautyBean(FaceBeautyFilterEnum.NUANSEDIAO_1, R.string.nuansediao_1, R.drawable.icon_beauty_filter_nuansediao_1_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.NUANSEDIAO_2, R.string.nuansediao_2, R.drawable.icon_beauty_filter_nuansediao_2_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.NUANSEDIAO_3, R.string.nuansediao_3, R.drawable.icon_beauty_filter_nuansediao_3_selector, 0.4f));

        filters.add(new BeautyBean(FaceBeautyFilterEnum.GEXING_1, R.string.gexing_1, R.drawable.icon_beauty_filter_gexing_1_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.GEXING_2, R.string.gexing_2, R.drawable.icon_beauty_filter_gexing_2_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.GEXING_3, R.string.gexing_3, R.drawable.icon_beauty_filter_gexing_3_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.GEXING_4, R.string.gexing_4, R.drawable.icon_beauty_filter_gexing_4_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.GEXING_5, R.string.gexing_5, R.drawable.icon_beauty_filter_gexing_5_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.GEXING_6, R.string.gexing_6, R.drawable.icon_beauty_filter_gexing_6_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.GEXING_7, R.string.gexing_7, R.drawable.icon_beauty_filter_gexing_7_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.GEXING_8, R.string.gexing_8, R.drawable.icon_beauty_filter_gexing_8_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.GEXING_9, R.string.gexing_9, R.drawable.icon_beauty_filter_gexing_9_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.GEXING_10, R.string.gexing_10, R.drawable.icon_beauty_filter_gexing_10_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.GEXING_11, R.string.gexing_11, R.drawable.icon_beauty_filter_gexing_11_selector, 0.4f));

        filters.add(new BeautyBean(FaceBeautyFilterEnum.XIAOQINGXIN_1, R.string.xiaoqingxin_1, R.drawable.icon_beauty_filter_xiaoqingxin_1_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.XIAOQINGXIN_2, R.string.xiaoqingxin_2, R.drawable.icon_beauty_filter_xiaoqingxin_2_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.XIAOQINGXIN_3, R.string.xiaoqingxin_3, R.drawable.icon_beauty_filter_xiaoqingxin_3_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.XIAOQINGXIN_4, R.string.xiaoqingxin_4, R.drawable.icon_beauty_filter_xiaoqingxin_4_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.XIAOQINGXIN_5, R.string.xiaoqingxin_5, R.drawable.icon_beauty_filter_xiaoqingxin_5_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.XIAOQINGXIN_6, R.string.xiaoqingxin_6, R.drawable.icon_beauty_filter_xiaoqingxin_6_selector, 0.4f));

        filters.add(new BeautyBean(FaceBeautyFilterEnum.HEIBAI_1, R.string.heibai_1, R.drawable.icon_beauty_filter_heibai_1_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.HEIBAI_2, R.string.heibai_2, R.drawable.icon_beauty_filter_heibai_2_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.HEIBAI_3, R.string.heibai_3, R.drawable.icon_beauty_filter_heibai_3_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.HEIBAI_4, R.string.heibai_4, R.drawable.icon_beauty_filter_heibai_4_selector, 0.4f));
        filters.add(new BeautyBean(FaceBeautyFilterEnum.HEIBAI_5, R.string.heibai_5, R.drawable.icon_beauty_filter_heibai_5_selector, 0.4f));

        return new BeautyCategory("滤镜", filters, false, filters.get(2));
    }
}
