package com.example.wppdabia.data

data class MessageData(
    val id: String = "",
    val sender: UserData = UserData(),
    val content: String = "",
    val timestamp: String = "",
    val isSentByUser: Boolean = false,
    val lastMessage: String = "",
    var wasRead: Boolean = false
)