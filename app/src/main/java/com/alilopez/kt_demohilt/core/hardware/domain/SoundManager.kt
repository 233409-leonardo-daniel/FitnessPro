package com.alilopez.kt_demohilt.core.hardware.domain

interface SoundManager {
    fun beep()
    fun hasSpeaker(): Boolean
}