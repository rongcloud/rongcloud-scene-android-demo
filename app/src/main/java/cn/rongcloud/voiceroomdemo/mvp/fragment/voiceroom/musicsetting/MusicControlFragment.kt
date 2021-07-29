/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.musicsetting

import android.widget.SeekBar
import cn.rongcloud.rtc.api.RCRTCAudioMixer
import cn.rongcloud.rtc.api.RCRTCEngine
import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.mvp.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_music_control.*

/**
 * @author gusd
 * @Date 2021/07/06
 */
class MusicControlFragment(view: IMusicControlView, roomId: String) :
    BaseFragment<MusicControlPresenter, IMusicControlView>(
        R.layout.fragment_music_control
    ), IMusicControlView by view, SeekBar.OnSeekBarChangeListener {
    override fun initPresenter(): MusicControlPresenter {
        return MusicControlPresenter(this)
    }

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

    }


    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        when (seekBar) {
            sb_local_audio_setting -> {
                tv_local_audio_value.text = "$progress"
                RCRTCAudioMixer.getInstance().playbackVolume = progress
            }
            sb_mic_audio_setting -> {
                tv_mic_audio_value.text = "$progress"
                RCRTCEngine.getInstance().defaultAudioStream.adjustRecordingVolume(progress)
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