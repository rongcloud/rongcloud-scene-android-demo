/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.musicsetting

import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.SeekBar
import cn.rongcloud.annotation.HiltBinding
import cn.rongcloud.rtc.api.RCRTCAudioMixer
import cn.rongcloud.rtc.api.RCRTCEngine
import cn.rongcloud.voiceroomdemo.R
import com.rongcloud.common.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_music_control.*
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/07/06
 */
@HiltBinding(value = IMusicControlView::class)
@AndroidEntryPoint
class MusicControlFragment(view: IMusicControlView) :
    BaseFragment(
        R.layout.fragment_music_control
    ), IMusicControlView by view, SeekBar.OnSeekBarChangeListener {

    @Inject
    lateinit var presenter: MusicControlPresenter

    override fun initView() {
        sb_local_audio_setting.setOnSeekBarChangeListener(this)
        sb_mic_audio_setting.setOnSeekBarChangeListener(this)
        sb_remote_audio_setting.setOnSeekBarChangeListener(this)
        tv_local_audio_value.text = "${RCRTCAudioMixer.getInstance().playbackVolume}"
        sb_local_audio_setting.progress = RCRTCAudioMixer.getInstance().playbackVolume

        tv_mic_audio_value.text = "${RCRTCEngine.getInstance().defaultAudioStream.recordingVolume}"
        sb_mic_audio_setting.progress = RCRTCEngine.getInstance().defaultAudioStream.recordingVolume

        tv_remote_audio_value.text =
            "${RCRTCAudioMixer.getInstance().mixingVolume}"
        sb_remote_audio_setting.progress = RCRTCAudioMixer.getInstance().mixingVolume
        // 默认关闭耳返
//        sw_checked.setChecked(false)
//        RCRTCEngine.getInstance().defaultAudioStream.enableEarMonitoring(false)
//        sw_checked.setOnCheckedChangeListener { compoundButton: CompoundButton, checked: Boolean ->
//            Log.e("MusicControlFragment","checked = "+checked);
//            RCRTCEngine.getInstance().defaultAudioStream.enableEarMonitoring(checked)//耳返
//        }
    }


    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        when (seekBar) {
            sb_local_audio_setting -> {
                tv_local_audio_value.text = "$progress"
                RCRTCAudioMixer.getInstance().playbackVolume = progress
            }
            sb_mic_audio_setting -> {
                tv_mic_audio_value.text = "$progress"
                RCRTCEngine.getInstance().defaultAudioStream.adjustRecordingVolume(progress)//麦克风音量
            }
            sb_remote_audio_setting -> {
                tv_remote_audio_value.text = "$progress"
                RCRTCAudioMixer.getInstance().mixingVolume = progress
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }


}