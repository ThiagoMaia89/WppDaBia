package com.example.wppdabia.ui.login

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wppdabia.network.Remote
import com.example.wppdabia.network.RemoteImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

class LoginViewModel @Inject constructor() : ViewModel() {
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