package com.alilopez.kt_demohilt.core.hardware.data

import android.content.Context
import android.media.AudioManager
import com.alilopez.kt_demohilt.core.hardware.domain.SoundManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidSoundManager @Inject constructor(
    @ApplicationContext private val context: Context
): SoundManager{
    private val soundManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun beep() {
        TODO("Not yet implemented")
    }

    override fun hasSpeaker(): Boolean {
        TODO("Not yet implemented")
    }
}
