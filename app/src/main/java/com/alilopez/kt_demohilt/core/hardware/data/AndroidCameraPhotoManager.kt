package com.alilopez.kt_demohilt.core.hardware.data

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.camera2.CameraManager
import android.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import com.alilopez.kt_demohilt.core.hardware.domain.CameraPhotoManager
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import java.io.File
import java.io.FileOutputStream

class AndroidCameraPhotoManager @Inject constructor(
    @ApplicationContext private val context: Context
) : CameraPhotoManager {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    private var currentPhotoFile: File? = null
    private var currentPhotoNormalized = false

    override fun createPhotoUri(): Uri {
        val photoFile = File.createTempFile(
            "recipe_${System.currentTimeMillis()}",
            ".jpg",
            context.cacheDir
        )
        currentPhotoFile = photoFile
        currentPhotoNormalized = false

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
    }

    override fun getPhotoFile(): File? {
        val photoFile = currentPhotoFile?.takeIf { it.exists() && it.length() > 0 } ?: return null

        if (!currentPhotoNormalized) {
            val normalized = normalizePhotoOrientation(photoFile)
            if (!normalized) return null
            currentPhotoNormalized = true
        }

        return photoFile
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
                currentPhotoNormalized = false
                if (!normalizePhotoOrientation(tempFile)) {
                    tempFile.delete()
                    currentPhotoFile = null
                    return null
                }
                currentPhotoNormalized = true
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

    private fun normalizePhotoOrientation(photoFile: File): Boolean {
        return try {
            val exif = ExifInterface(photoFile.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            if (orientation == ExifInterface.ORIENTATION_NORMAL ||
                orientation == ExifInterface.ORIENTATION_UNDEFINED
            ) {
                return true
            }

            val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath) ?: return false
            val matrix = Matrix().apply {
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> postRotate(270f)
                    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> postScale(-1f, 1f)
                    ExifInterface.ORIENTATION_FLIP_VERTICAL -> postScale(1f, -1f)
                    ExifInterface.ORIENTATION_TRANSPOSE -> {
                        postRotate(90f)
                        postScale(-1f, 1f)
                    }
                    ExifInterface.ORIENTATION_TRANSVERSE -> {
                        postRotate(270f)
                        postScale(-1f, 1f)
                    }
                }
            }

            val rotatedBitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )

            if (rotatedBitmap != bitmap) {
                bitmap.recycle()
            }

            FileOutputStream(photoFile, false).use { output ->
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 95, output)
                output.flush()
            }
            rotatedBitmap.recycle()

            val updatedExif = ExifInterface(photoFile.absolutePath)
            updatedExif.setAttribute(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL.toString()
            )
            updatedExif.saveAttributes()
            true
        } catch (e: Exception) {
            false
        }
    }
}

