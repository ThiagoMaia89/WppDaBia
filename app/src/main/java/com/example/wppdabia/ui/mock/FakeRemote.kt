package com.example.wppdabia.ui.mock

import com.example.wppdabia.data.ContactData
import com.example.wppdabia.data.MessageData
import com.example.wppdabia.data.UserData
import com.example.wppdabia.network.Remote
import kotlinx.coroutines.flow.Flow

val fakeRemote = object : Remote {
    override suspend fun registerUser(
        userData: UserData,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        onSuccess()
    }

    override fun loginUser(
        userData: UserData,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        onSuccess()
    }

    override suspend fun getAllContacts(
        onSuccess: (List<ContactData>) -> Unit,
        onError: (String) -> Unit
    ) { }

    override suspend fun getCurrentUser(onSuccess: (UserData?) -> Unit, onError: (String) -> Unit) {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(chatId: String, message: MessageData) {
        TODO("Not yet implemented")
    }

    override suspend fun fetchMessages(chatId: String): Flow<List<MessageData>> {
        TODO("Not yet implemented")
    }
}