package com.alilopez.kt_demohilt.core.hardware.data

import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import com.alilopez.kt_demohilt.core.hardware.domain.MicrophoneManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class AndroidMicrophoneManager @Inject constructor(
    @ApplicationContext private val context: Context
) : MicrophoneManager {

    private var mediaRecorder: MediaRecorder? = null
    private var currentRecordingFile: File? = null
    private var recording = false

    override fun hasMicrophone(): Boolean =
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)

    override fun startRecording() {
        if (recording) return

        val audioFile = File.createTempFile(
            "audio_${System.currentTimeMillis()}",
            ".m4a",
            context.cacheDir
        )
        currentRecordingFile = audioFile

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)
            setOutputFile(audioFile.absolutePath)
            prepare()
            start()
        }

        recording = true
    }

    override fun stopRecording() {
        if (!recording) return

        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            currentRecordingFile?.delete()
            currentRecordingFile = null
        } finally {
            mediaRecorder = null
            recording = false
        }
    }

    override fun isRecording(): Boolean = recording

    override fun getRecordingFile(): File? {
        return currentRecordingFile?.takeIf { it.exists() && it.length() > 0 }
    }
}