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
import com.example.wppdabia.network.Remote
import com.example.wppdabia.network.RemoteImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private var _capturedImageUri = MutableLiveData<Uri?>()
    val capturedImageUri: LiveData<Uri?> = _capturedImageUri

    private val remote: Remote = RemoteImpl()

    fun saveCapturedImage(uri: Uri) {
        _capturedImageUri.value = uri
    }

    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        profileImageUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        remote.registerUser(
            name = name,
            email = email,
            password = password,
            profileImageUri = profileImageUri,
            onSuccess = {
                onSuccess.invoke()
            },
            onError = {
                onError.invoke(it)
            }
        )
    }

}