package com.alilopez.kt_demohilt.core.hardware.data

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import com.alilopez.kt_demohilt.core.hardware.domain.SoundManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidSoundManager @Inject constructor(
    @ApplicationContext private val context: Context
): SoundManager{
    private val soundManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun onPostSound() {
        try {
            val toneG = ToneGenerator(AudioManager.STREAM_ALARM, 100)
            toneG.startTone(ToneGenerator.TONE_PROP_ACK, 200)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun hasSpeaker(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)
    }
}
