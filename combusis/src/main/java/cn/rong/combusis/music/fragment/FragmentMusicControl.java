package cn.rong.combusis.music.fragment;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.SwitchCompat;

import com.basis.mvp.BasePresenter;
import com.basis.ui.BaseFragment;
import com.kit.cache.SharedPreferUtil;

import cn.rong.combusis.R;
import cn.rong.combusis.common.utils.UIKit;
import cn.rongcloud.rtc.api.RCRTCAudioMixer;
import cn.rongcloud.rtc.api.RCRTCEngine;

public class FragmentMusicControl extends BaseFragment implements SeekBar.OnSeekBarChangeListener {
    final static String EAR_MONITORING = "key_earMonitoring_";
    private String roomId;
    private AppCompatSeekBar sbAudio, sbRemote, sbMic;
    private TextView tvAudio, tvRemote, tvMic;
    private SwitchCompat sbChecked;

    public static FragmentMusicControl getInstance(String roomId) {
        FragmentMusicControl f = new FragmentMusicControl();
        Bundle b = new Bundle();
        b.putString(UIKit.KEY_BASE, roomId);
        f.setArguments(b);
        return f;
    }

    @Override
    public BasePresenter createPresent() {
        return null;
    }

    @Override
    public void initListener() {
    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_music_control;
    }

    @Override
    public void init() {
        roomId = getArguments().getString(UIKit.KEY_BASE);
        sbAudio = (AppCompatSeekBar) getView(R.id.sb_local_audio_setting);
        tvAudio = (TextView) getView(R.id.tv_local_audio_value);

        sbRemote = (AppCompatSeekBar) getView(R.id.sb_remote_audio_setting);
        tvRemote = (TextView) getView(R.id.tv_remote_audio_value);

        sbMic = (AppCompatSeekBar) getView(R.id.sb_mic_audio_setting);
        tvMic = (TextView) getView(R.id.tv_mic_audio_value);

        int audioVolume = RCRTCAudioMixer.getInstance().getPlaybackVolume();
        int mixVolume = RCRTCAudioMixer.getInstance().getMixingVolume();
        int micVolume = RCRTCEngine.getInstance().getDefaultAudioStream().getRecordingVolume();

        sbAudio.setProgress(audioVolume);
        sbAudio.setOnSeekBarChangeListener(this);
        sbRemote.setProgress(mixVolume);
        sbRemote.setOnSeekBarChangeListener(this);
        sbMic.setProgress(micVolume);
        sbMic.setOnSeekBarChangeListener(this);

        tvAudio.setText("" + audioVolume);
        tvRemote.setText("" + mixVolume);
        tvMic.setText("" + micVolume);
        // 获取缓存的
        sbChecked = (SwitchCompat) getView(R.id.sw_checked);
        boolean enable = SharedPreferUtil.getBoolean(EAR_MONITORING + roomId);
        setEarMonitoringAndCache(enable);
        sbChecked.setChecked(enable);
        sbChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                setEarMonitoringAndCache(checked);
            }
        });
    }

    /**
     * 设置耳返
     */
    void setEarMonitoringAndCache(boolean enable) {
        RCRTCEngine.getInstance().getDefaultAudioStream().enableEarMonitoring(enable);//耳返
        SharedPreferUtil.set(EAR_MONITORING + roomId, enable);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        if (seekBar == sbAudio) {
            tvAudio.setText(progress + "");
            RCRTCAudioMixer.getInstance().setPlaybackVolume(progress);
        } else if (seekBar == sbRemote) {
            tvRemote.setText(progress + "");
            RCRTCAudioMixer.getInstance().setMixingVolume(progress);
        } else if (seekBar == sbMic) {
            tvMic.setText(progress + "");
            RCRTCEngine.getInstance().getDefaultAudioStream().adjustRecordingVolume(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
