package com.alilopez.kt_demohilt.core.hardware.domain

interface SoundManager {
    fun onPostSound()
    fun hasSpeaker(): Boolean
}