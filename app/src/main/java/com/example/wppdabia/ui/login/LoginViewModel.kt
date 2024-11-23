package com.example.wppdabia.ui.login

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.imageLoader
import coil.request.ImageRequest
import com.example.wppdabia.data.model.CachedImage
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _cachedImage = MutableLiveData<CachedImage?>(null)
    val cachedImage: LiveData<CachedImage?> = _cachedImage

    var capturedImageBitmap: Bitmap? by mutableStateOf(null)
        private set

    var selectedImageUri: Uri? by mutableStateOf(null)
        private set

    fun saveCapturedImage(bitmap: Bitmap) {
        capturedImageBitmap = bitmap
    }

    fun saveSelectedImageUri(uri: Uri) {
        selectedImageUri = uri
    }


    fun saveImageToCache(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            val key = try {
                val request = ImageRequest.Builder(context)
                    .data(imageUri)
                    .target { drawable -> drawable.toBitmap().toString() }
                    .build()
                context.imageLoader.execute(request).request.data.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
                _cachedImage.value = CachedImage(
                    key = key,
                    uri = imageUri
                )
        }
    }

    fun getCachedImage(): CachedImage? {
        return _cachedImage.value
    }
}