package com.simplesoftware.wppdabia.data

data class MessageData(
    val recipientId: String = "",
    val sender: UserData = UserData(),
    val messageText: String = "",
    val messageImage: String? = null,
    val timestamp: String = "",
    val isSentByUser: Boolean = false,
    val lastMessage: String = "",
    var wasRead: Boolean = false,
    val audioUrl: String? = null,
    val audioDuration: Int? = null
)