package com.example.wppdabia.network

import android.net.Uri

interface Remote {

    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        profileImageUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
}