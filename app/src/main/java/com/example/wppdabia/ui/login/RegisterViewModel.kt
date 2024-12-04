package com.example.wppdabia.ui.login

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wppdabia.data.UserData
import com.example.wppdabia.data.data_store.PreferencesManager
import com.example.wppdabia.network.Remote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val preferencesManager: PreferencesManager, private val remote: Remote) :
    ViewModel() {
    private var _capturedImageUri = MutableLiveData<String?>()
    val capturedImageUri: LiveData<String?> = _capturedImageUri

    private val _loginLoading = MutableStateFlow(false)
    val loginLoading: StateFlow<Boolean> = _loginLoading

    private val _registerLoading = MutableStateFlow(false)
    val registerLoading: StateFlow<Boolean> = _registerLoading

    fun saveCapturedImage(uri: Uri) {
        _capturedImageUri.value = uri.toString()
    }

    suspend fun registerUser(
        userData: UserData,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        _registerLoading.value = true
        remote.registerUser(
            userData = userData,
            onSuccess = {
                _registerLoading.value = false
                onSuccess.invoke()
                viewModelScope.launch {
                    preferencesManager.saveIsRegistered(true)
                }
            },
            onError = {
                _registerLoading.value = false
                onError.invoke(it)
            }
        )
    }

    fun loginWithEmailAndPassword(
        userData: UserData,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        _loginLoading.value = true
        remote.loginUser(
            userData = userData,
            onSuccess = {
                _loginLoading.value = false
                onSuccess.invoke()
                viewModelScope.launch {
                    preferencesManager.saveIsRegistered(true)
                }
            },
            onError = {
                _loginLoading.value = false
                onError.invoke(it)
            }
        )
    }
}