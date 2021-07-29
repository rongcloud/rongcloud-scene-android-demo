/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.utils

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager


/**
 * @author gusd
 * @Date 2021/07/16
 */
object AudioManagerUtil : BroadcastReceiver() {


    private var lastModel: Int = 0
    private lateinit var audioManager: AudioManager

    fun init(context: Context) {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        lastModel = audioManager.mode

        context.registerReceiver(this, IntentFilter().apply {
            addAction(Intent.ACTION_HEADSET_PLUG)
            addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
            addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        })
    }

    fun changeToSpeaker() {
        with(audioManager) {
            mode = AudioManager.MODE_IN_COMMUNICATION
            stopBluetoothSco()
            isBluetoothScoOn = false
            isSpeakerphoneOn = true
        }
    }

    fun changeToBluetooth() {
        with(audioManager) {
            mode = AudioManager.MODE_IN_COMMUNICATION
            startBluetoothSco()
            isBluetoothScoOn = true
            isSpeakerphoneOn = false
        }
    }

    fun changeToReceiver() {
        with(audioManager) {
            mode = AudioManager.MODE_IN_COMMUNICATION
            isSpeakerphoneOn = false
        }
    }

    fun changeToNormal() {
        audioManager.mode = AudioManager.MODE_NORMAL
    }

    fun isWiredHeadsetOn(): Boolean {
        return audioManager.isWiredHeadsetOn
    }

    fun isBluetoothA2dpOn(): Boolean {
        return BluetoothAdapter.getDefaultAdapter()?.let { adapter ->
            if (!adapter.isEnabled) {
                return false
            }
            val a2dp: Int =
                adapter.getProfileConnectionState(BluetoothProfile.A2DP)
            return if (a2dp == BluetoothProfile.STATE_CONNECTED || a2dp == BluetoothProfile.STATE_CONNECTING) {
                audioManager.isBluetoothScoOn = true
                return audioManager.isBluetoothScoOn || audioManager.isBluetoothA2dpOn
            } else false
        } ?: false
    }

    fun choiceAudioModel() {
        when {
            isWiredHeadsetOn() -> {
                changeToReceiver()
            }
            isBluetoothA2dpOn() -> {
                changeToBluetooth()
            }
            else -> {
                changeToSpeaker()
            }
        }
    }

    fun dispose() {
        audioManager.mode = lastModel
        if (audioManager.isBluetoothScoOn) {
            audioManager.isBluetoothScoOn = false
            audioManager.stopBluetoothSco()
        }
        audioManager.unloadSoundEffects()

    }

    override fun onReceive(context: Context, intent: Intent) {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        when (intent.action) {
            Intent.ACTION_HEADSET_PLUG -> {
                val state = intent.getIntExtra("state", 0)
                if (state == 0) {// 耳机拔出
                    changeToSpeaker()
                } else if (state == 1) {// 耳机插入
                    changeToReceiver()
                }
            }
            BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED -> {
                val state = intent.getIntExtra(
                    BluetoothHeadset.EXTRA_STATE,
                    BluetoothHeadset.STATE_DISCONNECTED
                )
                updateBluetoothIndication(state)
            }
            AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
                changeToSpeaker()
            }
        }
    }

    private fun updateBluetoothIndication(bluetoothHeadsetState: Int) {
        if (bluetoothHeadsetState == BluetoothProfile.STATE_CONNECTED) {
            changeToBluetooth()
        } else {
            changeToSpeaker()
        }
    }
}