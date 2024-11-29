package com.example.wppdabia.data

data class ContactData(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String? = null,
    val lastMessage: String = "",
    val timestamp: String = "",
    val chatId: String? = null
)