package com.simplesoftware.wppdabia.repository

import com.simplesoftware.wppdabia.data.ContactData
import com.simplesoftware.wppdabia.data.MessageData
import com.simplesoftware.wppdabia.data.UserData
import kotlinx.coroutines.flow.Flow

interface Repository {

    suspend fun registerUser(userData: UserData, onSuccess: () -> Unit, onError: (String) -> Unit)

    suspend fun registerUserToken()

    fun loginUser(userData: UserData, onSuccess: () -> Unit, onError: (String) -> Unit)

    suspend fun getAllContacts(onSuccess: (List<ContactData>) -> Unit, onError: (String) -> Unit)

    suspend fun getCurrentUser(onSuccess: (UserData?) -> Unit, onError: (String) -> Unit)

    suspend fun sendMessage(
        chatId: String,
        message: MessageData,
        recipientId: String,
        onSuccess: () -> Unit,
        onAudioSuccess: () -> Unit,
        onError: () -> Unit,
        onTemporaryMessageAdded: (MessageData) -> Unit
    )

    suspend fun fetchMessages(chatId: String): Flow<List<MessageData>>

    suspend fun getAllChats(
        currentUserUid: String,
        onSuccess: (List<ContactData>) -> Unit,
        onError: (String) -> Unit
    )

    suspend fun updateProfileImage(
        newImageUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    suspend fun markMessagesAsRead(chatId: String, currentUserId: String)

    suspend fun deletePhoto(userId: String, onSuccess: () -> Unit, onError: (String) -> Unit)

    fun logout()
}