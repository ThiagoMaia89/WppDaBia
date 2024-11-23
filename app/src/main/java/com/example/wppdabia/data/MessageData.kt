package com.example.wppdabia.data

data class MessageData(
    val sender: String,
    val content: String,
    val timestamp: String,
    val isSentByUser: Boolean
)