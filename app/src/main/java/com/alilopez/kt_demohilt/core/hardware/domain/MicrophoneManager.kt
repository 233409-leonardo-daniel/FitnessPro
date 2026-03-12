package com.alilopez.kt_demohilt.core.hardware.domain

import java.io.File

interface MicrophoneManager {
    fun hasMicrophone(): Boolean
    fun startRecording()
    fun stopRecording()
    fun isRecording(): Boolean
    fun getRecordingFile(): File?
}

