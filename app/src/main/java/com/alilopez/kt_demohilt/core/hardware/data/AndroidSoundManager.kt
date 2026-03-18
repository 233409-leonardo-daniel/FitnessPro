package com.alilopez.kt_demohilt.core.hardware.data

import android.content.Context
import android.content.pm.PackageManager
import android.media.RingtoneManager
import com.alilopez.kt_demohilt.core.hardware.domain.SoundManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidSoundManager @Inject constructor(
    @ApplicationContext private val context: Context
): SoundManager {
    override fun onPostSound() {
        try {
            val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, notificationUri)
            ringtone.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun hasSpeaker(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)
    }
}
