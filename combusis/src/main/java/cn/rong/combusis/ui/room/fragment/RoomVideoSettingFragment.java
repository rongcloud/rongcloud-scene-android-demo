package cn.rong.combusis.ui.room.fragment;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;

import cn.rong.combusis.R;
import cn.rong.combusis.common.base.BaseBottomSheetDialogFragment;
import cn.rongcloud.liveroom.api.RCLiveEngine;
import cn.rongcloud.rtc.api.RCRTCEngine;
import cn.rongcloud.rtc.base.RCRTCParamsType;

/**
 * @author 李浩
 * @date 2021/11/19
 */
public class RoomVideoSettingFragment extends BaseBottomSheetDialogFragment {

    private TextView tvTitle;
    private View vDivider;
    private TextView tvDpi;
    private RecyclerView rvDpi;
    private TextView tvFrame;
    private RecyclerView rvFrame;
    private TextView tvCodeRateTitle;
    private TextView tvCodeRate;
    private RCRTCParamsType.RCRTCVideoResolution resolution;
    private RCRTCParamsType.RCRTCVideoFps videoFps;
    private OnVideoConfigSetting onVideoConfigSetting;

    public RoomVideoSettingFragment(String resolution, String fps, OnVideoConfigSetting onVideoConfigSetting) {
        super(R.layout.fragment_room_video_settings);
        if (!TextUtils.isEmpty(resolution)) {
            String[] xes = resolution.split("x");
            this.resolution = RCRTCParamsType.RCRTCVideoResolution.parseVideoResolution(Integer.parseInt(xes[0]), Integer.parseInt(xes[1]));
        } else {
            this.resolution = RCRTCEngine.getInstance().getDefaultVideoStream().getVideoConfig().getVideoResolution();
        }
        if (!TextUtils.isEmpty(fps)) {
            this.videoFps = RCRTCParamsType.RCRTCVideoFps.parseVideoFps(Integer.parseInt(fps));
        } else {
            this.videoFps = RCRTCEngine.getInstance().getDefaultVideoStream().getVideoConfig().getVideoFps();
        }
        this.onVideoConfigSetting = onVideoConfigSetting;
    }

    @Override
    public void initView() {

        tvTitle = (TextView) getView().findViewById(R.id.tv_title);
        vDivider = (View) getView().findViewById(R.id.v_divider);
        tvDpi = (TextView) getView().findViewById(R.id.tv_dpi);
        rvDpi = (RecyclerView) getView().findViewById(R.id.rv_dpi);
        tvFrame = (TextView) getView().findViewById(R.id.tv_frame);
        rvFrame = (RecyclerView) getView().findViewById(R.id.rv_frame);
        tvCodeRateTitle = (TextView) getView().findViewById(R.id.tv_code_rate_title);
        tvCodeRate = (TextView) getView().findViewById(R.id.tv_code_rate);

        initResolutionRatio();
        initFps();
    }

    /**
     * 帧率
     */
    private void initFps() {
        ArrayList<RCRTCParamsType.RCRTCVideoFps> rcrtcVideoFps = new ArrayList<>();
        rcrtcVideoFps.add(RCRTCParamsType.RCRTCVideoFps.Fps_10);
        rcrtcVideoFps.add(RCRTCParamsType.RCRTCVideoFps.Fps_15);
        rcrtcVideoFps.add(RCRTCParamsType.RCRTCVideoFps.Fps_24);
        rcrtcVideoFps.add(RCRTCParamsType.RCRTCVideoFps.Fps_30);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 4);
        rvFrame.setLayoutManager(gridLayoutManager);
        FpsAdapter fpsAdapter = new FpsAdapter();
        rvFrame.setAdapter(fpsAdapter);
        fpsAdapter.setNewInstance(rcrtcVideoFps);
        fpsAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                videoFps = rcrtcVideoFps.get(position);
                RCLiveEngine.getInstance().setVideoFps(videoFps, null);
                if (onVideoConfigSetting != null) onVideoConfigSetting.updateVideoFps(videoFps);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 构建分辨率
     */
    private void initResolutionRatio() {
        ArrayList<RCRTCParamsType.RCRTCVideoResolution> videoResolutions = new ArrayList<>();
        videoResolutions.add(RCRTCParamsType.RCRTCVideoResolution.RESOLUTION_480_640);
        videoResolutions.add(RCRTCParamsType.RCRTCVideoResolution.RESOLUTION_480_720);
        videoResolutions.add(RCRTCParamsType.RCRTCVideoResolution.RESOLUTION_720_1280);
        videoResolutions.add(RCRTCParamsType.RCRTCVideoResolution.RESOLUTION_1080_1920);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        rvDpi.setLayoutManager(gridLayoutManager);
        ResolutionAdapter resolutionAdapter = new ResolutionAdapter();
        rvDpi.setAdapter(resolutionAdapter);
        resolutionAdapter.setNewInstance(videoResolutions);
        resolutionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                resolution = videoResolutions.get(position);
                RCLiveEngine.getInstance().setVideoResolution(resolution, null);
                if (onVideoConfigSetting != null)
                    onVideoConfigSetting.updateVideoResolution(resolution);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
    }

    /**
     * 设置推荐码率
     */
    private void setTvCodeRate() {
        if (resolution == RCRTCParamsType.RCRTCVideoResolution.RESOLUTION_480_640) {
            setTvCodeRateForFps(900);
        } else if (resolution == RCRTCParamsType.RCRTCVideoResolution.RESOLUTION_480_720) {
            setTvCodeRateForFps(1000);
        } else if (resolution == RCRTCParamsType.RCRTCVideoResolution.RESOLUTION_720_1280) {
            setTvCodeRateForFps(2200);
        } else if (resolution == RCRTCParamsType.RCRTCVideoResolution.RESOLUTION_1080_1920) {
            setTvCodeRateForFps(4000);
        }
    }

    private void setTvCodeRateForFps(int codeRate) {
        if (videoFps == RCRTCParamsType.RCRTCVideoFps.Fps_10 || videoFps == RCRTCParamsType.RCRTCVideoFps.Fps_15) {
            tvCodeRate.setText(codeRate + "kbps");
        } else {
            tvCodeRate.setText((int) (codeRate * 1.5) + "kbps");
        }
    }

    public interface OnVideoConfigSetting {
        void updateVideoResolution(RCRTCParamsType.RCRTCVideoResolution resolution);

        void updateVideoFps(RCRTCParamsType.RCRTCVideoFps fps);
    }

    class FpsAdapter extends BaseQuickAdapter<RCRTCParamsType.RCRTCVideoFps, BaseViewHolder> {

        public FpsAdapter() {
            super(R.layout.item_video_setting_layout);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, RCRTCParamsType.RCRTCVideoFps fps) {
            TextView textView = baseViewHolder.getView(R.id.tv_id);
            if (fps == RCRTCParamsType.RCRTCVideoFps.Fps_10) {
                textView.setText("10");
            } else if (fps == RCRTCParamsType.RCRTCVideoFps.Fps_15) {
                textView.setText("15");
            } else if (fps == RCRTCParamsType.RCRTCVideoFps.Fps_24) {
                textView.setText("24");
            } else if (fps == RCRTCParamsType.RCRTCVideoFps.Fps_30) {
                textView.setText("30");
            }
            if (videoFps == fps) {
                textView.setBackground(requireContext().getDrawable(R.drawable.shape_video_setting_selected_bg));
                textView.setTextColor(Color.parseColor("#EF499A"));
                setTvCodeRate();
            } else {
                textView.setBackground(requireContext().getDrawable(R.drawable.shape_video_setting_unselected_bg));
                textView.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }
    }

    class ResolutionAdapter extends BaseQuickAdapter<RCRTCParamsType.RCRTCVideoResolution, BaseViewHolder> {

        public ResolutionAdapter() {
            super(R.layout.item_video_setting_layout);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, RCRTCParamsType.RCRTCVideoResolution rcrtcVideoResolution) {
            TextView textView = baseViewHolder.getView(R.id.tv_id);
            if (rcrtcVideoResolution == RCRTCParamsType.RCRTCVideoResolution.RESOLUTION_480_640) {
                textView.setText("640 * 480 (4:3)");
            } else if (rcrtcVideoResolution == RCRTCParamsType.RCRTCVideoResolution.RESOLUTION_480_720) {
                textView.setText("720 * 480 (3:2)");
            } else if (rcrtcVideoResolution == RCRTCParamsType.RCRTCVideoResolution.RESOLUTION_720_1280) {
                textView.setText("1280 * 720 (16:9)");
            } else if (rcrtcVideoResolution == RCRTCParamsType.RCRTCVideoResolution.RESOLUTION_1080_1920) {
                textView.setText("1920 * 1080 (16:9)");
            }
            if (resolution == rcrtcVideoResolution) {
                textView.setBackground(requireContext().getDrawable(R.drawable.shape_video_setting_selected_bg));
                textView.setTextColor(Color.parseColor("#EF499A"));
                //设置推荐码率
                setTvCodeRate();
            } else {
                textView.setBackground(requireContext().getDrawable(R.drawable.shape_video_setting_unselected_bg));
                textView.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }
    }
}
