package com.example.wppdabia.ui.extensions

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream

fun String.getInitials(): String {
    val names = this.split(" ")
    val initials = names.mapNotNull { it.firstOrNull() }.joinToString("")
    return initials.uppercase().take(2)
}

fun String.getFirstName(): String {
    return this.split(" ").firstOrNull() ?: this
}

fun Bitmap.toUri(context: Context): Uri {
    val file = File(context.cacheDir, "temp_image.jpg")
    FileOutputStream(file).use { output ->
        this.compress(Bitmap.CompressFormat.JPEG, 100, output)
    }
    return file.toUri()
}