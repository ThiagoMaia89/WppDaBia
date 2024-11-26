package com.example.wppdabia.network

import android.net.Uri
import com.example.wppdabia.data.UserData

interface Remote {

    suspend fun registerUser(
        userData: UserData,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
}