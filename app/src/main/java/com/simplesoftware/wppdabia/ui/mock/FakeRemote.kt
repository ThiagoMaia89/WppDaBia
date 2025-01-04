package com.simplesoftware.wppdabia.ui.mock

import com.simplesoftware.wppdabia.data.ContactData
import com.simplesoftware.wppdabia.data.MessageData
import com.simplesoftware.wppdabia.data.UserData
import com.simplesoftware.wppdabia.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

val fakeRepository = object : Repository {
    override suspend fun registerUser(
        userData: UserData,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {}

    override suspend fun registerUserToken() {
        TODO("Not yet implemented")
    }

    override fun loginUser(
        userData: UserData,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {}

    override suspend fun getAllContacts(
        onSuccess: (List<ContactData>) -> Unit,
        onError: (String) -> Unit
    ) {}

    override suspend fun getCurrentUser(onSuccess: (UserData?) -> Unit, onError: (String) -> Unit) {}
    override suspend fun sendMessage(
        chatId: String,
        message: MessageData,
        recipientId: String,
        onSuccess: () -> Unit,
        onAudioSuccess: () -> Unit,
        onError: () -> Unit,
        onTemporaryMessageAdded: (MessageData) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun fetchMessages(chatId: String): Flow<List<MessageData>> {
        return flowOf()
    }

    override suspend fun getAllChats(
        currentUserUid: String,
        onSuccess: (List<ContactData>) -> Unit,
        onError: (String) -> Unit
    ) {}

    override suspend fun updateProfileImage(
        newImageUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {}

    override suspend fun markMessagesAsRead(chatId: String, currentUserId: String) {}

    override suspend fun deletePhoto(
        userId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {}

    override fun logout() {}
}