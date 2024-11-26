package com.example.wppdabia.ui.login

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wppdabia.data.UserData
import com.example.wppdabia.data.data_store.PreferencesManager
import com.example.wppdabia.network.Remote
import com.example.wppdabia.network.RemoteImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val preferencesManager: PreferencesManager) :
    ViewModel() {
    private var _capturedImageUri = MutableLiveData<Uri?>()
    val capturedImageUri: LiveData<Uri?> = _capturedImageUri

    private val remote: Remote = RemoteImpl()

    fun saveCapturedImage(uri: Uri) {
        _capturedImageUri.value = uri
    }

    suspend fun registerUser(
        userData: UserData,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        remote.registerUser(
            userData = userData,
            onSuccess = {
                onSuccess.invoke()
                viewModelScope.launch {
                    preferencesManager.saveIsRegistered(true)
                }
            },
            onError = {
                onError.invoke(it)
            }
        )
    }
}