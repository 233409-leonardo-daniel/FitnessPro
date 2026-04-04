package com.alilopez.kt_demohilt.core.hardware.data

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.net.Uri
import androidx.core.content.FileProvider
import com.alilopez.kt_demohilt.core.hardware.domain.CameraPhotoManager
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import java.io.File

class AndroidCameraPhotoManager @Inject constructor(
    @ApplicationContext private val context: Context
) : CameraPhotoManager {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    private var currentPhotoFile: File? = null

    override fun createPhotoUri(): Uri {
        val photoFile = File.createTempFile(
            "recipe_${System.currentTimeMillis()}",
            ".jpg",
            context.cacheDir
        )
        currentPhotoFile = photoFile

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
    }

    override fun getPhotoFile(): File? {
        return currentPhotoFile?.takeIf { it.exists() && it.length() > 0 }
    }

    override fun copyGalleryImageToFile(uri: Uri): File? {
        return try {
            val tempFile = File.createTempFile(
                "gallery_${System.currentTimeMillis()}",
                ".jpg",
                context.cacheDir
            )
            context.contentResolver.openInputStream(uri)?.use { input ->
                tempFile.outputStream().use { output -> input.copyTo(output) }
            }
            if (tempFile.length() > 0) {
                currentPhotoFile = tempFile
                tempFile
            } else {
                tempFile.delete()
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun hasCamera(): Boolean {
        val hasFeature = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        val hasCameraDevice = try {
            cameraManager.cameraIdList.isNotEmpty()
        } catch (e: Exception) {
            false
        }
        return hasFeature && hasCameraDevice
    }
}

