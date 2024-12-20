package com.simplesoftware.wppdabia.data

data class UserData(
    val uid: String = "",
    var profileImageUrl: String? = null,
    val name: String = "",
    val email: String = "",
    val password: String = ""
)