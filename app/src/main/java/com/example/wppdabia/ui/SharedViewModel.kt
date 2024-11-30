package com.example.wppdabia.ui

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
): ViewModel() {

    private val _currentUser = MutableStateFlow<UserData?>(UserData())
    val currentUser: StateFlow<UserData?> = _currentUser

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

    fun logout() {
       remote.logout()
        viewModelScope.launch { preferencesManager.saveIsRegistered(false) }
        logout.value = true
    }

    fun resetLogoutFlag() {
        logout.value = false
    }
}