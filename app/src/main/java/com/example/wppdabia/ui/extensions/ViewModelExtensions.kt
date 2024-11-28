package com.example.wppdabia.ui.extensions

import androidx.lifecycle.MutableLiveData
import com.example.wppdabia.data.UserData
import com.example.wppdabia.network.Remote

suspend fun getCurrentUser(currentUser: MutableLiveData<UserData?>, remote: Remote): UserData? {
    remote.getCurrentUser(
        onSuccess = { userData ->
            currentUser.value = userData
        },
        onError = {
            currentUser.value = null
        }
    )
    return currentUser.value
}