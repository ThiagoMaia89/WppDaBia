package com.example.wppdabia.ui.extensions

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream

fun String.getInitials(): String {
    val names = this.split(" ")
    val initials = names.mapNotNull { it.firstOrNull() }.joinToString("")
    return initials.uppercase().take(2)
}

fun Bitmap.toUri(context: Context): Uri {
    val bytes = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(
        context.contentResolver, this, "Title", null
    )
    return Uri.parse(path)
}