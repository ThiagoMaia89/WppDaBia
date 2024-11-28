package com.example.wppdabia.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wppdabia.data.LastMessageCardViewData
import com.example.wppdabia.data.UserData
import com.example.wppdabia.network.Remote
import com.example.wppdabia.ui.extensions.getCurrentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val remote: Remote) : ViewModel() {
    private val _lastMessageCardViews = MutableLiveData<List<LastMessageCardViewData>>()
    val lastMessageCardViews: LiveData<List<LastMessageCardViewData>> = _lastMessageCardViews

    val currentUser = MutableLiveData<UserData?>()

    init {
        viewModelScope.launch { getCurrentUser(currentUser, remote) }
    }
}