package com.example.wppdabia.ui

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
class SharedViewModel @Inject constructor(
    private val remote: Remote,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserData?>(UserData())
    val currentUser: StateFlow<UserData?> = _currentUser

    private var _capturedImageUri = MutableLiveData<String?>()
    val capturedImageUri: LiveData<String?> = _capturedImageUri

    var logout = MutableLiveData(false)

    suspend fun getCurrentUser() {
        remote.getCurrentUser(
            onSuccess = { userData ->
                _currentUser.value = userData
            },
            onError = {
                _currentUser.value = null
            }
        )
    }

    fun saveCapturedImage(uri: Uri) {
        _capturedImageUri.value = uri.toString()
    }

    fun updateProfileImage(
        newImageUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            remote.updateProfileImage(
                newImageUrl,
                onSuccess = {
                    _currentUser.value = _currentUser.value?.copy(profileImageUrl = newImageUrl)
                    onSuccess.invoke()
                },
                onError = {
                    onError.invoke(it.message ?: "Erro")
                }
            )
        }
    }

    fun logout() {
        remote.logout()
        viewModelScope.launch { preferencesManager.saveIsRegistered(false) }
        logout.value = true
    }

    fun resetLogoutFlag() {
        logout.value = false
    }
}