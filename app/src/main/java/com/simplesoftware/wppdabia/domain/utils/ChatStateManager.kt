package com.simplesoftware.wppdabia.domain.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatStateManager @Inject constructor() {
    private val _activeChatUserId = MutableLiveData<String?>()
    val activeChatUserId: LiveData<String?> = _activeChatUserId

    fun setActiveChatUserId(userId: String?) {
        _activeChatUserId.value = userId
    }
}