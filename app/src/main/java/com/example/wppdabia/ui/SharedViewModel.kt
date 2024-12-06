package com.example.wppdabia.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wppdabia.data.UserData
import com.example.wppdabia.data.data_store.PreferencesManager
import com.example.wppdabia.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: Repository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserData?>(UserData())
    val currentUser: StateFlow<UserData?> = _currentUser

    private var _capturedImageUri = MutableLiveData<String?>()

    var errorMessage = MutableLiveData<String>()

    var logout = MutableLiveData(false)

    suspend fun getCurrentUser() {
        repository.getCurrentUser(
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
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            repository.updateProfileImage(
                newImageUrl,
                onSuccess = {
                    _currentUser.value = _currentUser.value?.copy(profileImageUrl = newImageUrl)
                },
                onError = {
                    onError.invoke(it)
                }
            )
        }
    }

    fun deleteProfilePhoto() {
        viewModelScope.launch {
            _currentUser.value?.uid?.let { userId ->
                repository.deletePhoto(
                    userId = userId,
                    onSuccess = {
                        _currentUser.value = _currentUser.value?.copy(profileImageUrl = null)
                        _capturedImageUri.value = null
                    },
                    onError = { errorMessage.value = it }
                )
            }
        }
    }

    fun logout() {
        repository.logout()
        viewModelScope.launch { preferencesManager.saveIsRegistered(false) }
        logout.value = true
    }

    fun resetLogoutFlag() {
        logout.value = false
    }
}