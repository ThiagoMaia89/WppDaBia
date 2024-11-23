package com.example.wppdabia.domain.utils

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
import android.Manifest

class ImageHandler(
    val onImageCaptured: (Bitmap) -> Unit,
    val onImageSelected: (Uri) -> Unit
) {
    lateinit var cameraLauncher: ActivityResultLauncher<Void?>
    lateinit var galleryLauncher: ActivityResultLauncher<String>

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
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted()
        } else {
            LaunchedEffect(Unit) {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}