package com.example.wppdabia.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wppdabia.data.ContactData
import com.example.wppdabia.data.UserData
import com.example.wppdabia.repository.Repository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _lastMessageCardViews = MutableStateFlow<List<ContactData>>(emptyList())
    val lastMessageCardViews: StateFlow<List<ContactData>> = _lastMessageCardViews

    private var errorMessage = MutableLiveData<String>()

    private val _currentUser = MutableStateFlow<UserData?>(UserData())
    val currentUser: StateFlow<UserData?> = _currentUser

    init {
        viewModelScope.launch { getCurrentUser() }
        getMessages()
    }

    private suspend fun getCurrentUser() {
        repository.getCurrentUser(
            onSuccess = { userData ->
                _currentUser.value = userData
            },
            onError = {
                _currentUser.value = null
            }
        )
    }

    private fun getMessages() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        //_contactsLoading.value = true
        viewModelScope.launch {
            repository.getAllChats(
                currentUserUid = currentUserUid,
                onSuccess = { chatList ->
                    _lastMessageCardViews.value = chatList
                },
                onError = { error ->
                    errorMessage.value = error
                }
            )
        }
    }
}