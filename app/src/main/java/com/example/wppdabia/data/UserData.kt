package com.example.wppdabia.data

import android.net.Uri

data class UserData(
    val profileImageUrl: Uri? = null,
    val name: String = "",
    val email: String = "",
    val password: String = ""
)