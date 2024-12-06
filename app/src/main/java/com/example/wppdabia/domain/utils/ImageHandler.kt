package com.example.wppdabia.domain.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.yalantis.ucrop.UCrop
import java.io.File

class ImageHandler(
    val onImageCaptured: (Bitmap) -> Unit,
    val onImageSelected: (Uri) -> Unit,
    val onImageCropped: (Uri) -> Unit,
    val onCropError: (Boolean) -> Unit
) {
    lateinit var cameraLauncher: ActivityResultLauncher<Void?>
    lateinit var galleryLauncher: ActivityResultLauncher<String>
    private lateinit var cropLauncher: ActivityResultLauncher<Intent>

    @Composable
    fun InitializeLaunchers() {
        cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->
            bitmap?.let { onImageCaptured(it) }
        }

        galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let { onImageSelected(it) }
        }

        cropLauncher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val croppedUri = UCrop.getOutput(result.data!!)
                    croppedUri?.let { onImageCropped(it) }
                } else {
                    onCropError.invoke(true)
                }
            }
    }

    @Composable
    fun RequestCameraPermission(
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit = {}
    ) {

        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }

        val context = LocalContext.current
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onPermissionGranted()
        } else {
            LaunchedEffect(Unit) {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    fun startCrop(context: Context, sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(context.cacheDir, "cropped_image.jpg"))

        val options = UCrop.Options().apply {
            setCircleDimmedLayer(true)
            setFreeStyleCropEnabled(false)
            setCompressionFormat(Bitmap.CompressFormat.PNG)
            setCompressionQuality(100)
        }

        val intent = UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .withAspectRatio(1f, 1f)
            .getIntent(context)

        cropLauncher.launch(intent)
    }
}