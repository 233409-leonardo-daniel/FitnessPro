package com.alilopez.kt_demohilt.core.hardware.domain

import android.net.Uri
import java.io.File

interface CameraPhotoManager {
    fun createPhotoUri(): Uri
    fun getPhotoFile(): File?
    fun hasCamera(): Boolean
}

